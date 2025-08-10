<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<jsp:useBean id="currentDate" class="java.util.Date" />
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<openmrs:require privilege="Permite acesso à aplicação SESP-CT" otherwise="/login.htm"
                 redirect="/module/sespct/sespct.form" />

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sespct/css/sespct.css" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sespct/css/datatables.net/1.13.2/jquery.dataTables.min.css" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sespct/css/buttons/2.3.4/buttons.dataTables.min.css" />

<h2><openmrs:message code="sespct.title"/></h2>

<p>
    <small>
        <small>
            <fmt:message key="sespct.lastSync">
                <fmt:param value="8 de Julho de 2025" />
                <fmt:param value="11:36h" />
                <fmt:param value="10 minutos" />
            </fmt:message>
        </small>
    </small>
</p>


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
                        Nenhum caso encontrado com os critérios selecionados.
                    </td>
                </tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="pedido" items="${pedidos}">
                    <tr>
                        <td>${pedido.dadosUtente.unidadeSanitaria} (${pedido.dadosUtente.codigoUnidadeSanitaria})</td>
                        <td>${pedido.dadosUtente.nid}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/module/sespct/viewRequest.form?pedidoId=${pedido.pedidoId}">
                                    ${pedido.pedidoId}
                            </a>
                        </td>
                        <td>${pedido.dadosUtente.iniciais}</td>
                        <td>
                            <c:choose>
                                <c:when test="${pedido.dadosUtente.sexo == 'masculino'}">M</c:when>
                                <c:when test="${pedido.dadosUtente.sexo == 'feminino'}">F</c:when>
                                <c:otherwise>${pedido.dadosUtente.sexo}</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <fmt:formatNumber value="${pedido.dadosUtente.idade}" maxFractionDigits="0" />
                        </td>
                        <td>
                            <fmt:formatDate value="${pedido.dataSubmissao}" pattern="dd/MM/yyyy" />
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${pedido.estado == 'Sem resposta'}">-</c:when>
                                <c:otherwise>
                                    <fmt:formatDate value="${pedido.dataSubmissao}" pattern="dd/MM/yyyy" />
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <fmt:formatDate value="${pedido.dataSubmissao}" pattern="dd/MM/yyyy" />
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${pedido.estado == 'Sem resposta' || pedido.estado == 'No Response'}">
                                    <span class="status-no-response"><openmrs:message code="sespct.search.status.SEM_RESPOSTA"/></span>
                                </c:when>
                                <c:when test="${pedido.estado == 'Não Processado' || pedido.estado == 'Not Processed'}">
                                    <span class="status-not-processed"><openmrs:message code="sespct.search.status.NAO_PROCESSADO"/></span>
                                </c:when>
                                <c:otherwise>
                                    ${pedido.estado} <%-- Fallback for any other statuses --%>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${pedido.estado == 'Não Processado' || pedido.estado == 'Not Processed'}">
                                    <openmrs:message code="sespct.error.NOT_FOUND"/>
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:if test="${pedido.estado == 'Não Processado'}">
                                <a href="#" onclick="mapNid('${request.nid}')">
                                    <openmrs:message code="sespct.mapNid"/>
                                </a>
                            </c:if>
                            <c:if test="${pedido.estado != 'Não Processado'}">-</c:if>
                        </td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>
    <br />
    <div class="submit-btn center">
        <button id="exportButton" onclick="window.location.href = '${pageContext.request.contextPath}/module/sespct/manageftcases/export.form'">
            <openmrs:message code="sespct.export.button"/>
        </button>
    </div>
</div>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sespct/js/datatables.net/1.13.2/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sespct/js/buttons/2.3.4/dataTables.buttons.min.js" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sespct/js/buttons/2.3.4/buttons.html5.min.js" />

<script type="text/javascript">
    window.addEventListener("DOMContentLoaded", () => {
        var $ = window.jQuery || window.jq || window.$;

        if ($) {
            $("#ftResultsTable").DataTable({
                pageLength: 20,
                lengthMenu: [20, 50, 75, 100],
                order: [[6, 'desc']],
                language: {
                    "emptyTable": "Nenhum registo encontrado",
                    "info": "Mostrando de _START_ até _END_ de _TOTAL_ registos",
                    "infoEmpty": "Mostrando 0 até 0 de 0 registos",
                    "infoFiltered": "(Filtrado de _MAX_ registos no total)",
                    "lengthMenu": "Mostrar _MENU_ registos",
                    "loadingRecords": "A carregar...",
                    "processing": "A processar...",
                    "search": "Procurar:",
                    "zeroRecords": "Nenhum registo encontrado",
                    "paginate": {
                        "first": "Primeiro",
                        "last": "Último",
                        "next": "Seguinte",
                        "previous": "Anterior"
                    },
                    "aria": {
                        "sortAscending": ": Ordenar por ordem crescente",
                        "sortDescending": ": Ordenar por ordem decrescente"
                    }
                }
            });
        } else {
            console.error("No jQuery available!");
        }

        // Handle export button click
        const exportButton = document.getElementById("exportButton");
        if (exportButton) {
            exportButton.addEventListener("click", function () {
                const startDate = document.getElementById("startDate").value.trim();
                const endDate = document.getElementById("endDate").value.trim();

                if (!startDate || !endDate) {
                	showAlertMessage("Para exportar dados, efectue primeiro uma pesquisa informando a Data Inicial e a Data Final (em que os resultados foram criados no servidor SESP).");
                	//window.location.href = "/openmrs/module/sespct/sespct.form";
                    return;
                }

                const url = "/openmrs/module/sespct/manageftcases/export.form"
                          + "?startDate=" + encodeURIComponent(startDate)
                          + "&endDate=" + encodeURIComponent(endDate);

                window.location.href = url;
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
            }
        }
    }
</script>

<%@ include file="/WEB-INF/template/footer.jsp" %>
