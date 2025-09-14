package org.openmrs.module.sespct.api.util;

public class Constants {
	
	private Constants() {
		// Utility class, evitar instanciamento
	}
	
	public static final String CT_ENCOUNTER_TYPE = "45277526-85a1-497a-93ce-30240d4d5acb";
	
	public static final String PRIMARY_USER = "generic.provider";
	
	public static final String FALLBACK_USER = "provedor.desconhecido";
	
	public static final String PEDIDO_STATUS_NOT_PROCESSED = "NOT_PROCESSED";
	
	public static final String PEDIDO_STATUS_PATIENT_NOT_FOUND = "NID_NOT_FOUND";
	
	public static final String PEDIDO_STATUS_DUPLICATE_NID = "DUPLICATE_NID";
	
	public static final String SIM_UUID = "1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public static final String NAO_UUID = "1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public static final String SIM = "Sim";
	
	public static final String NAO = "Nao";
	
	public static final String ESTADO_SEM_RESPOSTA = "Sem resposta";
	
	public static final String ESTADO_APROVADO = "Aprovado";
	
	public static final String ESTADO_ADIADO = "Adiado";
	
	// Dados do utente
	public static final String INICIAIS_UTENTE_UUID = "397ebb22-925f-413a-b9f0-2d84553686dc";
	
	public static final String PESO_UUID = "5089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public static final String GESTANTE_UUID = "e1e056a6-1d5f-11e0-b929-000c29ad1d07";
	
	public static final String LACTANTE_UUID = "bc4fe755-fc8f-49b8-9956-baf2477e8313";
	
	public static final String ID_PEDIDO_UUID = "70609d1a-6c7f-4fb2-8bbf-45edea172b99";
	
	public static final String ESTADIO_OMS_UUID = "e1e53c02-1d5f-11e0-b929-000c29ad1d07";
	
	public static final String ESTADIO_I_UUID = "e1d9055e-1d5f-11e0-b929-000c29ad1d07";
	
	public static final String ESTADIO_II_UUID = "e1d9066c-1d5f-11e0-b929-000c29ad1d07";
	
	public static final String ESTADIO_III_UUID = "e1d9077a-1d5f-11e0-b929-000c29ad1d07";
	
	public static final String ESTADIO_IV_UUID = "e1d90888-1d5f-11e0-b929-000c29ad1d07";
	
	public static final String ESTADIO_OMS_MOTIVO = "e1e09526-1d5f-11e0-b929-000c29ad1d07";
	
	public static final String ESTADIO_I = "Estadio I";
	
	public static final String ESTADIO_II = "Estadio II";
	
	public static final String ESTADIO_III = "Estadio III";
	
	public static final String ESTADIO_IV = "Estadio VI";
	
	// Historia tarv
	public static final String HISTORIA_TARV_GROUP_UUID = "b5df02e4-991c-4320-9aaf-d3132f3aacfa";
	
	public static final String TARV_DATA_INICIO_UUID = "5bdef973-bc81-491a-826f-bbb4f26785bc";
	
	public static final String TARV_DATA_FIM_UUID = "5caf0d41-913f-494c-b1d9-3aa2524dd84d";
	
	public static final String TARV_ESQUEMA_UUID = "e1d83e4e-1d5f-11e0-b929-000c29ad1d07";
	
	public static final String OUTRO_UUID = "5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	// Historia Laboratorial CD4
	public static final String HISTORIA_CD4_GROUP_UUID = "e3530ef0-74f5-49fc-9abd-e2b9e845cd74";
	
	public static final String CD4_DATA_UUID = "3026f0f7-98f3-4b64-b475-5f827a92ac82";
	
	public static final String CD4_ABSOLUTO_UUID = "e1dd5ab4-1d5f-11e0-b929-000c29ad1d07";
	
	public static final String CD4_PERCENTUAL_UUID = "e1d48fba-1d5f-11e0-b929-000c29ad1d07";
	
	// Historia Laboratorial Carga Viral
	public static final String HISTORIA_CARGA_VIRAL_GROUP_UUID = "8251a01e-38ca-4646-aabd-63a6bc5c8130";
	
	public static final String CV_DATA_UUID = "d636391d-25fb-4ba0-83b0-55ea5347903b";
	
	public static final String CV_VALOR_UUID = "e1d6247e-1d5f-11e0-b929-000c29ad1d07";
	
	// Resumo Historia Clínica
	public static final String HISTORIA_CLINICA_UUID = "5089d2c3-aed1-4bba-9c22-02c647bbc851";
	
	public static final String HISTORIA_ADESAO_UUID = "c58386ec-9415-43e2-826e-a06fe55d5b58";
	
	public static final String TRATAMENTO_TB_UUID = "e1d9fbda-1d5f-11e0-b929-000c29ad1d07";
	
	// Dados do clínico
	public static final String CLINICO_NOME_UUID = "0ddebec7-5d05-4bdf-8964-393a35908c1e";
	
	public static final String CLINICO_TELEFONE_UUID = "eb23c94a-2c2e-40fa-ab82-22308b1c5f27";
	
	public static final String CLINICO_CATEGORIA_UUID = "74d70a0f-a3d7-4a4b-8c2b-2a2c24af8714";
	
	public static final String CLINICO_EMAIL_UUID = "02698cbf-0b20-4e21-8c42-54efa8fe1b20";
	
	public static final String CATEGORIA_MEDICO_UUID = "8c6dbda2-ac14-4844-9ae8-0c687e9971ef";
	
	public static final String CATEGORIA_TECNICO_GERAL_UUID = "fc27e878-3b50-4e14-a577-d28077ce3ca4";
	
	public static final String CATEGORIA_ENFERMEIRO_SMI_UUID = "e17e94ea-becb-4893-ba49-d836d88842a6";
	
	public static final String CATEGORIA_FARMACEUTICO_UUID = "8a32da11-8bc4-4e8c-9203-f38351dc488f";
	
	public static final String MEDICO = "MEDICO";
	
	public static final String TECNICO_MEDICIONA_GERAL = "TECNICO DE MEDICINA GERAL";
	
	public static final String ENFERMEIRA_SMI = "ENFERMEIRO/A DE SMI";
	
	public static final String TECNICO_FARMACEUTICO = "TECNICO FARMACEUTICO";
	
	// Linha Solicitada
	public static final String LINHA_SOLICITADA_GROUP_UUID = "a298fe1b-d65d-48f3-ba52-144694809539";
	
	public static final String LINHA_SOLICITADA_UUID = "fdff0637-b36f-4dce-90c7-fe9f1ec586f0";
	
	public static final String SEGUNDA_LINHA_UUID = "7f367983-9911-4f8c-bbfc-a85678801f64";
	
	public static final String TERCEIRA_LINHA_UUID = "ade7656f-0ce3-461b-b7d8-121932dcd6a2";
	
	public static final String REGIME_INDIVIDUALIZADO_UUID = "160050AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	// Resposta do Comite Terapeutico 
	public static final String RESPOSTA_COMITE_GROUP_UUID = "bb25dc01-ea37-42a7-ae51-ecce1bdd7d3c";
	
	public static final String RESPOSTA_ESTADO_UUID = "3dba5068-fc4d-46f5-a338-78548761a21c";
	
	public static final String RESPOSTA_LINHA_UUID = "fdff0637-b36f-4dce-90c7-fe9f1ec586f0";
	
	public static final String RESPOSTA_COMENTARIO_UUID = "e1d9d498-1d5f-11e0-b929-000c29ad1d07";
	
	public static final String RESPOSTA_AUTOR_UUID = "8e96e54b-9c2b-462f-ac2a-9e085ba9d179";
	
	public static final String ESTADO_SEM_RESPOSTA_UUID = "e1dc9ce6-1d5f-11e0-b929-000c29ad1d07";
	
	public static final String ESTADO_APROVADO_UUID = "1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public static final String ESTADO_ADIADO_UUID = "1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
}
