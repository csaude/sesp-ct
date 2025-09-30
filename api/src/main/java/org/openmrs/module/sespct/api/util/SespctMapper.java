package org.openmrs.module.sespct.api.util;

import org.openmrs.api.context.Context;
import org.openmrs.module.sespct.api.dao.PedidoDao;
import org.openmrs.module.sespct.api.dto.*;
import org.openmrs.module.sespct.api.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.stream.Collectors;

public class SespctMapper {
	
	// A flexible formatter that can handle dates with or without timezones
	private static final DateTimeFormatter ISO_OFFSET_DATE_TIME = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
	
	private static final DateTimeFormatter ISO_LOCAL_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
	
	private static PedidoDao pedidoDao;
	
	/**
	 * Converts a PedidoDTO from the API into a persistable Pedido entity.
	 */
	public static Pedido toPedidoEntity(PedidoDTO dto) {
		if (dto == null) {
			return null;
		}
		MetadadosPedidoDTO meta = dto.getMetadadosPedidoDTO();

		// 1. Create the main Pedido entity and set OpenMRS base fields
		Pedido pedido = new Pedido();
		pedido.setUuid(dto.getUuid()); // Use the UUID from the API
		pedido.setCreator(Context.getAuthenticatedUser());
		pedido.setDateCreated(new Date());


		// 2. Map metadata
		if (meta != null) {
			pedido.setPedidoId(meta.getPedidoId());

			String incomingEstado = meta.getEstado();

			// Check if the incoming state is not null
			if (incomingEstado != null) {
				// Use equalsIgnoreCase for case-insensitive comparison
				if ("APROVADO".equalsIgnoreCase(incomingEstado)) {
					pedido.setEstado(Pedido.ESTADO_APROVADO);

				} else if ("ADIADO".equalsIgnoreCase(incomingEstado)) {
					pedido.setEstado(Pedido.ESTADO_ADIADO);

				} else if ("SEM_RESPOSTA".equalsIgnoreCase(incomingEstado)) {
					pedido.setEstado(Pedido.ESTADO_SEM_RESPOSTA);

				} else {
					pedido.setEstado(incomingEstado.toLowerCase());
				}
			}

			pedido.setOrigem(meta.getOrigem());
			pedido.setSolicitadoPor(meta.getSolicitadoPor());
			pedido.setTipoFormulario(meta.getTipoFormulario());
			pedido.setVersao(meta.getVersao());
			if (meta.getDataSubmissao() != null) {
				// The ZonedDateTime class is best for handling strings with timezone offsets
				ZonedDateTime zdt = ZonedDateTime.parse(meta.getDataSubmissao(), ISO_OFFSET_DATE_TIME);
				pedido.setDataSubmissao(zdt.toLocalDateTime());
			}
		}

		// 3. Map One-to-One nested objects by calling helper methods
		pedido.setDadosUtente(toDadosUtenteEntity(dto.getDadosUtente(), pedido));
		pedido.setDadosClinico(toDadosClinicoEntity(dto.getDadosClinico(), pedido));
		pedido.setLinhaSolicitada(toLinhaSolicitadaEntity(dto.getLinhaSolicitada(), pedido));
		pedido.setReportarFalencia(toReportarFalenciaEntity(dto.getReportarFalencia(), pedido));

		// 4. Map One-to-Many lists
		if (dto.getHistoriaTarv() != null) {
			pedido.setHistoriaTarv(
					dto.getHistoriaTarv().stream()
							.map(historiaDto -> toHistoriaTarvEntity(historiaDto, pedido))
							.collect(Collectors.toList())
			);
		}
		if (dto.getDadosLaboratorioCD4() != null) {
			pedido.setDadosLaboratorioCD4(
					dto.getDadosLaboratorioCD4().stream()
							.map(cd4Dto -> toDadosLaboratorioCD4Entity(cd4Dto, pedido))
							.collect(Collectors.toList())
			);
		}
		if (dto.getDadosLaboratorioCargaViral() != null) {
			pedido.setDadosLaboratorioCargaViral(
					dto.getDadosLaboratorioCargaViral().stream()
							.map(cvDto -> toDadosLaboratorioCargaViralEntity(cvDto, pedido))
							.collect(Collectors.toList())
			);
		}

		return pedido;
	}
	
	// --- Helper methods for nested objects ---
	private static DadosUtente toDadosUtenteEntity(DadosUtenteDTO dto, Pedido parent) {
		if (dto == null)
			return null;
		DadosUtente entity = new DadosUtente();
		entity.setPedido(parent); // Set back-reference
		entity.setCodigoUnidadeSanitaria(dto.getCodigoUnidadeSanitaria());
		entity.setDistrito(dto.getDistrito());
		entity.setEstadioOms(dto.getEstadioOms());
		entity.setEstadioOmsMotivo(dto.getEstadioOmsMotivo());
		entity.setGestante(dto.getGestante());
		entity.setIdade(dto.getIdade());
		entity.setIniciais(dto.getIniciais());
		entity.setLactante(dto.getLactante());
		entity.setNid(dto.getNid());
		entity.setNomeCompleto(dto.getNomeCompleto());
		entity.setPeso(dto.getPeso());
		entity.setProvincia(dto.getProvincia());
		entity.setSexo(dto.getSexo());
		entity.setUnidadeSanitaria(dto.getUnidadeSanitaria());
		return entity;
	}
	
	private static DadosClinico toDadosClinicoEntity(DadosClinicoDTO dto, Pedido parent) {
		if (dto == null)
			return null;
		DadosClinico entity = new DadosClinico();
		entity.setPedido(parent);
		entity.setCategoriaProfissional(dto.getCategoriaProfissional());
		entity.setEmail(dto.getEmail());
		entity.setNome(dto.getNome());
		entity.setTelefone(dto.getTelefone());
		return entity;
	}
	
	private static LinhaSolicitada toLinhaSolicitadaEntity(LinhaSolicitadaDTO dto, Pedido parent) {
		if (dto == null)
			return null;
		LinhaSolicitada entity = new LinhaSolicitada();
		entity.setPedido(parent);
		entity.setLinha(dto.getLinha());
		return entity;
	}
	
	private static ReportarFalencia toReportarFalenciaEntity(ReportarFalenciaDTO dto, Pedido parent) {
		if (dto == null)
			return null;
		ReportarFalencia entity = new ReportarFalencia();
		entity.setPedido(parent);
		entity.setHistoriaAdesao(dto.getHistoriaAdesao());
		entity.setHistoriaClinica(dto.getHistoriaClinica());
		entity.setTratamentoTbAtivo(dto.getTratamentoTbAtivo());
		return entity;
	}
	
	private static HistoriaTarv toHistoriaTarvEntity(HistoriaTarvDTO dto, Pedido parent) {
		if (dto == null)
			return null;
		HistoriaTarv entity = new HistoriaTarv();
		entity.setPedido(parent);
		entity.setEsquemaTarv(dto.getEsquemaTarv());
		if (dto.getDataInicio() != null) {
			entity.setDataInicio(LocalDate.parse(dto.getDataInicio(), ISO_LOCAL_DATE).atStartOfDay());
		}
		if (dto.getDataTermino() != null) {
			entity.setDataTermino(LocalDate.parse(dto.getDataTermino(), ISO_LOCAL_DATE).atStartOfDay());
		}
		return entity;
	}
	
	private static DadosLaboratorioCD4 toDadosLaboratorioCD4Entity(DadosLaboratorioCD4DTO dto, Pedido parent) {
		if (dto == null)
			return null;
		DadosLaboratorioCD4 entity = new DadosLaboratorioCD4();
		entity.setPedido(parent);
		entity.setCd4(dto.getCd4());
		entity.setCd4Percentagem(dto.getCd4Percentagem());
		if (dto.getData() != null) {
			entity.setData(LocalDate.parse(dto.getData(), ISO_LOCAL_DATE).atStartOfDay());
		}
		return entity;
	}
	
	private static DadosLaboratorioCargaViral toDadosLaboratorioCargaViralEntity(DadosLaboratorioCargaViralDTO dto,
	        Pedido parent) {
		if (dto == null)
			return null;
		DadosLaboratorioCargaViral entity = new DadosLaboratorioCargaViral();
		entity.setPedido(parent);
		entity.setCargaViral(dto.getCargaViral());
		if (dto.getData() != null) {
			entity.setData(LocalDate.parse(dto.getData(), ISO_LOCAL_DATE).atStartOfDay());
		}
		return entity;
	}
	
	public static Resposta toRespostaEntity(RespostaDTO dto, Pedido parentPedido) {
		if (dto == null || parentPedido == null) {
			return null;
		}
		
		Resposta entity = new Resposta();
		
		// Set standard OpenMRS fields
		entity.setCreator(Context.getAuthenticatedUser());
		entity.setDateCreated(new Date()); // BaseOpenmrsData uses java.util.Date
		entity.setPedido(parentPedido);
		entity.setUuid(dto.getUuid()); // Use the UUID from the outer message
		
		// Map all fields from the flat DTO to the flat entity
		entity.setRespostaIdExterno(dto.getRespostaId());
		entity.setProcessadoPor(dto.getProcessadoPor());
		entity.setVersao(dto.getVersao());
		entity.setResposta(dto.getResposta());
		entity.setLinhaTerapeutica(dto.getLinhaTerapeutica());
		entity.setEsquemaAprovado(dto.getEsquemaAprovado());
		entity.setComentario(dto.getComentario());
		entity.setAutorizante(dto.getAutorizante());
		entity.setEmailAutorizante(dto.getEmailAutorizante());
		entity.setContactoAutorizante(dto.getContactoAutorizante());
		entity.setNivelAutorizacao(dto.getNivelAutorizacao());
		
		// Handle LocalDateTime parsing safely using a helper
		if (dto.getTimestamp() != null) {
			ZonedDateTime zdt = ZonedDateTime.parse(dto.getTimestamp(), ISO_OFFSET_DATE_TIME);
			entity.setTimestamp(zdt.toLocalDateTime());
		}
		
		if (dto.getDataResposta() != null) {
			ZonedDateTime zdt = ZonedDateTime.parse(dto.getDataResposta(), ISO_OFFSET_DATE_TIME);
			entity.setDataResposta(zdt.toLocalDateTime());
		}
		
		entity.setSincronizado(true); // Mark as processed
		
		return entity;
	}
	
}
