package br.com.tucunare.apoiodigital.resposta.data;

/**
 * @param viewID The chosen element's {@code viewId}, echoed back verbatim from the elementos
 *               the SDK itself sent in the request — the SDK uses it to look the element up in
 *               its own index and position the spotlight. (Response field name stays
 *               {@code viewID} per the documented SDK contract.)
 * @param mensagemVozUrl Absolute URL to the synthesized audio for mensagem_voz, or null when TTS
 *                       synthesis failed or is disabled — the written instruction still stands
 *                       on its own in that case.
 */
public record AcharRespostaResponseDTO(String viewID, String mensagem_escrita, String mensagem_voz_url, Double precisao) {
}
