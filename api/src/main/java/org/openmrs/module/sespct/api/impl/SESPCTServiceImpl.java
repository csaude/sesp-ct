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
			log.info("Creating 50 dummy SESP-CT records...");

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

			for (int i = 1; i <= 50; i++) {
				SESPCTRequest request = new SESPCTRequest();
				request.setUuid(UUID.randomUUID().toString());
				request.setCreator(Context.getAuthenticatedUser());
				request.setDateCreated(new Date());

				// Generate dynamic values
				String nid = String.format("0109010701/2007/%05d", i);
				String initials = "PT" + i;
				int idade = 20 + (i % 30); // range 20-49
				String sexo = (i % 2 == 0) ? "masculino" : "feminino";
				String estado = (i % 5 == 0) ? "Não processado" : "Sem resposta";
				String causa = estado.equals("Não processado") ? "NID não encontrado" : "-";

				request.setPedidoId(""+i);
				request.setDataSubmissao(dateTimeFormat.parse("2025-07-31T10:30:00Z"));
				request.setVersao("2.0");
				request.setOrigem("SESP_MOZAMBIQUE");
				request.setTipoFormulario("e-FT");
				request.setSolicitadoPor("user_sesp_" + i);
				request.setEstado(estado);

				request.setNomeCompleto("Paciente Teste " + i);
				request.setIniciais(initials);
				request.setNid(nid);
				request.setIdade((double) idade);
				request.setEstadioOms("Estadio I");
				request.setEstadioOmsMotivo("Sem sintomas relevantes");
				request.setProvincia("Maputo");
				request.setDistrito("Maputo Cidade");
				request.setUnidadeSanitaria("US Simulada " + i);
				request.setCodigoUnidadeSanitaria("US" + String.format("%03d", i));
				request.setPeso((double)50 + (i % 30));
				request.setSexo(sexo);
				request.setGestante("nao");
				request.setDataProvavelParto(null);
				request.setLactante("nao");
				request.setDataParto(null);

				request.setHistoriaClinica("Histórico clínico simulado " + i);
				request.setHistoriaAdesao("Adesão estimada em " + (60 + (i % 30)) + "%.");
				request.setTratamentoTbAtivo("nao");

				request.setClinicoNome("Dr. Simulado " + i);
				request.setClinicoCategoria("Medico");
				request.setClinicoTelefone("+25882" + String.format("%07d", i));
				request.setClinicoEmail("medico" + i + "@misau.gov.mz");

				request.setLinhaSolicitada("1 Linha");
				request.setAnexo("[DUMMY_ATTACHMENT]");

				// Save request before creating relations
				request = dao.saveSESPCTRequest(request);

				// TARV History
				List<SESPCTTarvHistory> tarvHistory = new ArrayList<>();
				SESPCTTarvHistory tarv = new SESPCTTarvHistory();
				tarv.setUuid(UUID.randomUUID().toString());
				tarv.setCreator(Context.getAuthenticatedUser());
				tarv.setDateCreated(new Date());
				tarv.setSespRequest(request);
				tarv.setDataInicio(dateFormat.parse("2022-01-01"));
				tarv.setDataTermino(dateFormat.parse("2023-01-01"));
				tarv.setEsquemaTarv("TDF+3TC+EFV");
				tarvHistory.add(tarv);
				request.setHistoriaTarv(tarvHistory);

				// CD4 Data
				List<SESPCTCd4Data> cd4Data = new ArrayList<>();
				SESPCTCd4Data cd4 = new SESPCTCd4Data();
				cd4.setUuid(UUID.randomUUID().toString());
				cd4.setCreator(Context.getAuthenticatedUser());
				cd4.setDateCreated(new Date());
				cd4.setSespRequest(request);
				cd4.setDataExame(dateFormat.parse("2024-07-31"));
				cd4.setCd4(200 + (i % 100));
				cd4.setCd4Percentagem((double)10 + (i % 5));
				cd4Data.add(cd4);
				request.setDadosLaboratorioCD4(cd4Data);

				// Viral Load Data
				List<SESPCTViralLoadData> vlData = new ArrayList<>();
				SESPCTViralLoadData vl = new SESPCTViralLoadData();
				vl.setUuid(UUID.randomUUID().toString());
				vl.setCreator(Context.getAuthenticatedUser());
				vl.setDateCreated(new Date());
				vl.setSespRequest(request);
				vl.setDataExame(dateFormat.parse("2024-07-31"));
				vl.setCargaViral(10000 + (i * 500));
				vlData.add(vl);
				request.setDadosLaboratorioCargaViral(vlData);

				// Save with child data
				dao.saveSESPCTRequest(request);
			}

			log.info("Dummy batch created successfully.");
		} catch (Exception e) {
			log.error("Error while creating batch dummy data", e);
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
