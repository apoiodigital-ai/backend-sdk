package br.com.tucunare.apoiodigital.resposta.data;

import br.com.tucunare.apoiodigital.agent.CapturedElementDTO;

import java.util.List;

/**
 * Body of {@code POST /resposta/achar-resposta}. {@code prompt} is expected to already carry
 * any clarification the user gave in the validar/* loop (that loop only threads state through
 * idPedido between its own two endpoints — this endpoint takes no idPedido, matching the SDK
 * contract exactly, so it is the caller's responsibility to fold the resolved answer into the
 * prompt it sends here). {@code userId} is the partner-supplied anonymized identifier, same as
 * in {@link NecessidadeInformacoesRequestDTO}.
 */
public record AcharRespostaRequestDTO(String userId, String prompt, List<CapturedElementDTO> elementos) {
}
