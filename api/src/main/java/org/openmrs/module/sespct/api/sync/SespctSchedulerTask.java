package org.openmrs.module.sespct.api.sync;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.sespct.api.PedidoService;
import org.openmrs.module.sespct.api.builder.ObsBuilder;
import org.openmrs.module.sespct.api.model.Pedido;
import org.openmrs.module.sespct.api.util.Constants;
import org.openmrs.module.sespct.api.util.DateTimeUtils;
import org.openmrs.module.sespct.api.util.EncounterUtils;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

/*
 * Scheduled task for synchronizing SESP-CT pedidos and creating corresponding encounters.
 */
public class SespctSchedulerTask extends AbstractTask {
	
	private static final Logger log = LoggerFactory.getLogger(SespctSchedulerTask.class);
	
	private User user;
	
	private Provider provider;
	
	private PedidoService pedidoService;
	
	@Override
	public void execute() {
		log.info("Starting SESP-CT scheduled task...");
		
		try {
			if (!initializeRequiredServices()) {
				log.error("Failed to initialize required services. Task aborted.");
				return;
			}
			
			processPedidos();
			
		}
		catch (Exception e) {
			log.error("Unexpected error while executing SESP-CT scheduled task", e);
		}
	}
	
	private boolean initializeRequiredServices() {
		try {
			this.user = findSystemUser();
			if (this.user == null) {
				log.error("No valid system user found for task execution");
				return false;
			}
			
			this.provider = findProviderForUser(this.user);
			if (this.provider == null) {
				log.error("No provider found for user: {}", this.user.getUsername());
				return false;
			}
			
			this.pedidoService = Context.getService(PedidoService.class);
			if (this.pedidoService == null) {
				log.error("PedidoService not available");
				return false;
			}
			
			return true;
			
		}
		catch (Exception e) {
			log.error("Error during service initialization", e);
			return false;
		}
	}
	
	private User findSystemUser() {
		User user = Context.getUserService().getUserByUsername(Constants.PRIMARY_USER);
		if (user == null) {
			log.warn("Primary user '{}' not found, trying fallback user", Constants.PRIMARY_USER);
			user = Context.getUserService().getUserByUsername(Constants.FALLBACK_USER);
		}
		return user;
	}
	
	private Provider findProviderForUser(User user) {
		Collection<Provider> providers = Context.getProviderService().getProvidersByPerson(user.getPerson());
		return providers.isEmpty() ? null : providers.iterator().next();
	}
	
	private void processPedidos() {
		List<Pedido> pedidos = pedidoService.getPedidosByEstado(Constants.ESTADO_SEM_RESPOSTA);
		log.info("Found {} pedidos with status '{}'", pedidos.size(), Constants.ESTADO_SEM_RESPOSTA);
		
		if (pedidos.isEmpty()) {
			log.info("No pending pedidos to process");
			return;
		}
		
		int processedCount = 0;
		int errorCount = 0;
		
		for (Pedido pedido : pedidos) {
			try {
				if (processSinglePedido(pedido)) {
					processedCount++;
				} else {
					errorCount++;
				}
			}
			catch (Exception e) {
				log.error("Unexpected error processing Pedido id={}", pedido.getId(), e);
				errorCount++;
			}
		}
		
		log.info("Processing completed. Successful: {}, Errors: {}", processedCount, errorCount);
	}
	
	private boolean processSinglePedido(Pedido pedido) {
		if (pedido == null || pedido.getDadosUtente() == null) {
			log.warn("Invalid pedido or missing patient data for Pedido id={}", pedido != null ? pedido.getId() : "null");
			return false;
		}
		
		String identifier = pedido.getDadosUtente().getNid();
		if (identifier == null || identifier.trim().isEmpty()) {
			log.warn("Empty or null NID for Pedido id={}", pedido.getId());
			return false;
		}
		
		try {
			Optional<Patient> patient = findPatientByIdentifier(pedido, identifier);
			
			if (!patient.isPresent()) {
				handlePatientNotFound(pedido, identifier);
				return false;
			}
			
			// Aqui entra a verificação para não duplicar encounters
			Encounter existing = EncounterUtils.findEncounterByPedidoId(pedido.getPedidoId());
			if (existing != null) {
				log.warn("Encounter já existe para Pedido id={}, não vamos criar duplicado", pedido.getPedidoId());
				return false;
			}
			
			createEncounterForPedido(pedido, patient.get());
			log.info("Successfully processed Pedido id={} for Patient NID={}", pedido.getId(), identifier);
			return true;
			
		}
		catch (APIException e) {
			log.error("API error processing Pedido id={}", pedido.getId(), e);
			return false;
		}
		catch (DataIntegrityViolationException e) {
			log.error("Data integrity violation for Pedido id={}", pedido.getId(), e);
			return false;
		}
		catch (Exception e) {
			log.error("Unexpected error processing Pedido id={}", pedido.getId(), e);
			return false;
		}
	}
	
	private Optional<Patient> findPatientByIdentifier(Pedido pedido, String identifier) {
		List<Patient> patients = Context.getPatientService().getPatients(null, identifier, null, true);
		
		if (patients == null || patients.isEmpty()) {
			log.warn("No patient found with NID={} for Pedido id={}", identifier, pedido.getId());
			try {
				pedido.setEstado(Constants.PEDIDO_STATUS_NOT_PROCESSED);
				pedido.setCausa(Constants.PEDIDO_STATUS_PATIENT_NOT_FOUND);
				pedidoService.savePedido(pedido);
				log.info("Pedido id={} marcado como PATIENT_NOT_FOUND", pedido.getId());
			}
			catch (Exception e) {
				log.error("Erro ao atualizar estado do Pedido id={} (NOT_FOUND)", pedido.getId(), e);
			}
			return Optional.empty();
		}
		
		if (patients.size() > 1) {
			log.warn("Duplicate NID detected ({} patients) for Pedido id={}", patients.size(), pedido.getId());
			try {
				pedido.setEstado(Constants.PEDIDO_STATUS_NOT_PROCESSED);
				pedido.setCausa(Constants.PEDIDO_STATUS_DUPLICATE_NID);
				pedidoService.savePedido(pedido);
				log.info("Pedido id={} marcado como DUPLICATE_NID", pedido.getId());
			}
			catch (Exception e) {
				log.error("Erro ao atualizar estado do Pedido id={} (DUPLICATE_NID)", pedido.getId(), e);
			}
			return Optional.empty();
		}
		
		// Caso normal: apenas 1 paciente encontrado
		Patient patient = patients.get(0);
		log.debug("Found Patient id={} for Pedido id={}", patient.getPatientId(), pedido.getId());
		
		return Optional.of(patient);
	}
	
	private void handlePatientNotFound(Pedido pedido, String identifier) {
		log.warn("No patient found for Pedido id={}", pedido.getId());
		
		try {
			pedido.setEstado(Constants.PEDIDO_STATUS_NOT_PROCESSED);
			pedido.setCausa(Constants.PEDIDO_STATUS_PATIENT_NOT_FOUND);
			pedidoService.savePedido(pedido);
			log.info("Marked Pedido id={} as patient not found", pedido.getId());
		}
		catch (Exception e) {
			log.error("Failed to update status for Pedido id={}", pedido.getId(), e);
		}
	}
	
	private void createEncounterForPedido(Pedido pedido, Patient patient) {
		log.debug("Creating encounter for Patient id={}, Pedido id={}", patient.getPatientId(), pedido.getId());
		
		Encounter encounter = buildEncounter(pedido, patient);
		
		addClinicalObservations(encounter, pedido, patient);
		
		Context.getEncounterService().saveEncounter(encounter);
	}
	
	private void addClinicalObservations(Encounter encounter, Pedido pedido, Patient patient) {
		ObsBuilder obsBuilder = new ObsBuilder(encounter, patient);
		
		// Dados do utente
		obsBuilder.addTextObs(Constants.INICIAIS_UTENTE_UUID, pedido.getDadosUtente().getIniciais().trim());
		obsBuilder.addNumericObs(Constants.PESO_UUID, pedido.getDadosUtente().getPeso());
		obsBuilder.addBooleanObs(Constants.GESTANTE_UUID, pedido.getDadosUtente().isGestante());
		obsBuilder.addBooleanObs(Constants.LACTANTE_UUID, pedido.getDadosUtente().isLactante());
		obsBuilder.addNumericObs(Constants.ID_PEDIDO_UUID,
		    pedido.getPedidoId() != null ? Double.valueOf(pedido.getPedidoId()) : null);
		obsBuilder.addEstadioOmsObs(pedido.getDadosUtente().getEstadioOms().trim());
		obsBuilder.addTextObs(Constants.ESTADIO_OMS_MOTIVO, pedido.getDadosUtente().getEstadioOmsMotivo().trim());
		
		// Historia TARV
		/*if (pedido.getHistoriaTarv() != null) {
		    pedido.getHistoriaTarv().forEach(regime -> {
		        obsBuilder.addTarvRegimeObs(
		        		DateTimeUtils.toDate(regime.getDataInicio()),
		        		DateTimeUtils.toDate(regime.getDataTermino()),
		        		regime.getEsquemaTarv());
		    });
		}*/
		
		// Historia Laboratorial CD4
		/*if (pedido.getDadosLaboratorioCD4() != null) {
		    pedido.getDadosLaboratorioCD4().forEach(cd4 -> {
		        obsBuilder.addCd4Obs(
		            DateTimeUtils.toDate(cd4.getData()), 
		            cd4.getCd4().doubleValue(),
		            cd4.getCd4Percentagem()
		        );
		    });
		}*/
		
		// Historia Laboratorial Carga Viral
		/*if (pedido.getDadosLaboratorioCargaViral() != null) {
		    pedido.getDadosLaboratorioCargaViral().forEach(cv -> {
		        Double valor = cv.getCargaViral() != null ? cv.getCargaViral().doubleValue() : null;

		        obsBuilder.addCargaViralObs(
		            DateTimeUtils.toDate(cv.getData()), 
		            valor
		        );
		    });
		}*/
		
		// Resumo História Clínica
		/*obsBuilder.addTextObs(Constants.HISTORIA_CLINICA_UUID, pedido.getReportarFalencia().getHistoriaClinica());
		obsBuilder.addTextObs(Constants.HISTORIA_ADESAO_UUID, pedido.getReportarFalencia().getHistoriaAdesao());
		obsBuilder.addBooleanObs(Constants.TRATAMENTO_TB_UUID, pedido.getReportarFalencia().isEmTratamentoTb());*/
		
		// Dados do clínico
		/*obsBuilder.addTextObs(Constants.CLINICO_NOME_UUID, pedido.getDadosClinico().getNome());
		obsBuilder.addTextObs(Constants.CLINICO_TELEFONE_UUID, pedido.getDadosClinico().getTelefone());
		obsBuilder.addTextObs(Constants.CLINICO_EMAIL_UUID, pedido.getDadosClinico().getEmail());
		
		if (pedido.getDadosClinico().getCategoriaProfissional() != null) {
		    String categoria = pedido.getDadosClinico().getCategoriaProfissional().trim().toUpperCase();
		    switch (categoria) {
		        case "MEDICO":
		            obsBuilder.addCodedObs(Constants.CLINICO_CATEGORIA_UUID, Constants.CATEGORIA_MEDICO_UUID);
		            break;
		        case "TECNICO DE MEDICINA GERAL":
		            obsBuilder.addCodedObs(Constants.CLINICO_CATEGORIA_UUID, Constants.CATEGORIA_TECNICO_GERAL_UUID);
		            break;
		        case "ENFERMEIRO/A DE SMI":
		            obsBuilder.addCodedObs(Constants.CLINICO_CATEGORIA_UUID, Constants.CATEGORIA_ENFERMEIRO_SMI_UUID);
		            break;
		        case "TECNICO FARMACEUTICO":
		            obsBuilder.addCodedObs(Constants.CLINICO_CATEGORIA_UUID, Constants.CATEGORIA_FARMACEUTICO_UUID);
		            break;
		        default:
		            log.warn("Categoria profissional desconhecida: {}", categoria);
		    }
		}*/
		
		// Linha Solicitada
		//obsBuilder.addLinhaSolicitadaObs(pedido.getLinhaSolicitada().getLinha());
		
		// Resposta do Comité (estado inicial do Pedido)
		/*obsBuilder.addRespostaComiteObs(pedido.getEstado(), // SEM_RESPOSTA
		    null, // sem linha ainda
		    null, // sem comentário
		    DateTimeUtils.toDate(pedido.getDataSubmissao()), // data submissão
		    null // sem autor
				);*/
	}
	
	private Encounter buildEncounter(Pedido pedido, Patient patient) {
		Encounter encounter = new Encounter();
		encounter.setPatient(patient);
		if (pedido.getDataSubmissao() != null) {
			encounter.setEncounterDatetime(DateTimeUtils.toDate(pedido.getDataSubmissao()));
		}
		
		EncounterType encounterType = Context.getEncounterService().getEncounterTypeByUuid(Constants.CT_ENCOUNTER_TYPE);
		
		if (encounterType == null) {
			throw new APIException("CT encounter type not found with UUID: " + Constants.CT_ENCOUNTER_TYPE);
		}
		
		encounter.setEncounterType(encounterType);
		
		Location location = Context.getLocationService().getDefaultLocation();
		if (location == null) {
			throw new APIException("No default location configured");
		}
		encounter.setLocation(location);
		
		EncounterRole encounterRole = Context.getEncounterService().getEncounterRoleByUuid(
		    EncounterRole.UNKNOWN_ENCOUNTER_ROLE_UUID);
		
		if (encounterRole == null) {
			throw new APIException("Unknown encounter role not found");
		}
		
		encounter.setProvider(encounterRole, this.provider);
		
		Form form = Context.getFormService().getFormByUuid(Constants.SESPCT_FORM_UUID);
		if (form != null) {
			encounter.setForm(form);
			log.debug("Form {} set on Encounter for Pedido id={}", form.getName(), pedido.getPedidoId());
		} else {
			log.warn("No form found with UUID={} while creating Encounter for Pedido id={}", Constants.SESPCT_FORM_UUID,
			    pedido.getPedidoId());
		}
		
		return encounter;
	}
}
