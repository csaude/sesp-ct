<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<jsp:useBean id="currentDate" class="java.util.Date" />

<openmrs:require privilege="Permite acesso à aplicação SESP-CT" otherwise="/login.htm"
                 redirect="/module/sespct/sespct.form" />

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sespct/css/sespct.css" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sespct/css/datatables.net/1.13.2/jquery.dataTables.min.css" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sespct/css/buttons/2.3.4/buttons.dataTables.min.css" />

<h2><openmrs:message code="sespct.title"/></h2>

<%--<p>--%>
<%--    <small>--%>
<%--        <small>--%>
<%--            <fmt:message key="sespct.lastSync">--%>
<%--                <fmt:param value="<fmt:formatDate value='${currentDate}' pattern='dd \'de\' MMMM \'de\' yyyy' />" />--%>
<%--                <fmt:param value="<fmt:formatDate value='${currentDate}' pattern='HH:mm\'h\'' />" />--%>
<%--                <fmt:param value="10 mins" />--%>
<%--            </fmt:message>--%>
<%--        </small>--%>
<%--    </small>--%>
<%--</p>--%>

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
            <c:when test="${empty sespctRequests}">
                <tr>
                    <td colspan="12" style="text-align: center; font-style: italic;">
                        Nenhum caso encontrado com os critérios selecionados.
                    </td>
                </tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="request" items="${sespctRequests}">
                    <tr>
                        <td>${request.unidadeSanitaria} (${request.codigoUnidadeSanitaria})</td>
                        <td>${request.nid}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/module/sespct/viewRequest.form?pedidoId=${request.pedidoId}">
                                    ${request.pedidoId}
                            </a>
                        </td>
                        <td>${request.iniciais}</td>
                        <td>
                            <c:choose>
                                <c:when test="${request.sexo == 'masculino'}">M</c:when>
                                <c:when test="${request.sexo == 'feminino'}">F</c:when>
                                <c:otherwise>${request.sexo}</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <fmt:formatNumber value="${request.idade}" maxFractionDigits="0" />
                        </td>
                        <td>
                            <fmt:formatDate value="${request.dataSubmissao}" pattern="dd/MM/yyyy" />
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${request.estado == 'Sem resposta'}">-</c:when>
                                <c:otherwise>
                                    <fmt:formatDate value="${request.dateChanged}" pattern="dd/MM/yyyy" />
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <fmt:formatDate value="${request.dateChanged}" pattern="dd/MM/yyyy" />
                        </td>
                        <td>
                            <span class="status-${fn:replace(request.estado, ' ', '-')}">
                                    ${request.estado}
                            </span>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${request.estado == 'Não processado'}">
                                    NID não encontrado
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:if test="${request.estado == 'Não processado'}">
                                <a href="#" onclick="mapNid('${request.nid}')">
                                    <openmrs:message code="sespct.mapNid"/>
                                </a>
                            </c:if>
                            <c:if test="${request.estado != 'Não processado'}">-</c:if>
                        </td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>
    <br />
    <div class="submit-btn center">
        <button onclick="window.location.href = '${pageContext.request.contextPath}/module/sespct/manageftcases/export.form'">
            <openmrs:message code="sespct.export.button"/>
        </button>
    </div>
</div>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sespct/js/datatables.net/1.13.2/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sespct/js/buttons/2.3.4/dataTables.buttons.min.js" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sespct/js/buttons/2.3.4/buttons.html5.min.js" />

<script type="text/javascript">
    window.addEventListener("DOMContentLoaded", () => {
        // Check what's available and use it
        var $ = window.jQuery || window.jq || window.$;

        if ($) {
            $("#ftResultsTable").DataTable({
                pageLength: 20,
                lengthMenu: [20, 50, 75, 100],
                order: [[6, 'desc']],
                language: {
                    emptyTable: "Nenhum caso encontrado com os critérios selecionados."
                }
            });
        } else {
            console.error("No jQuery available!");
        }
    });
</script>

<%@ include file="/WEB-INF/template/footer.jsp" %>