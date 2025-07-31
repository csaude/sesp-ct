package org.openmrs.module.sespct.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sespct.api.SESPCTService;
import org.openmrs.module.sespct.api.dao.SESPCTDao;
import org.openmrs.module.sespct.api.model.SESPCTCd4Data;
import org.openmrs.module.sespct.api.model.SESPCTRequest;
import org.openmrs.module.sespct.api.model.SESPCTTarvHistory;
import org.openmrs.module.sespct.api.model.SESPCTViralLoadData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Transactional
public class SESPCTServiceImpl extends BaseOpenmrsService implements SESPCTService {
	
	private static final Log log = LogFactory.getLog(SESPCTServiceImpl.class);
	
	@Autowired
	private SESPCTDao dao;
	
	public void setDao(SESPCTDao dao) {
		this.dao = dao;
	}
	
	@Override
	public void initializeModule() {
		log.info("Initializing SESP-CT Module...");
		// Check if we already have dummy data
		List<SESPCTRequest> existingRequests = dao.getAllSESPCTRequests();
		if (existingRequests.isEmpty()) {
			log.info("No existing data found. Creating dummy data...");
			createDummyData();
		} else {
			log.info("Found " + existingRequests.size() + " existing SESP requests. Skipping dummy data creation.");
		}
	}
	
	@Override
	public SESPCTRequest saveSESPCTRequest(SESPCTRequest sespRequest) {
		return dao.saveSESPCTRequest(sespRequest);
	}
	
	@Override
	public SESPCTRequest getSESPCTRequestById(Integer id) {
		return dao.getSESPCTRequestById(id);
	}
	
	@Override
	public SESPCTRequest getSESPCTRequestByPedidoId(String pedidoId) {
		return dao.getSESPCTRequestByPedidoId(pedidoId);
	}
	
	@Override
	public List<SESPCTRequest> getAllSESPCTRequests() {
		return dao.getAllSESPCTRequests();
	}
	
	@Override
	public List<SESPCTRequest> getSESPCTRequestsByEstado(String estado) {
		return dao.getSESPCTRequestsByEstado(estado);
	}
	
	@Override
	public void deleteSESPCTRequest(SESPCTRequest sespRequest) {
		dao.deleteSESPCTRequest(sespRequest);
	}
	
	@Override
	public void createDummyData() {
		try {
			log.info("Creating dummy SESP-CT data...");
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			
			// Create the main request based on your JSON sample
			SESPCTRequest request = new SESPCTRequest();
			
			// Set UUID and standard OpenMRS fields
			request.setUuid(UUID.randomUUID().toString());
			request.setCreator(Context.getAuthenticatedUser());
			request.setDateCreated(new Date());
			
			// Metadados
			request.setPedidoId("15151");
			request.setDataSubmissao(dateTimeFormat.parse("2024-03-15T10:30:00Z"));
			request.setVersao("2.0");
			request.setOrigem("SESP_MOZAMBIQUE");
			request.setTipoFormulario("e-FT");
			request.setSolicitadoPor("user_sesp_123");
			request.setEstado("Sem resposta");
			
			// Dados do Utente
			request.setNomeCompleto("João Manuel Santos");
			request.setIniciais("JMS");
			request.setNid("0109010701/2007/00399");
			request.setIdade(35.50);
			request.setEstadioOms("Estadio III");
			request.setEstadioOmsMotivo("Perda de peso significativa e infecções recorrentes");
			request.setProvincia("Maputo");
			request.setDistrito("Maputo Cidade");
			request.setUnidadeSanitaria("Hospital Central de Maputo");
			request.setCodigoUnidadeSanitaria("HCM001");
			request.setPeso(65.50);
			request.setSexo("masculino");
			request.setGestante("nao");
			request.setDataProvavelParto(null);
			request.setLactante("nao");
			request.setDataParto(null);
			
			// Reportar Falencia
			request.setHistoriaClinica("Paciente com histórico de boa adesão inicial, apresentando sinais de falência terapêutica nos últimos 6 meses. Observado perda de peso progressiva e aumento de infecções oportunistas.");
			request.setHistoriaAdesao("Adesão inicial excelente (>95%) nos primeiros 2 anos. Declínio gradual observado a partir de 2022 devido a fatores socioeconômicos. Adesão atual estimada em 70-80%.");
			request.setTratamentoTbAtivo("nao");
			
			// Dados Clinico
			request.setClinicoNome("Dr. Maria Santos");
			request.setClinicoCategoria("Medico");
			request.setClinicoTelefone("+258823456789");
			request.setClinicoEmail("maria.santos@misau.gov.mz");
			
			// Linha Solicitada
			request.setLinhaSolicitada("2 Linha");
			request.setAnexo("[BASE64_ENCODED_ATTACHMENT]");
			
			// Save the main request first
			request = dao.saveSESPCTRequest(request);
			
			// Create TARV History
			List<SESPCTTarvHistory> tarvHistory = new ArrayList<SESPCTTarvHistory>();
			
			SESPCTTarvHistory tarv1 = new SESPCTTarvHistory();
			tarv1.setUuid(UUID.randomUUID().toString());
			tarv1.setCreator(Context.getAuthenticatedUser());
			tarv1.setDateCreated(new Date());
			tarv1.setSespRequest(request);
			tarv1.setDataInicio(dateFormat.parse("2020-01-15"));
			tarv1.setDataTermino(dateFormat.parse("2022-06-30"));
			tarv1.setEsquemaTarv("AZT+3TC+EFV");
			tarvHistory.add(tarv1);
			
			SESPCTTarvHistory tarv2 = new SESPCTTarvHistory();
			tarv2.setUuid(UUID.randomUUID().toString());
			tarv2.setCreator(Context.getAuthenticatedUser());
			tarv2.setDateCreated(new Date());
			tarv2.setSespRequest(request);
			tarv2.setDataInicio(dateFormat.parse("2022-07-01"));
			tarv2.setDataTermino(dateFormat.parse("2024-03-10"));
			tarv2.setEsquemaTarv("TDF+3TC+EFV");
			tarvHistory.add(tarv2);
			
			request.setHistoriaTarv(tarvHistory);
			
			// Create CD4 Data
			List<SESPCTCd4Data> cd4Data = new ArrayList<SESPCTCd4Data>();
			
			SESPCTCd4Data cd4_1 = new SESPCTCd4Data();
			cd4_1.setUuid(UUID.randomUUID().toString());
			cd4_1.setCreator(Context.getAuthenticatedUser());
			cd4_1.setDateCreated(new Date());
			cd4_1.setSespRequest(request);
			cd4_1.setDataExame(dateFormat.parse("2024-01-15"));
			cd4_1.setCd4(250);
			cd4_1.setCd4Percentagem(12.5);
			cd4Data.add(cd4_1);
			
			SESPCTCd4Data cd4_2 = new SESPCTCd4Data();
			cd4_2.setUuid(UUID.randomUUID().toString());
			cd4_2.setCreator(Context.getAuthenticatedUser());
			cd4_2.setDateCreated(new Date());
			cd4_2.setSespRequest(request);
			cd4_2.setDataExame(dateFormat.parse("2024-02-20"));
			cd4_2.setCd4(180);
			cd4_2.setCd4Percentagem(8.2);
			cd4Data.add(cd4_2);
			
			request.setDadosLaboratorioCD4(cd4Data);
			
			// Create Viral Load Data
			List<SESPCTViralLoadData> viralLoadData = new ArrayList<SESPCTViralLoadData>();
			
			SESPCTViralLoadData vl1 = new SESPCTViralLoadData();
			vl1.setUuid(UUID.randomUUID().toString());
			vl1.setCreator(Context.getAuthenticatedUser());
			vl1.setDateCreated(new Date());
			vl1.setSespRequest(request);
			vl1.setDataExame(dateFormat.parse("2024-01-15"));
			vl1.setCargaViral(15000);
			viralLoadData.add(vl1);
			
			SESPCTViralLoadData vl2 = new SESPCTViralLoadData();
			vl2.setUuid(UUID.randomUUID().toString());
			vl2.setCreator(Context.getAuthenticatedUser());
			vl2.setDateCreated(new Date());
			vl2.setSespRequest(request);
			vl2.setDataExame(dateFormat.parse("2024-02-20"));
			vl2.setCargaViral(25000);
			viralLoadData.add(vl2);
			
			request.setDadosLaboratorioCargaViral(viralLoadData);
			
			// Save the complete request with all related data
			dao.saveSESPCTRequest(request);
			
			// Create a second dummy request for variety
			createSecondDummyRequest(dateFormat, dateTimeFormat);
			
			log.info("Dummy SESP-CT data created successfully");
			
		}
		catch (ParseException e) {
			log.error("Error parsing dates while creating dummy data", e);
		}
		catch (Exception e) {
			log.error("Error creating dummy SESP-CT data", e);
		}
	}
	
	private void createSecondDummyRequest(SimpleDateFormat dateFormat, SimpleDateFormat dateTimeFormat)
	        throws ParseException {
		SESPCTRequest request2 = new SESPCTRequest();
		
		// Set UUID and standard OpenMRS fields
		request2.setUuid(UUID.randomUUID().toString());
		request2.setCreator(Context.getAuthenticatedUser());
		request2.setDateCreated(new Date());
		
		// Metadados
		request2.setPedidoId("15152");
		request2.setDataSubmissao(dateTimeFormat.parse("2024-03-20T14:20:00Z"));
		request2.setVersao("2.0");
		request2.setOrigem("SESP_MOZAMBIQUE");
		request2.setTipoFormulario("e-FT");
		request2.setSolicitadoPor("user_sesp_456");
		request2.setEstado("Em processamento");
		
		// Dados do Utente
		request2.setNomeCompleto("Maria José Silva");
		request2.setIniciais("MJS");
		request2.setNid("0205020801/2008/00234");
		request2.setIdade(28.75);
		request2.setEstadioOms("Estadio II");
		request2.setEstadioOmsMotivo("Linfadenopatia generalizada persistente");
		request2.setProvincia("Gaza");
		request2.setDistrito("Xai-Xai");
		request2.setUnidadeSanitaria("Hospital Rural de Xai-Xai");
		request2.setCodigoUnidadeSanitaria("HRX002");
		request2.setPeso(58.2);
		request2.setSexo("feminino");
		request2.setGestante("sim");
		request2.setDataProvavelParto(dateFormat.parse("2024-08-15"));
		request2.setLactante("nao");
		request2.setDataParto(null);
		
		// Reportar Falencia
		request2.setHistoriaClinica("Paciente jovem, grávida, com falência virológica documentada. Necessita mudança urgente de esquema devido à gravidez.");
		request2.setHistoriaAdesao("Boa adesão documentada (>90%) mas com falência virológica. Possível resistência ao esquema atual.");
		request2.setTratamentoTbAtivo("nao");
		
		// Dados Clinico
		request2.setClinicoNome("Dr. António Machel");
		request2.setClinicoCategoria("Medico");
		request2.setClinicoTelefone("+258824567890");
		request2.setClinicoEmail("antonio.machel@misau.gov.mz");
		
		// Linha Solicitada
		request2.setLinhaSolicitada("2 Linha");
		request2.setAnexo("[BASE64_ENCODED_ATTACHMENT_2]");
		
		// Save the main request
		request2 = dao.saveSESPCTRequest(request2);
		
		// Add some TARV history for the second patient
		List<SESPCTTarvHistory> tarvHistory2 = new ArrayList<SESPCTTarvHistory>();
		
		SESPCTTarvHistory tarv2_1 = new SESPCTTarvHistory();
		tarv2_1.setUuid(UUID.randomUUID().toString());
		tarv2_1.setCreator(Context.getAuthenticatedUser());
		tarv2_1.setDateCreated(new Date());
		tarv2_1.setSespRequest(request2);
		tarv2_1.setDataInicio(dateFormat.parse("2023-01-10"));
		tarv2_1.setDataTermino(dateFormat.parse("2024-03-15"));
		tarv2_1.setEsquemaTarv("TDF+3TC+DTG");
		tarvHistory2.add(tarv2_1);
		
		request2.setHistoriaTarv(tarvHistory2);
		
		// Add recent CD4 data
		List<SESPCTCd4Data> cd4Data2 = new ArrayList<SESPCTCd4Data>();
		
		SESPCTCd4Data cd4_2_1 = new SESPCTCd4Data();
		cd4_2_1.setUuid(UUID.randomUUID().toString());
		cd4_2_1.setCreator(Context.getAuthenticatedUser());
		cd4_2_1.setDateCreated(new Date());
		cd4_2_1.setSespRequest(request2);
		cd4_2_1.setDataExame(dateFormat.parse("2024-03-01"));
		cd4_2_1.setCd4(420);
		cd4_2_1.setCd4Percentagem(22.1);
		cd4Data2.add(cd4_2_1);
		
		request2.setDadosLaboratorioCD4(cd4Data2);
		
		// Add viral load data showing failure
		List<SESPCTViralLoadData> viralLoadData2 = new ArrayList<SESPCTViralLoadData>();
		
		SESPCTViralLoadData vl2_1 = new SESPCTViralLoadData();
		vl2_1.setUuid(UUID.randomUUID().toString());
		vl2_1.setCreator(Context.getAuthenticatedUser());
		vl2_1.setDateCreated(new Date());
		vl2_1.setSespRequest(request2);
		vl2_1.setDataExame(dateFormat.parse("2024-03-01"));
		vl2_1.setCargaViral(45000);
		viralLoadData2.add(vl2_1);
		
		request2.setDadosLaboratorioCargaViral(viralLoadData2);
		
		// Save the complete second request
		dao.saveSESPCTRequest(request2);
	}
}
