package org.openmrs.module.sespct.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sespct.api.PedidoService;
import org.openmrs.module.sespct.api.dao.PedidoDao;
import org.openmrs.module.sespct.api.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Transactional
public class PedidoServiceImpl extends BaseOpenmrsService implements PedidoService {
	
	private static final Log log = LogFactory.getLog(PedidoServiceImpl.class);
	
	@Autowired
	private PedidoDao pedidoDao;
	
	public void setPedidoDao(PedidoDao pedidoDao) {
		this.pedidoDao = pedidoDao;
	}
	
	@Override
	public void initializeModule() {
		log.info("Initializing SESP-CT Module...");
		List<Pedido> existingPedidos = pedidoDao.getAllPedidos();
		if (existingPedidos.isEmpty() || existingPedidos.size() > 20) {
			log.info("No existing data found. Creating dummy data...");
			createDummyData();
		} else {
			log.info("Found " + existingPedidos.size() + " existing pedidos. Skipping dummy data creation.");
		}
	}
	
	@Override
	public Pedido savePedido(Pedido pedido) {
		return pedidoDao.savePedido(pedido);
	}
	
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
	public List<Pedido> getPedidosByEstado(String estado) {
		return pedidoDao.getPedidosByEstado(estado);
	}
	
	@Override
	public void deletePedido(Pedido pedido) {
		pedidoDao.deletePedido(pedido);
	}
	
	/**
	 * Creates and persists a set of dummy Pedido objects with their related entities. Generates 50
	 * records with submissions from 2020 to 2025. Includes special cases for NID mapping as per
	 * business rules.
	 */
	/**
	 * Creates and persists a set of dummy Pedido objects with their related entities. Generates 50
	 * records with submissions from 2020 to 2025. Includes special cases for NID mapping as per
	 * business rules.
	 */
	@Override
	public void createDummyData() {
		log.info("Starting dummy data creation for SESPCT module...");
		Random rand = new Random();
		int numberOfPedidos = 50; // Generate 50 dummy requests

		// Arrays for realistic dummy data
		String[] FIRST_NAMES = {"João", "Maria", "Pedro", "Ana", "Carlos", "Luisa", "António", "Isabel", "Manuel", "Rosa", "Francisco", "Teresa", "José", "Catarina", "Miguel"};
		String[] LAST_NAMES = {"Silva", "Santos", "Oliveira", "Pereira", "Costa", "Rodrigues", "Martins", "Jesus", "Soares", "Ferreira", "Alves", "Monteiro", "Ribeiro", "Rocha", "Nunes"};
		// FIXED: Simplified statuses to make response generation logic work correctly
		String[] REQUEST_STATUSES = {"Aprovado", "Não Processado", "Adiado", "Sem resposta"};
		String[] REJECTION_CAUSES = {"NID não encontrado", "NID duplicado",};
		String[] WHO_STAGES = {"Estadio I", "Estadio II", "Estadio III", "Estadio IV"};
		String[] PROVINCES = {"Maputo", "Gaza", "Inhambane", "Sofala", "Manica", "Tete", "Zambézia", "Nampula", "Cabo Delgado", "Niassa"};
		String[] DISTRICTS = {"Maputo", "Matola", "Boane", "Marracuene", "Manhiça", "Magude", "Moamba", "Namaacha", "Xai-Xai", "Chibuto"};
		String[] US_NAMES = {"Hospital Central de Maputo", "Hospital Geral de Mavalane", "Centro de Saúde da Polana", "Hospital Rural de Manhiça", "Centro de Saúde de Marracuene"};
		String[] YES_NO = {"Sim", "Não"};
		String[] PROFESSIONAL_CATEGORIES = {"Médico", "Enfermeiro", "Técnico de Medicina", "Farmacêutico"};
		String[] ART_REGIMENS = {"TDF+3TC+EFV", "AZT+3TC+NVP", "ABC+3TC+EFV", "TDF+3TC+DTG", "AZT+3TC+EFV"};
		String[] APPROVED_REGIMENS = {"ATV/r+TDF+3TC", "LPV/r+AZT+3TC", "DTG+ABC+3TC", "DRV/r+TDF+FTC"};
		String[] RESPONSE_TEXTS = {"Aprovado", "Rejeitado", "Aprovado com condições"};

		// Calculate how many records should have "NID não encontrado" status (about 20%)
		int nidNotFoundCount = (int) (numberOfPedidos * 0.2);

		for (int i = 0; i < numberOfPedidos; i++) {
			try {
				Pedido pedido = new Pedido();

				// FIXED: Set all standard OpenMRS audit fields, including the creator
//				pedido.setUuid(UUID.randomUUID().toString());
				// Use Context.getAuthenticatedUser() if in a logged-in session, or a default user ID.
				// Using '1' for the admin user as a fallback for dummy data scripts.
				pedido.setCreator(Context.getAuthenticatedUser());
				pedido.setDateCreated(new Date());
				pedido.setVoided(false);

				// --- 1. Populate Pedido (the main request entity) ---
				pedido.setPedidoId((1515 + i + 1)+""); // Use UUID for unique external ID
				pedido.setDataSubmissao(getRandomDate(rand, 2020, 2025));
				pedido.setVersao("1.0");
				pedido.setOrigem("OpenMRS");
				pedido.setTipoFormulario("Falencia Terapeutica");
				pedido.setSolicitadoPor("admin");

				// --- Special Case Logic for NID mapping ---
				if (i < nidNotFoundCount) {
					pedido.setEstado("Não Processado");
					pedido.setCausa("NID não encontrado");
				} else {
					pedido.setEstado(getRandomElement(REQUEST_STATUSES, rand));
					if ("Não Processado".equals(pedido.getEstado())) {
						// Ensure a cause is set if "Não Processado" but not the special NID case
						String cause = getRandomElement(REJECTION_CAUSES, rand);
						pedido.setCausa(cause);
					} else {
						pedido.setCausa(null);
					}
				}

				// --- 2. Populate DadosUtente (One-to-One) ---
				DadosUtente utente = new DadosUtente();
				utente.setPedido(pedido);

				String firstName = getRandomElement(FIRST_NAMES, rand);
				String lastName = getRandomElement(LAST_NAMES, rand);
				utente.setNomeCompleto(firstName + " " + lastName);
				utente.setIniciais(String.valueOf(firstName.charAt(0)) + String.valueOf(lastName.charAt(0)));

				String facilityCode = "0109010701";  // or pick from predefined list
				int year = 2007 + rand.nextInt(19);  // 2007–2025
				String sequence = String.format("%05d", i + 1); // ensures uniqueness per dummy data batch
				utente.setNid(facilityCode + "/" + year + "/" + sequence);

				utente.setIdade((double) (rand.nextInt(50) + 18));
				utente.setEstadioOms(getRandomElement(WHO_STAGES, rand));
				utente.setEstadioOmsMotivo("Motivo de teste para estadio " + utente.getEstadioOms());
				utente.setProvincia(getRandomElement(PROVINCES, rand));
				utente.setDistrito(getRandomElement(DISTRICTS, rand));
				utente.setUnidadeSanitaria(getRandomElement(US_NAMES, rand));
				utente.setCodigoUnidadeSanitaria(String.valueOf(101150 + rand.nextInt(100)));
				utente.setPeso((double) (rand.nextInt(40) + 50));
				utente.setSexo(rand.nextBoolean() ? "Masculino" : "Feminino");

				if ("Feminino".equals(utente.getSexo())) {
					boolean isGestante = rand.nextBoolean();
					if (isGestante) {
						utente.setGestante("Sim");
						utente.setDataProvavelParto(getRandomDate(rand, 2025, 2026));
						utente.setLactante("Não");
					} else {
						utente.setGestante("Não");
						boolean isLactante = rand.nextBoolean();
						utente.setLactante(isLactante ? "Sim" : "Não");
						if (isLactante) {
							utente.setDataParto(getRandomDate(rand, 2024, 2025));
						}
					}
				} else {
					utente.setGestante("Não");
					utente.setLactante("Não");
				}
				pedido.setDadosUtente(utente);

				// --- 3. Populate DadosClinico (One-to-One) ---
				DadosClinico clinico = new DadosClinico();
				clinico.setPedido(pedido);
				clinico.setNome("Dr. " + getRandomElement(FIRST_NAMES, rand) + " " + getRandomElement(LAST_NAMES, rand));
				clinico.setCategoriaProfissional(getRandomElement(PROFESSIONAL_CATEGORIES, rand));
				clinico.setTelefone("84" + String.format("%07d", rand.nextInt(10_000_000)));
				clinico.setEmail("clinico" + i + "@mail.com");
				pedido.setDadosClinico(clinico);

				// --- 4. Populate ReportarFalencia (One-to-One) ---
				ReportarFalencia falencia = new ReportarFalencia();
				falencia.setPedido(pedido);
				falencia.setHistoriaClinica("Paciente com historial de má adesão, apresentando sinais de falha virológica.");
				falencia.setHistoriaAdesao("Adesão reportada como inconsistente nos últimos meses.");
				falencia.setTratamentoTbAtivo(getRandomElement(YES_NO, rand));
				pedido.setReportarFalencia(falencia);

				// --- 5. Populate LinhaSolicitada (One-to-One) ---
				LinhaSolicitada linha = new LinhaSolicitada();
				linha.setPedido(pedido);
				linha.setLinha(rand.nextBoolean() ? "Segunda Linha" : "Terceira Linha");
				pedido.setLinhaSolicitada(linha);

				// --- 6. Populate HistoriaTarv (One-to-Many) ---
				// FIXED: Changed Set to List and HashSet to ArrayList
				List<HistoriaTarv> historiaTarvList = new ArrayList<>();
				int numTarvHistory = rand.nextInt(3) + 1;
				for (int j = 0; j < numTarvHistory; j++) {
					HistoriaTarv hist = new HistoriaTarv();
//					hist.setUuid(UUID.randomUUID().toString());
					hist.setCreator(Context.getAuthenticatedUser()); // Default to admin user
					hist.setDateCreated(new Date());
					hist.setVoided(false);
					hist.setPedido(pedido);
					hist.setDataInicio(getRandomDate(rand, 2018 - j * 2, 2020 - j * 2));
					hist.setDataTermino(getRandomDate(rand, 2020 - j * 2, 2022 - j * 2));
					hist.setEsquemaTarv(getRandomElement(ART_REGIMENS, rand));
					historiaTarvList.add(hist);
				}
				pedido.setHistoriaTarv(historiaTarvList);

				// --- 7. Populate DadosLaboratorioCargaViral (One-to-Many) ---
				// FIXED: Changed Set to List and HashSet to ArrayList
				List<DadosLaboratorioCargaViral> cargaViralList = new ArrayList<>();
				int numViralLoads = rand.nextInt(4) + 2;
				for (int j = 0; j < numViralLoads; j++) {
					DadosLaboratorioCargaViral cv = new DadosLaboratorioCargaViral();
//					cv.setUuid(UUID.randomUUID().toString());
					cv.setCreator(Context.getAuthenticatedUser());
					cv.setDateCreated(new Date());
					cv.setVoided(false);
					cv.setPedido(pedido);
					cv.setData(getRandomDate(rand, 2022, 2024));
					cv.setCargaViral((long) (1000 + rand.nextInt(50000)));
					cargaViralList.add(cv);
				}
				pedido.setDadosLaboratorioCargaViral(cargaViralList);

				// --- 8. Populate DadosLaboratorioCD4 (One-to-Many) ---
				// FIXED: Changed Set to List and HashSet to ArrayList
				List<DadosLaboratorioCD4> cd4List = new ArrayList<>();
				int numCd4 = rand.nextInt(4) + 2;
				for (int j = 0; j < numCd4; j++) {
					DadosLaboratorioCD4 cd4 = new DadosLaboratorioCD4();
//					cd4.setUuid(UUID.randomUUID().toString());
					cd4.setCreator(Context.getAuthenticatedUser());
					cd4.setDateCreated(new Date());
					cd4.setVoided(false);
					cd4.setPedido(pedido);
					cd4.setData(getRandomDate(rand, 2022, 2024));
					cd4.setCd4(100 + rand.nextInt(400));
					cd4.setCd4Percentagem(rand.nextDouble() * 15 + 5);
					cd4List.add(cd4);
				}
				pedido.setDadosLaboratorioCD4(cd4List);

				// --- 9. Populate Resposta (One-to-Many) - Conditionally ---
				// FIXED: Changed Set to List and HashSet to ArrayList
				List<Resposta> respostasList = new ArrayList<>();
				// FIXED: Corrected logic to generate responses for "Aprovado" or "Não Processado" statuses
				if ("Aprovado".equals(pedido.getEstado()) || "Não Processado".equals(pedido.getEstado())) {
					Resposta resposta = new Resposta();
//					resposta.setUuid(UUID.randomUUID().toString());
					resposta.setCreator(Context.getAuthenticatedUser());
					resposta.setDateCreated(new Date());
					resposta.setVoided(false);
					resposta.setPedido(pedido);

					MetadadosResposta meta = new MetadadosResposta();
					meta.setResposta(resposta);
					meta.setRespostaId("RESP-" + (i + 1));
					meta.setPedidoId(pedido.getPedidoId());
					meta.setVersao("1.0");
					meta.setTimestamp(new Date());
					meta.setProcessadoPor("Comite Nacional");
					meta.setUltimaSincronizacao(new Date());
					resposta.setMetadados(meta);

					RespostaComite comite = new RespostaComite();
					comite.setResposta(resposta);
					// Base response on the Pedido's state
					String responseText = "Aprovado".equals(pedido.getEstado()) ? "Aprovado" : "Rejeitado";
					comite.setRespostaTexto(responseText);

					if ("Aprovado".equals(responseText)) {
						comite.setLinhaTerapeutica(linha.getLinha());
						comite.setEsquemaAprovado(getRandomElement(APPROVED_REGIMENS, rand));
					}
					comite.setDataResposta(new Date());
					comite.setComentario("Comentário automático de teste gerado para pedido " + pedido.getPedidoId());
					comite.setAutorizante("Dr. Responsável Virtual");
					comite.setEmail("responsavel.comite@sis.gov.mz");
					comite.setContacto("82" + String.format("%07d", rand.nextInt(10_000_000)));
					comite.setNivelAutorizacao("Nacional");
					comite.setDataAprovacao(new Date());
					resposta.setRespostaComite(comite);

					Notificacoes notificacoes = new Notificacoes();
					notificacoes.setResposta(resposta);
					notificacoes.setWebhookEntregue(rand.nextBoolean());
					notificacoes.setEmailEnviado(true);
					notificacoes.setSmsEnviado(rand.nextBoolean());
					notificacoes.setDataNotificacao(new Date());
					resposta.setNotificacoes(notificacoes);

					respostasList.add(resposta);
				}
				pedido.setRespostas(respostasList);

				// --- 10. Save the Pedido ---
				pedidoDao.savePedido(pedido);

				if ((i + 1) % 10 == 0) {
					log.info("Created " + (i + 1) + " dummy pedidos...");
				}

			} catch (Exception e) {
				log.error("Error saving dummy pedido " + (i + 1), e);
			}
		}
		log.info("Finished creating " + numberOfPedidos + " dummy SESPCT pedidos with " + nidNotFoundCount + " NID mapping cases.");
	}
	
	private String getRandomElement(String[] array, Random rand) {
		return array[rand.nextInt(array.length)];
	}
	
	/**
	 * FIXED: Refactored to accept a Random instance instead of creating a new one.
	 */
	private Date getRandomDate(Random rand, int startYear, int endYear) {
		int year = startYear + rand.nextInt(endYear - startYear + 1);
		int month = rand.nextInt(12); // 0-11 for Calendar
		int day = rand.nextInt(28) + 1;
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, rand.nextInt(24), rand.nextInt(60), rand.nextInt(60));
		return cal.getTime();
	}
	
	// And implement it in your PedidoServiceImpl
	@Override
	public List<Pedido> getPedidosByDateRange(Date startDate, Date endDate) {
		// Adjust end date to include the entire day
		Calendar cal = Calendar.getInstance();
		cal.setTime(endDate);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		Date adjustedEndDate = cal.getTime();
		
		return pedidoDao.getPedidosByDateRange(startDate, adjustedEndDate);
	}
}
