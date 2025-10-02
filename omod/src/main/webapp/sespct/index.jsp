<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<jsp:useBean id="currentDate" class="java.util.Date" />
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="org.openmrs.module.sespct.api.model.Pedido" %>

<openmrs:require privilege="Permite acesso à aplicação SESP-CT" otherwise="/login.htm"
                 redirect="/module/sespct/sespct.form" />

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sespct/css/sespct.css" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sespct/css/datatables.net/1.13.2/jquery.dataTables.min.css" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sespct/css/buttons/2.3.4/buttons.dataTables.min.css" />

<h2><openmrs:message code="sespct.title"/></h2>

<div>
    <b class="boxHeader"><openmrs:message code="sespct.search.header"/></b>
    <div class="box">
        <%@ include file="../common/searchForm.jspf" %>
    </div>
</div>

<c:if test="${not empty errorMessage}">
    <div class="error">
        <strong>Error:</strong> ${errorMessage}
    </div>
    <br/>
</c:if>


<c:if test="${empty pedidos}">
    <div id="openmrs_msg">
        <b>
            <spring:message code="sespct.noResults" />
        </b>
    </div>
</c:if>

<%@ include file="../common/alertBox.jspf" %>

<c:if test="${not empty pedidos}">
<div class="box">
    <table id="ftResultsTable" class="disa-table disa-table-results" style="width:100%; font-size:12px;">
        <thead>
        <tr>
            <th><openmrs:message code="sespct.table.us"/></th>
            <th><openmrs:message code="sespct.table.nid"/></th>
            <th><openmrs:message code="sespct.table.ncft"/></th>
            <th><openmrs:message code="sespct.table.initials"/></th>
            <th><openmrs:message code="sespct.table.sex"/></th>
            <th><openmrs:message code="sespct.table.age"/></th>
            <th><openmrs:message code="sespct.table.submission"/></th>
            <th><openmrs:message code="sespct.table.responseDate"/></th>
            <th><openmrs:message code="sespct.table.sync"/></th>
            <th><openmrs:message code="sespct.table.status"/></th>
            <th><openmrs:message code="sespct.table.cause"/></th>
            <th><openmrs:message code="sespct.table.action"/></th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${empty pedidos}">
                <tr>
                    <td colspan="12" style="text-align: center; font-style: italic;">
                        <openmrs:message code="sespct.noResults"/>
                    </td>
                </tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="pedido" items="${pedidos}">
                    <c:set var="ultimaResposta" value="${null}" />
                    <c:if test="${not empty pedido.respostas}">
                        <c:set var="ultimaResposta" value="${pedido.respostas[fn:length(pedido.respostas) - 1]}" />
                    </c:if>

                    <tr>
                        <td>${pedido.dadosUtente.unidadeSanitaria} (${pedido.dadosUtente.codigoUnidadeSanitaria})</td>
                        <td>${pedido.dadosUtente.nid}</td>
                        <td>
                                    ${pedido.pedidoId}
                        </td>
                        <td>${pedido.dadosUtente.iniciais}</td>
                        <td>
                            <c:choose>
                                <c:when test="${pedido.dadosUtente.sexo == Pedido.SEXO_MASCULINO}">M</c:when>
                                <c:when test="${pedido.dadosUtente.sexo == Pedido.SEXO_FEMININO}">F</c:when>
                                <c:otherwise>${pedido.dadosUtente.sexo}</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <fmt:formatNumber value="${pedido.dadosUtente.idade}" maxFractionDigits="0" />
                        </td>
                        <td>
                                ${pedido.formattedDataSubmissao}
                        </td>
                        <td>
                                ${ultimaResposta.formattedDataResposta}
                        </td>

                        <td>
                                ${ultimaResposta.formattedTimestamp}
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${pedido.estado == Pedido.ESTADO_SEM_RESPOSTA}">
                                    <span class="status-no-response"><openmrs:message code="sespct.search.status.SEM_RESPOSTA"/></span>
                                </c:when>
                                <c:when test="${pedido.estado == Pedido.ESTADO_NAO_PROCESSADO}">
                                    <span class="status-not-processed"><openmrs:message code="sespct.search.status.NAO_PROCESSADO"/></span>
                                </c:when>
                                <c:when test="${pedido.estado == Pedido.ESTADO_APROVADO}">
                                    <span class="status-not-processed"><openmrs:message code="sespct.search.status.APROVADO"/></span>
                                </c:when>
                                <c:when test="${pedido.estado == Pedido.ESTADO_ADIADO}">
                                    <span class="status-not-processed"><openmrs:message code="sespct.search.status.ADIADO"/></span>
                                </c:when>
                                <c:otherwise>
                                    ${pedido.estado}
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${pedido.causa == Pedido.CAUSA_NID_NAO_ENCONTRADO}">
                                    <openmrs:message code="sespct.error.NOT_FOUND"/>
                                </c:when>
                                <c:when test="${pedido.causa == Pedido.CAUSA_NID_DUPLICADO}">
                                    <openmrs:message code="sespct.error.DUPLICATED"/>
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                            <c:when test="${pedido.causa == Pedido.CAUSA_NID_NAO_ENCONTRADO}">
                                <a href="manageftcases/${pedido.id}/map.form" onclick="mapNid('${pedido.dadosUtente.nid}')">
                                    <openmrs:message code="sespct.mapNid"/>
                                </a>
                            </c:when>
                            <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>
    <br />
    <div class="submit-btn center">
		<button id="exportButton">
		    <openmrs:message code="sespct.export.button"/>
		</button>
    </div>
</div>
</c:if>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sespct/js/datatables.net/1.13.2/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sespct/js/buttons/2.3.4/dataTables.buttons.min.js" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sespct/js/buttons/2.3.4/buttons.html5.min.js" />

<script type="text/javascript">
    window.addEventListener("DOMContentLoaded", () => {
        var $ = window.jQuery || window.jq || window.$;

        if ($) {
            // 1. Define the language object using OpenMRS messages
            const dataTableTranslations = {
                "emptyTable": "<openmrs:message code='sespct.datatable.emptyTable'/>",
                "info": "<openmrs:message code='sespct.datatable.info'/>",
                "infoEmpty": "<openmrs:message code='sespct.datatable.infoEmpty'/>",
                "infoFiltered": "<openmrs:message code='sespct.datatable.infoFiltered'/>",
                "lengthMenu": "<openmrs:message code='sespct.datatable.lengthMenu'/>",
                "loadingRecords": "<openmrs:message code='sespct.datatable.loadingRecords'/>",
                "processing": "<openmrs:message code='sespct.datatable.processing'/>",
                "search": "<openmrs:message code='sespct.datatable.search'/>",
                "zeroRecords": "<openmrs:message code='sespct.datatable.zeroRecords'/>",
                "paginate": {
                    "first": "<openmrs:message code='sespct.datatable.paginate.first'/>",
                    "last": "<openmrs:message code='sespct.datatable.paginate.last'/>",
                    "next": "<openmrs:message code='sespct.datatable.paginate.next'/>",
                    "previous": "<openmrs:message code='sespct.datatable.paginate.previous'/>"
                },
                "aria": {
                    "sortAscending": "<openmrs:message code='sespct.datatable.aria.sortAscending'/>",
                    "sortDescending": "<openmrs:message code='sespct.datatable.aria.sortDescending'/>"
                }
            };

            $("#ftResultsTable").DataTable({
                pageLength: 20,
                lengthMenu: [20, 50, 75, 100],
                order: [[6, 'desc']],
                // 2. Use the dynamically created language object here
                language: dataTableTranslations,
                dom: 'frtip<"clear">l',
                pagingType: 'full_numbers'
            });
        } else {
            console.error("No jQuery available!");
        }


        // Handle export button click
        const exportButton = document.getElementById("exportButton");
        if (exportButton) {
            exportButton.addEventListener("click", function (event) {
                event.preventDefault();

                // 1. Get date values and perform mandatory check (this part is unchanged)
                const startDate = document.getElementById("startDate").value.trim();
                const endDate = document.getElementById("endDate").value.trim();

                if (!startDate || !endDate) {
                    showAlertMessage("Para exportar dados, efectue primeiro uma pesquisa informando o periodo de submissão do pedido (a Data Início e Fim)");
                    return;
                }

                // 2. Get the rest of the filter values from the form
                const estado = document.getElementById('estado').value;
                const ncft = document.getElementById('ncft').value;
                const nid = document.getElementById('nid').value;
                const usCode = document.getElementById('us').value; // The ID of your <select> is 'us'

                // 3. Build the URL parameter list dynamically
                const baseUrl = "${pageContext.request.contextPath}/module/sespct/manageftcases/export.form";
                const params = [];

                // Add the mandatory date parameters first
                params.push('startDate=' + encodeURIComponent(startDate));
                params.push('endDate=' + encodeURIComponent(endDate));

                // Add the other parameters only if they have a value
                if (estado) {
                    params.push('estado=' + encodeURIComponent(estado));
                }
                if (ncft) {
                    params.push('ncft=' + encodeURIComponent(ncft));
                }
                if (nid) {
                    params.push('nid=' + encodeURIComponent(nid));
                }
                if (usCode) {
                    params.push('usCode=' + encodeURIComponent(usCode));
                }

                // 4. Construct the final URL and trigger the download
                const finalUrl = baseUrl + '?' + params.join('&');
                window.location.href = finalUrl;
            });
        }
    });

    // Function to show message in OpenMRS alert box
    function showAlertMessage(message) {
        const alertBox = document.getElementById("openmrs_msg");
        if (alertBox) {
            alertBox.innerHTML = "<b>" + message + "</b>";
            alertBox.style.display = "block";
        } else {
            // Create the alert box if it doesn't exist
            const alertContainer = document.getElementById("alert-box");
            if (alertContainer) {
                const newAlert = document.createElement("div");
                newAlert.id = "openmrs_msg";
                newAlert.innerHTML = "<b>" + message + "</b>";
                alertContainer.appendChild(newAlert);
            } else {
                // Fallback para alert do browser se não encontrar container
                alert(message);
            }
        }
        
        // Scroll para mostrar a mensagem
        const alertElement = document.getElementById("openmrs_msg");
        if (alertElement) {
            alertElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
    }
</script>

<%@ include file="/WEB-INF/template/footer.jsp" %>
