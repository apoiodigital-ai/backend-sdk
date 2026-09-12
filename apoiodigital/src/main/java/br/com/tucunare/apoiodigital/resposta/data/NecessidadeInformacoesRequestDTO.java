package br.com.tucunare.apoiodigital.resposta.data;

import br.com.tucunare.apoiodigital.agent.CapturedElementDTO;

import java.util.List;

/**
 * Body of {@code POST /resposta/validar/necessidade-informacoes} (the Gatekeeper entry point).
 * {@code userId} is the partner-supplied anonymized identifier forwarded verbatim by the SDK
 * (doc V2 §2.4) — an opaque string, not a UUID minted by this backend; the Usuario row is
 * auto-provisioned for the authenticated tenant on first use.
 */
public record NecessidadeInformacoesRequestDTO(String userId, String prompt, List<CapturedElementDTO> elementos) {
}
