package org.openmrs.module.sespct.api.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.sespct.api.MiddlewareApiService;
import org.openmrs.module.sespct.api.PedidoService;
import org.openmrs.module.sespct.api.dao.PedidoDao;
import org.openmrs.module.sespct.api.dao.RespostaDao;
import org.openmrs.module.sespct.api.dto.MetadadosPedidoDTO;
import org.openmrs.module.sespct.api.dto.PedidoDTO;
import org.openmrs.module.sespct.api.dto.RespostaDTO;
import org.openmrs.module.sespct.api.model.Pedido;
import org.openmrs.module.sespct.api.model.Resposta;
import org.openmrs.module.sespct.api.util.SespctMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

public class PedidoServiceImpl extends BaseOpenmrsService implements PedidoService {
	
	private static final Logger log = LoggerFactory.getLogger(PedidoServiceImpl.class);
	
	@Autowired
	private PedidoDao pedidoDao;
	
	@Autowired
	private RespostaDao respostaDao;
	
	@Autowired
	private MiddlewareApiService middlewareApiService;
	
	@Autowired
	private MessageSourceService messageSourceService;
	
	public static final String NID_SCT = "ac75ec91-bc27-4681-97d0-7db08937b2d7";
	
	public void setPedidoDao(PedidoDao pedidoDao) {
		this.pedidoDao = pedidoDao;
	}
	
	public void setRespostaDao(RespostaDao respostaDao) {
		this.respostaDao = respostaDao;
	}
	
	@Override
	@Transactional
	public Pedido savePedido(Pedido pedido) {
		return pedidoDao.savePedido(pedido);
	}
	
	@Override
	@Transactional
	public Resposta saveResposta(Resposta resposta) {
		return pedidoDao.saveResposta(resposta);
	};
	
	@Override
	@Transactional(readOnly = true)
	public Pedido getPedidoById(Integer id) {
		return pedidoDao.getPedidoById(id);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Pedido getPedidoByExternalId(String externalId) {
		return pedidoDao.getPedidoByExternalId(externalId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Pedido> getAllPedidos() {
		return pedidoDao.getAllPedidos();
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Pedido> getPedidosByEstado(List<String> estado) {
		return pedidoDao.getPedidosByEstado(estado);
	}
	
	@Override
	@Transactional
	public void deletePedido(Pedido pedido) {
		pedidoDao.deletePedido(pedido);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Pedido> searchPedidos(LocalDate startDate, LocalDate endDate, String estado, String ncft, String nid,
	        String usCode) {
		// Convert LocalDate to LocalDateTime for the database query
		// Start date becomes the very beginning of that day (00:00:00)
		LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
		
		// End date becomes the very end of that day (23:59:59) to include all records on that day
		LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(23, 59, 59) : null;
		
		// The service's job is done, pass the prepared data to the DAO
		return pedidoDao.searchPedidos(startDateTime, endDateTime, estado, ncft, nid, usCode);
	}
	
	// And implement it in your PedidoServiceImpl
	@Override
	@Transactional
	public List<Pedido> getPedidosByDateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		// The date range adjustment is now handled in the controller.
		// This method becomes a clean, direct pass-through to the DAO.
		return pedidoDao.getPedidosByDateTimeRange(startDateTime, endDateTime);
	}
	
	@Override
	public List<Resposta> getRespostasPendentes() {
		return respostaDao.getRespostasPendentes();
	}
	
	private String getRandomElement(String[] array, Random rand) {
		return array[rand.nextInt(array.length)];
	}
	
	/**
	 * FIXED: Refactored to accept a Random instance instead of creating a new one.
	 */
	private LocalDateTime getRandomLocalDateTime(Random rand, int startYear, int endYear) {
		int year = startYear + rand.nextInt(endYear - startYear + 1);
		int month = rand.nextInt(12) + 1; // 1-12 for java.time
		int day = rand.nextInt(28) + 1; // 1-28 to be safe for all months
		int hour = rand.nextInt(24); // 0-23
		int minute = rand.nextInt(60); // 0-59
		int second = rand.nextInt(60); // 0-59
		
		return LocalDateTime.of(year, month, day, hour, minute, second);
	}
	
	@Override
	@Transactional
	public Patient mapIdentifier(String patientUuid, Pedido pedido) throws IllegalArgumentException, IllegalStateException {
		log.debug("Starting mapIdentifier with patientUuid: {}", patientUuid);
		
		// Enhanced validation
		validateMappingInputs(patientUuid, pedido);
		
		// Validate pedido state
		validatePedidoState(pedido);
		
		// Get and validate patient
		Patient patient = getAndValidatePatient(patientUuid);
		
		// Handle NID identifier mapping
		handleNidIdentifierMapping(patient, pedido);
		
		log.debug("Successfully completed mapIdentifier for patient: {}", patient.getPatientId());
		return patient;
	}
	
	private void validateMappingInputs(String patientUuid, Pedido pedido) {
		if (StringUtils.isBlank(patientUuid)) {
			throw new IllegalArgumentException("Patient UUID cannot be null or empty");
		}
		
		if (pedido == null) {
			throw new IllegalArgumentException("Pedido cannot be null");
		}
		
		if (pedido.getDadosUtente() == null) {
			throw new IllegalArgumentException("DadosUtente cannot be null");
		}
		
		String nid = pedido.getDadosUtente().getNid();
		if (StringUtils.isBlank(nid)) {
			throw new IllegalArgumentException("NID cannot be null or empty");
		}
		
		log.debug("Input validation passed - NID: {}", nid);
	}
	
	private void validatePedidoState(Pedido pedido) {
		String estado = pedido.getEstado();
		String causa = pedido.getCausa();
		
		log.debug("Validating pedido state - Estado: {}, Causa: {}", estado, causa);
		
		if (!Pedido.ESTADO_NAO_PROCESSADO.equals(estado)) {
			throw new IllegalStateException(String.format(
			    "Pedido is not in a valid state for mapping. Current state: %s, Cause: %s", estado, causa));
		}
	}
	
	private Patient getAndValidatePatient(String patientUuid) {
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatientByUuid(patientUuid);
		
		if (patient == null) {
			throw new IllegalArgumentException("Patient not found with UUID: " + patientUuid);
		}
		
		log.debug("Patient found: {}", patient.getPatientId());
		return patient;
	}
	
	private void handleNidIdentifierMapping(Patient patient, Pedido pedido) {
		PatientService patientService = Context.getPatientService();
		String nid = pedido.getDadosUtente().getNid();
		
		// Get NID identifier type
		PatientIdentifierType nidIdentifierType = patientService.getPatientIdentifierTypeByUuid(NID_SCT);
		if (nidIdentifierType == null) {
			throw new IllegalStateException("NID SCT identifier type not found");
		}
		
		// Check if patient already has NID identifier
		if (!hasNidIdentifier(patient, nidIdentifierType)) {
			createAndSaveNidIdentifier(patient, nid, nidIdentifierType);
			
			// Reschedule result in separate transaction to avoid rollback issues
			try {
				rescheduleResultAsync(pedido.getId());
			}
			catch (Exception e) {
				log.error("Failed to reschedule result for pedido: {}", pedido.getId(), e);
				// Don't fail the main operation if rescheduling fails
			}
		} else {
			log.debug("Patient already has NID identifier, skipping creation");
		}
	}
	
	private boolean hasNidIdentifier(Patient patient, PatientIdentifierType nidIdentifierType) {
		List<PatientIdentifier> activeIdentifiers = patient.getActiveIdentifiers();
		
		if (activeIdentifiers == null || activeIdentifiers.isEmpty()) {
			log.debug("No active identifiers found for patient");
			return false;
		}
		
		for (PatientIdentifier identifier : activeIdentifiers) {
			if (nidIdentifierType.equals(identifier.getIdentifierType())) {
				return true;
			}
		}
		
		return false;
	}
	
	private void createAndSaveNidIdentifier(Patient patient, String nid, PatientIdentifierType nidIdentifierType) {
		PatientService patientService = Context.getPatientService();
		
		Location location = determineLocation(patient);
		if (location == null) {
			throw new IllegalStateException("Could not determine location for patient identifier");
		}
		
		PatientIdentifier patientIdentifier = new PatientIdentifier();
		patientIdentifier.setPatient(patient);
		patientIdentifier.setIdentifier(nid);
		patientIdentifier.setIdentifierType(nidIdentifierType);
		patientIdentifier.setLocation(location);
		patientService.savePatientIdentifier(patientIdentifier);
		log.debug("PatientIdentifier saved successfully");
	}
	
	private Location determineLocation(Patient patient) {
		LocationService locationService = Context.getLocationService();
		
		// Try to get location from patient's existing identifiers
		List<PatientIdentifier> activeIdentifiers = patient.getActiveIdentifiers();
		if (activeIdentifiers != null && !activeIdentifiers.isEmpty()) {
			for (PatientIdentifier identifier : activeIdentifiers) {
				Location location = identifier.getLocation();
				if (location != null) {
					log.debug("Using location from existing identifier: {}", location.getName());
					return location;
				}
			}
		}
		
		// Fallback to default location (could be configurable)
		Location defaultLocation = getDefaultLocation(locationService);
		if (defaultLocation != null) {
			log.debug("Using default location: {}", defaultLocation.getName());
			return defaultLocation;
		}
		
		return null;
	}
	
	private Location getDefaultLocation(LocationService locationService) {
		// This should ideally be configurable via global property
		String defaultLocationUuid = Context.getAdministrationService().getGlobalProperty("sespct.default.location.uuid");
		
		if (StringUtils.isNotBlank(defaultLocationUuid)) {
			return locationService.getLocationByUuid(defaultLocationUuid);
		}
		
		// Ultimate fallback
		List<Location> allLocations = locationService.getAllLocations(false);
		return allLocations.isEmpty() ? null : allLocations.get(0);
	}
	
	@Async
	public void rescheduleResultAsync(Integer pedidoId) {
		if (pedidoId == null) {
			log.warn("Cannot reschedule result: pedido ID is null");
			return;
		}
		
		try {
			Pedido pedido = getPedidoById(pedidoId);
			if (pedido != null) {
				pedido.setEstado(Pedido.ESTADO_SEM_RESPOSTA);
				savePedido(pedido);
				log.debug("Pedido {} rescheduled successfully", pedidoId);
			} else {
				log.warn("Could not reschedule: pedido {} not found", pedidoId);
			}
		}
		catch (Exception e) {
			log.error("Error rescheduling pedido {}", pedidoId, e);
		}
	}
	
	@Override
	@Transactional
	public void synchronizeMiddlewareData() {
		log.info("Starting SESP-CT middleware synchronization...");
		
		// Step 1: Login to the API to get an auth token
		log.info("Fetching Auth token");
		String authToken = middlewareApiService.login();
		if (authToken == null) {
			log.error("Synchronization failed: Could not log in to the middleware.");
			// You could throw an exception here to notify the controller
			throw new RuntimeException("Authentication with middleware failed.");
		}
		
		// Step 2: Fetch and process new Pedidos
		List<PedidoDTO> pedidoDtos = middlewareApiService.fetchPedidos(authToken);
		if (!pedidoDtos.isEmpty()) {
			List<String> newPedidoUuids = processPedidos(pedidoDtos);
			
			//Step 2b: Mark Pedidos as consumed
			if (!newPedidoUuids.isEmpty()) {
				middlewareApiService.markPedidosAsConsumed(newPedidoUuids, authToken);
			}
		} else {
			log.info("No new pedidos to synchronize.");
		}
		
		// Step 3: Fetch and process new Respostas
		List<RespostaDTO> respostaDtos = middlewareApiService.fetchRespostas(authToken);
		if (!respostaDtos.isEmpty()) {
			List<String> newRespostaIds = processRespostas(respostaDtos);
			
			// Step 3b: Mark Respostas as consumed
			if (!newRespostaIds.isEmpty()) {
				middlewareApiService.markRespostasAsConsumed(newRespostaIds, authToken);
			}
		} else {
			log.info("No new respostas to synchronize.");
		}
		
		log.info("Middleware synchronization finished.");
	}
	
	private List<String> processPedidos(List<PedidoDTO> dtos) {
		log.info("Processing " + dtos.size() + " fetched pedidos...");
		List<String> newlySavedPedidoUUIds = new ArrayList<>();

		for (PedidoDTO dto : dtos) {
			// 1. Get the metadata object into a variable first
			MetadadosPedidoDTO meta = dto.getMetadadosPedidoDTO();
			// 2. Add a null check to ensure the metadata and its ID exist
			if (meta == null || meta.getPedidoId() == null) {
				log.error("Skipping a PedidoDTO because its metadata or pedidoId is missing. DTO: " + dto);
				continue; // This skips the current loop iteration and moves to the next DTO
			}
			
			// 3. Now that we know 'meta' and 'meta.getPedidoId()' are not null, the rest of the code is safe.
			// IDEMPOTENCY CHECK: Only save if it's a new pedido
			if (!pedidoDao.doesPedidoExist(meta.getPedidoId())) {
				Pedido newPedido = SespctMapper.toPedidoEntity(dto);
				if (newPedido != null) {
					pedidoDao.savePedido(newPedido);
					log.info("Saved new pedido with ID: " + newPedido.getPedidoId());
					newlySavedPedidoUUIds.add(dto.getUuid());
				}
			} else {
				log.warn("Skipping already existing pedido with ID: " + meta.getPedidoId());
			}
		}
		return newlySavedPedidoUUIds;
	}
	
	private List<String> processRespostas(List<RespostaDTO> dtos) {
		log.info("Processing " + dtos.size() + " fetched respostas...");
		List<String> newlySavedRespostaUUIds = new ArrayList<>();

		// Track which pedidos we've updated so we can update their estado at the end
		Set<Integer> updatedPedidoIds = new HashSet<>();

		for (RespostaDTO dto : dtos) {
			Integer externalPedidoId = dto.getPedidoId();
			Integer externalRespostaId = dto.getRespostaId();

			if (externalRespostaId == null) {
				log.warn("Skipping a resposta with a null external ID. UUID: {}", dto.getUuid());
				continue;
			}

			// IDEMPOTENCY CHECK: Only save if it's a new resposta
			if (!pedidoDao.doesRespostaExist(String.valueOf(externalRespostaId))) {
				Pedido parentPedido = pedidoDao.getPedidoByExternalId(String.valueOf(externalPedidoId));

				if (parentPedido != null) {
					Resposta newResposta = SespctMapper.toRespostaEntity(dto, parentPedido);
					pedidoDao.saveResposta(newResposta);
					parentPedido.getRespostas().add(newResposta);

					// Add the outer UUID to the list to be marked as consumed
					newlySavedRespostaUUIds.add(dto.getUuid());
					log.info("Saved new resposta {} for pedido {}", externalRespostaId, externalPedidoId);

					// Track that this pedido needs estado update
					updatedPedidoIds.add(parentPedido.getId());
				} else {
					log.warn("Could not find parent pedido with ID: {}. Cannot save resposta {}", externalPedidoId, externalRespostaId);
				}
			} else {
				log.warn("Skipping already existing resposta with ID: " + externalRespostaId);
			}
		}

		// Now update the estado for all affected pedidos based on their LATEST resposta
		for (Integer pedidoId : updatedPedidoIds) {
			Pedido pedido = pedidoDao.getPedidoById(pedidoId);
			if (pedido != null) {
				updatePedidoEstadoFromLatestResposta(pedido);
				pedidoDao.savePedido(pedido);
			}
		}

		return newlySavedRespostaUUIds;
	}
	
	// Helper method to update pedido estado based on the latest resposta
	private void updatePedidoEstadoFromLatestResposta(Pedido pedido) {
		List<Resposta> allRespostas = pedido.getRespostas();

		if (allRespostas == null || allRespostas.isEmpty()) {
			return;
		}

		// Find the resposta with the latest timestamp
		Resposta latestResposta = allRespostas.stream()
				.filter(r -> r.getTimestamp() != null)
				.max(Comparator.comparing(Resposta::getTimestamp))
				.orElse(null);

		if (latestResposta == null) {
			log.warn("No respostas with timestamps found for pedido {}", pedido.getPedidoId());
			return;
		}

		String respostaTexto = latestResposta.getResposta();
		log.info("Updating pedido {} estado based on latest resposta (timestamp: {}): '{}'",
				pedido.getPedidoId(), latestResposta.getFormattedTimestamp(), respostaTexto);

		if (respostaTexto != null && !respostaTexto.trim().isEmpty()) {
			String[] words = respostaTexto.trim().split(" +");
			String firstWord = words[0];

			// Compare only the first word, ignoring case
			if ("aprovado".equalsIgnoreCase(firstWord)) {
				pedido.setEstado(Pedido.ESTADO_APROVADO);
				log.info("Set pedido {} estado to APROVADO", pedido.getPedidoId());
			} else if ("adiado".equalsIgnoreCase(firstWord)) {
				pedido.setEstado(Pedido.ESTADO_ADIADO);
				log.info("Set pedido {} estado to ADIADO", pedido.getPedidoId());
			}
		}
	}
	
	@Override
	public List<Resposta> getRespostasByPedidoId(Integer pedidoId) {
		return respostaDao.getRespostasByPedidoId(pedidoId);
	}
	
	@Override
	public List<Pedido> getPedidosByCausa(String causa) {
		return pedidoDao.getPedidosByCausa(causa);
	}
}
