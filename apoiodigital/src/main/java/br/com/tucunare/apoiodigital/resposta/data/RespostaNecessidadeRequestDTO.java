package br.com.tucunare.apoiodigital.resposta.data;

import java.util.UUID;

/**
 * Body of {@code POST /resposta/validar/resposta-necessidade}. {@code userId} is the
 * partner-supplied anonymized identifier, same as in {@link NecessidadeInformacoesRequestDTO}.
 */
public record RespostaNecessidadeRequestDTO(String userId, UUID idPedido, String resposta) {
}
