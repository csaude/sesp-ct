<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<style>
    .search-container {
        background-color: #f8f9fa;
        padding: 20px;
        border-radius: 5px;
        margin-bottom: 20px;
        border: 1px solid #dee2e6;
    }

    .search-title {
        background-color: #009d8e;
        color: white;
        padding: 10px 15px;
        margin: -20px -20px 20px -20px;
        border-radius: 5px 5px 0 0;
        font-weight: bold;
        font-size: 16px;
    }

    .form-row {
        display: flex;
        gap: 15px;
        margin-bottom: 15px;
        align-items: end;
        flex-wrap: wrap;
    }

    .form-group {
        flex: 1;
        min-width: 200px;
    }

    .form-group label {
        display: block;
        margin-bottom: 5px;
        font-weight: bold;
        color: #333;
    }

    .form-group input,
    .form-group select {
        width: 100%;
        padding: 8px;
        border: 1px solid #ccc;
        border-radius: 4px;
        font-size: 14px;
        box-sizing: border-box;
    }

    .btn-search {
        background-color: #007bff;
        color: white;
        padding: 10px 25px;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-size: 14px;
        margin-right: 10px;
    }

    .btn-clear {
        background-color: #6c757d;
        color: white;
        padding: 10px 25px;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-size: 14px;
    }

    .results-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;
        padding: 15px;
        background-color: #e9ecef;
        border-radius: 5px;
    }

    .results-info {
        font-weight: bold;
        color: #495057;
    }

    .export-controls {
        display: flex;
        align-items: center;
        gap: 15px;
    }

    .btn-export {
        background-color: #28a745;
        color: white;
        padding: 8px 15px;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-size: 14px;
        text-decoration: none;
    }

    .pagination-controls {
        display: flex;
        align-items: center;
        gap: 10px;
    }

    .pagination-controls label {
        font-weight: bold;
        color: #495057;
    }

    .pagination-controls select {
        padding: 5px 8px;
        border: 1px solid #ccc;
        border-radius: 4px;
        font-size: 14px;
    }

    .results-table {
        width: 100%;
        border-collapse: collapse;
        margin-bottom: 20px;
        font-size: 12px;
        background-color: white;
    }

    .results-table th,
    .results-table td {
        border: 1px solid #ddd;
        padding: 8px;
        text-align: left;
        vertical-align: middle;
    }

    .results-table th {
        background-color: #f8f9fa;
        font-weight: bold;
        color: #495057;
        text-align: center;
    }

    .results-table tbody tr:nth-child(even) {
        background-color: #f9f9f9;
    }

    .results-table tbody tr:hover {
        background-color: #e8f4fd;
    }

    .ncft-link {
        color: #007bff;
        text-decoration: none;
        font-weight: bold;
    }

    .ncft-link:hover {
        text-decoration: underline;
    }

    .status-badge {
        padding: 4px 8px;
        border-radius: 12px;
        font-size: 11px;
        font-weight: bold;
        text-align: center;
        display: inline-block;
        min-width: 80px;
    }

    .status-sem-resposta {
        background-color: #ffc107;
        color: white;
    }

    .status-aprovado {
        background-color: #28a745;
        color: white;
    }

    .status-adiado {
        background-color: #fd7e14;
        color: white;
    }

    .status-nao-processado {
        background-color: #dc3545;
        color: white;
    }

    .btn-action {
        background-color: #17a2b8;
        color: white;
        padding: 4px 8px;
        border: none;
        border-radius: 3px;
        cursor: pointer;
        font-size: 11px;
    }

    .btn-action:hover {
        background-color: #138496;
    }

    .no-results {
        text-align: center;
        padding: 40px;
        background-color: #fff3cd;
        border: 1px solid #ffeaa7;
        border-radius: 5px;
        color: #856404;
        font-size: 16px;
        font-weight: bold;
        margin: 20px 0;
    }

    .pagination-footer {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 10px;
        margin-top: 20px;
        padding: 15px;
        background-color: #f8f9fa;
        border-radius: 5px;
    }

    .pagination-footer button {
        padding: 8px 12px;
        border: 1px solid #ddd;
        background-color: white;
        cursor: pointer;
        border-radius: 4px;
    }

    .pagination-footer button:hover {
        background-color: #e9ecef;
    }

    .pagination-footer button.active {
        background-color: #007bff;
        color: white;
        border-color: #007bff;
    }

    .page-title {
        margin-bottom: 20px;
        color: #495057;
    }
</style>

<h2 class="page-title">Pesquisa de Casos FT</h2>

<div class="search-container">
    <div class="search-title">Filtros de Pesquisa</div>

    <form>
        <div class="form-row">
            <div class="form-group">
                <label for="dataInicio">Data Início:</label>
                <input type="date" id="dataInicio" name="dataInicio" />
            </div>
            <div class="form-group">
                <label for="dataFim">Data Fim:</label>
                <input type="date" id="dataFim" name="dataFim" />
            </div>
            <div class="form-group">
                <label for="estado">Estado:</label>
                <select id="estado" name="estado">
                    <option value="">Seleccionar...</option>
                    <option value="sem_resposta">Sem resposta</option>
                    <option value="aprovado">Aprovado</option>
                    <option value="adiado">Adiado</option>
                    <option value="nao_processado">Não processado</option>
                </select>
            </div>
        </div>

        <div class="form-row">
            <div class="form-group">
                <label for="ncft">NCFT:</label>
                <input type="text" id="ncft" name="ncft" placeholder="Digite o NCFT" />
            </div>
            <div class="form-group">
                <label for="nid">NID:</label>
                <input type="text" id="nid" name="nid" placeholder="Digite o NID" />
            </div>
            <div class="form-group">
                <label for="us">Unidade Sanitária:</label>
                <select id="us" name="us">
                    <option value="">Seleccionar US...</option>
                    <option value="us001">Hospital Central de Maputo (HCM001)</option>
                    <option value="us002">Centro de Saúde da Polana (CSP002)</option>
                    <option value="us003">Hospital Militar de Maputo (HMM003)</option>
                    <option value="us004">Centro de Saúde de Malhangalene (CSM004)</option>
                    <option value="us005">Hospital Geral José Macamo (HJM005)</option>
                </select>
            </div>
        </div>

        <div class="form-row">
            <div style="flex: 1;"></div>
            <div>
                <button type="button" class="btn-search">Pesquisar</button>
                <button type="button" class="btn-clear">Limpar</button>
            </div>
        </div>
    </form>
</div>

<div class="results-header">
    <div class="results-info">
        Resultados encontrados: 45 registos
    </div>
    <div class="export-controls">
        <div class="pagination-controls">
            <label for="rowsPerPage">Linhas por página:</label>
            <select id="rowsPerPage" name="rowsPerPage">
                <option value="20" selected>20</option>
                <option value="50">50</option>
                <option value="75">75</option>
                <option value="100">100</option>
            </select>
        </div>
        <button class="btn-export">Exportar Excel</button>
    </div>
</div>

<div style="overflow-x: auto;">
    <table class="results-table">
        <thead>
        <tr>
            <th>US</th>
            <th>NID</th>
            <th>NCFT</th>
            <th>Iniciais</th>
            <th>Sexo</th>
            <th>Idade</th>
            <th>Submissão</th>
            <th>Data Resposta</th>
            <th>Sincronização</th>
            <th>Estado</th>
            <th>Causa Não Processamento</th>
            <th>Acção</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>Hospital Central de Maputo (HCM001)</td>
            <td>1234567890123</td>
            <td><a href="#" class="ncft-link">FT2024001234</a></td>
            <td>A.M.S.</td>
            <td>F</td>
            <td>32</td>
            <td>15/03/2024</td>
            <td>18/03/2024</td>
            <td>19/03/2024</td>
            <td><span class="status-badge status-aprovado">Aprovado</span></td>
            <td>-</td>
            <td>-</td>
        </tr>
        <tr>
            <td>Centro de Saúde da Polana (CSP002)</td>
            <td>9876543210987</td>
            <td><a href="#" class="ncft-link">FT2024001235</a></td>
            <td>J.C.M.</td>
            <td>M</td>
            <td>28</td>
            <td>14/03/2024</td>
            <td>-</td>
            <td>15/03/2024</td>
            <td><span class="status-badge status-sem-resposta">Sem resposta</span></td>
            <td>-</td>
            <td>-</td>
        </tr>
        <tr>
            <td>Hospital Militar de Maputo (HMM003)</td>
            <td>5555666677778</td>
            <td><a href="#" class="ncft-link">FT2024001236</a></td>
            <td>M.F.L.</td>
            <td>F</td>
            <td>45</td>
            <td>13/03/2024</td>
            <td>16/03/2024</td>
            <td>17/03/2024</td>
            <td><span class="status-badge status-adiado">Adiado</span></td>
            <td>-</td>
            <td>-</td>
        </tr>
        <tr>
            <td>Centro de Saúde de Malhangalene (CSM004)</td>
            <td>1111222233334</td>
            <td><a href="#" class="ncft-link">FT2024001237</a></td>
            <td>R.A.T.</td>
            <td>M</td>
            <td>38</td>
            <td>12/03/2024</td>
            <td>-</td>
            <td>13/03/2024</td>
            <td><span class="status-badge status-nao-processado">Não processado</span></td>
            <td>NID não encontrado</td>
            <td><button class="btn-action">Mapear NID</button></td>
        </tr>
        <tr>
            <td>Hospital Geral José Macamo (HJM005)</td>
            <td>7777888899990</td>
            <td><a href="#" class="ncft-link">FT2024001238</a></td>
            <td>C.D.S.</td>
            <td>F</td>
            <td>29</td>
            <td>11/03/2024</td>
            <td>14/03/2024</td>
            <td>15/03/2024</td>
            <td><span class="status-badge status-aprovado">Aprovado</span></td>
            <td>-</td>
            <td>-</td>
        </tr>
        <tr>
            <td>Hospital Central de Maputo (HCM001)</td>
            <td>3333444455556</td>
            <td><a href="#" class="ncft-link">FT2024001239</a></td>
            <td>P.M.R.</td>
            <td>M</td>
            <td>52</td>
            <td>10/03/2024</td>
            <td>-</td>
            <td>11/03/2024</td>
            <td><span class="status-badge status-nao-processado">Não processado</span></td>
            <td>NID duplicado</td>
            <td><button class="btn-action">Mapear NID</button></td>
        </tr>
        <tr>
            <td>Centro de Saúde da Polana (CSP002)</td>
            <td>6666777788889</td>
            <td><a href="#" class="ncft-link">FT2024001240</a></td>
            <td>L.N.F.</td>
            <td>F</td>
            <td>41</td>
            <td>09/03/2024</td>
            <td>12/03/2024</td>
            <td>13/03/2024</td>
            <td><span class="status-badge status-adiado">Adiado</span></td>
            <td>-</td>
            <td>-</td>
        </tr>
        <tr>
            <td>Hospital Militar de Maputo (HMM003)</td>
            <td>9999000011112</td>
            <td><a href="#" class="ncft-link">FT2024001241</a></td>
            <td>S.B.M.</td>
            <td>M</td>
            <td>33</td>
            <td>08/03/2024</td>
            <td>-</td>
            <td>09/03/2024</td>
            <td><span class="status-badge status-sem-resposta">Sem resposta</span></td>
            <td>-</td>
            <td>-</td>
        </tr>
        <tr>
            <td>Centro de Saúde de Malhangalene (CSM004)</td>
            <td>2222333344445</td>
            <td><a href="#" class="ncft-link">FT2024001242</a></td>
            <td>T.G.L.</td>
            <td>F</td>
            <td>26</td>
            <td>07/03/2024</td>
            <td>10/03/2024</td>
            <td>11/03/2024</td>
            <td><span class="status-badge status-aprovado">Aprovado</span></td>
            <td>-</td>
            <td>-</td>
        </tr>
        <tr>
            <td>Hospital Geral José Macamo (HJM005)</td>
            <td>8888999900001</td>
            <td><a href="#" class="ncft-link">FT2024001243</a></td>
            <td>H.V.C.</td>
            <td>M</td>
            <td>47</td>
            <td>06/03/2024</td>
            <td>-</td>
            <td>07/03/2024</td>
            <td><span class="status-badge status-nao-processado">Não processado</span></td>
            <td>NID não encontrado</td>
            <td><button class="btn-action">Mapear NID</button></td>
        </tr>
        <tr>
            <td>Hospital Central de Maputo (HCM001)</td>
            <td>4444555566667</td>
            <td><a href="#" class="ncft-link">FT2024001244</a></td>
            <td>D.R.S.</td>
            <td>F</td>
            <td>39</td>
            <td>05/03/2024</td>
            <td>08/03/2024</td>
            <td>09/03/2024</td>
            <td><span class="status-badge status-aprovado">Aprovado</span></td>
            <td>-</td>
            <td>-</td>
        </tr>
        <tr>
            <td>Centro de Saúde da Polana (CSP002)</td>
            <td>1357924680135</td>
            <td><a href="#" class="ncft-link">FT2024001245</a></td>
            <td>K.P.M.</td>
            <td>M</td>
            <td>35</td>
            <td>04/03/2024</td>
            <td>-</td>
            <td>05/03/2024</td>
            <td><span class="status-badge status-sem-resposta">Sem resposta</span></td>
            <td>-</td>
            <td>-</td>
        </tr>
        <tr>
            <td>Hospital Militar de Maputo (HMM003)</td>
            <td>2468013579246</td>
            <td><a href="#" class="ncft-link">FT2024001246</a></td>
            <td>F.Q.N.</td>
            <td>F</td>
            <td>31</td>
            <td>03/03/2024</td>
            <td>06/03/2024</td>
            <td>07/03/2024</td>
            <td><span class="status-badge status-adiado">Adiado</span></td>
            <td>-</td>
            <td>-</td>
        </tr>
        <tr>
            <td>Centro de Saúde de Malhangalene (CSM004)</td>
            <td>9630741852963</td>
            <td><a href="#" class="ncft-link">FT2024001247</a></td>
            <td>G.L.T.</td>
            <td>M</td>
            <td>44</td>
            <td>02/03/2024</td>
            <td>-</td>
            <td>03/03/2024</td>
            <td><span class="status-badge status-nao-processado">Não processado</span></td>
            <td>NID duplicado</td>
            <td><button class="btn-action">Mapear NID</button></td>
        </tr>
        <tr>
            <td>Hospital Geral José Macamo (HJM005)</td>
            <td>7410852963741</td>
            <td><a href="#" class="ncft-link">FT2024001248</a></td>
            <td>B.W.K.</td>
            <td>F</td>
            <td>27</td>
            <td>01/03/2024</td>
            <td>04/03/2024</td>
            <td>05/03/2024</td>
            <td><span class="status-badge status-aprovado">Aprovado</span></td>
            <td>-</td>
            <td>-</td>
        </tr>
        <tr>
            <td>Hospital Central de Maputo (HCM001)</td>
            <td>1472583690147</td>
            <td><a href="#" class="ncft-link">FT2024001249</a></td>
            <td>V.Y.P.</td>
            <td>M</td>
            <td>50</td>
            <td>28/02/2024</td>
            <td>-</td>
            <td>29/02/2024</td>
            <td><span class="status-badge status-sem-resposta">Sem resposta</span></td>
            <td>-</td>
            <td>-</td>
        </tr>
        <tr>
            <td>Centro de Saúde da Polana (CSP002)</td>
            <td>3691472583691</td>
            <td><a href="#" class="ncft-link">FT2024001250</a></td>
            <td>Q.X.R.</td>
            <td>F</td>
            <td>42</td>
            <td>27/02/2024</td>
            <td>01/03/2024</td>
            <td>02/03/2024</td>
            <td><span class="status-badge status-aprovado">Aprovado</span></td>
            <td>-</td>
            <td>-</td>
        </tr>
        <tr>
            <td>Hospital Militar de Maputo (HMM003)</td>
            <td>8520963741852</td>
            <td><a href="#" class="ncft-link">FT2024001251</a></td>
            <td>N.J.H.</td>
            <td>M</td>
            <td>36</td>
            <td>26/02/2024</td>
            <td>-</td>
            <td>27/02/2024</td>
            <td><span class="status-badge status-nao-processado">Não processado</span></td>
            <td>NID não encontrado</td>
            <td><button class="btn-action">Mapear NID</button></td>
        </tr>
        <tr>
            <td>Centro de Saúde de Malhangalene (CSM004)</td>
            <td>9517534862951</td>
            <td><a href="#" class="ncft-link">FT2024001252</a></td>
            <td>E.U.I.</td>
            <td>F</td>
            <td>30</td>
            <td>25/02/2024</td>
            <td>28/02/2024</td>
            <td>29/02/2024</td>
            <td><span class="status-badge status-adiado">Adiado</span></td>
            <td>-</td>
            <td>-</td>
        </tr>
        <tr>
            <td>Hospital Geral José Macamo (HJM005)</td>
            <td>7531594862753</td>
            <td><a href="#" class="ncft-link">FT2024001253</a></td>
            <td>O.A.Z.</td>
            <td>M</td>
            <td>48</td>
            <td>24/02/2024</td>
            <td>27/02/2024</td>
            <td>28/02/2024</td>
            <td><span class="status-badge status-aprovado">Aprovado</span></td>
            <td>-</td>
            <td>-</td>
        </tr>
        </tbody>
    </table>
</div>

<div class="pagination-footer">
    <button>&laquo; Anterior</button>
    <button class="active">1</button>
    <button>2</button>
    <button>3</button>
    <button>Seguinte &raquo;</button>
    <span style="margin-left: 20px; color: #666;">
        Página 1 de 3 (45 registos)
    </span>
</div>

<!-- Uncomment to show no results message -->
<!--
<div class="no-results">
Nenhum caso encontrado com os critérios selecionados.
</div>
-->

<script>
    // Simple JavaScript for form interactions (non-functional, just for UI feedback)
    document.addEventListener('DOMContentLoaded', function() {
        // Auto-remove spaces from NCFT and NID fields
        const ncftField = document.getElementById('ncft');
        const nidField = document.getElementById('nid');

        if (ncftField) {
            ncftField.addEventListener('input', function() {
                this.value = this.value.replace(/\s/g, '');
            });
        }

        if (nidField) {
            nidField.addEventListener('input', function() {
                this.value = this.value.replace(/\s/g, '');
            });
        }

        // Button click handlers (non-functional)
        document.querySelector('.btn-search')?.addEventListener('click', function() {
            console.log('Search button clicked');
        });

        document.querySelector('.btn-clear')?.addEventListener('click', function() {
            console.log('Clear button clicked');
            document.querySelector('form').reset();
        });

        document.querySelector('.btn-export')?.addEventListener('click', function() {
            console.log('Export button clicked');
        });

        // Action button handlers
        document.querySelectorAll('.btn-action').forEach(button => {
            button.addEventListener('click', function() {
                console.log('Map NID button clicked');
            });
        });
    });
</script>

<%@ include file="/WEB-INF/template/footer.jsp" %>