<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

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

<%@ include file="../common/alertBox.jspf" %>

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
        <tr>
            <td>US Maputo (001)</td>
            <td>123456789</td>
            <td><a href="#">NCFT001</a></td>
            <td>AJ</td>
            <td>M</td>
            <td>32</td>
            <td>01/07/2025</td>
            <td>05/07/2025</td>
            <td>06/07/2025</td>
            <td>Aprovado</td>
            <td>-</td>
            <td>-</td>
        </tr>
        <tr>
            <td>US Beira (002)</td>
            <td>987654321</td>
            <td><a href="#">NCFT002</a></td>
            <td>BN</td>
            <td>F</td>
            <td>28</td>
            <td>15/06/2025</td>
            <td>20/06/2025</td>
            <td>21/06/2025</td>
            <td>Não processado</td>
            <td>NID não encontrado</td>
            <td><a href="#"><openmrs:message code="sespct.mapNid"/></a></td>
        </tr>
        <tr>
            <td>US Nampula (003)</td>
            <td>111222333</td>
            <td><a href="#">NCFT003</a></td>
            <td>CL</td>
            <td>M</td>
            <td>45</td>
            <td>02/06/2025</td>
            <td>-</td>
            <td>04/06/2025</td>
            <td>Sem resposta</td>
            <td>-</td>
            <td>-</td>
        </tr>
        <tr>
            <td>US Xai-Xai (004)</td>
            <td>444555666</td>
            <td><a href="#">NCFT004</a></td>
            <td>DM</td>
            <td>F</td>
            <td>39</td>
            <td>10/05/2025</td>
            <td>12/05/2025</td>
            <td>13/05/2025</td>
            <td>Adiado</td>
            <td>-</td>
            <td>-</td>
        </tr>
        <tr>
            <td>US Chimoio (005)</td>
            <td>777888999</td>
            <td><a href="#">NCFT005</a></td>
            <td>EP</td>
            <td>M</td>
            <td>50</td>
            <td>03/05/2025</td>
            <td>06/05/2025</td>
            <td>07/05/2025</td>
            <td>Aprovado</td>
            <td>-</td>
            <td>-</td>
        </tr>
        </tbody>
    </table>
    <br />
    <div class="submit-btn center">
        <button id="exportButton">
            <openmrs:message code="sespct.export.button"/>
        </button>
    </div>
</div>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sespct/js/datatables.net/1.13.2/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sespct/js/buttons/2.3.4/dataTables.buttons.min.js" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sespct/js/buttons/2.3.4/buttons.html5.min.js" />

<script type="text/javascript">
    window.addEventListener("DOMContentLoaded", () => {
        jQuery("#ftResultsTable").DataTable({
            pageLength: 20,
            lengthMenu: [20, 50, 75, 100],
            language: {
                emptyTable: "Nenhum caso encontrado com os critérios selecionados."
            }
        });
        
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
