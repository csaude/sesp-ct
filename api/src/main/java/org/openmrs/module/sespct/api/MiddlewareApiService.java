package org.openmrs.module.sespct.api;

import java.util.List;

public interface MiddlewareApiService {

    String login();

    /**
     * Fetches and decrypts Pedidos from the middleware.
     * @param authToken The auth token.
     * @return A List of Pedido DTOs
     */
    List<PedidoDTO> fetchPedidos(String authToken);

    /**
     * Fetches and decrypts Respostas from the middleware.
     * @param authToken The auth token.
     * @return A List of Resposta DTOs
     */
    List<RespostaDTO> fetchRespostas(String authToken);

    /**
     * Marks a list of Pedidos as consumed on the server.
     * @param pedidoUuids List of UUIDs to mark.
     * @param authToken The auth token.
     * @return true if successful.
     */
    boolean markPedidosAsConsumed(List<String> pedidoUuids, String authToken);

    /**
     * Marks a list of Respostas as consumed on the server.
     * @param respostaUuids List of UUIDs to mark.
     * @param authToken The auth token.
     * @return true if successful.
     */
    boolean markRespostasAsConsumed(List<String> respostaUuids, String authToken);
}