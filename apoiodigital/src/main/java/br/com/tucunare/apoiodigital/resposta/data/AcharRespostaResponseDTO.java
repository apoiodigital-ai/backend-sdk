package br.com.tucunare.apoiodigital.resposta.data;

import java.util.UUID;

public record AcharRespostaResponseDTO(
        String viewID,
        String mensagem_escrita,
        String mensagem_voz_url,
        Double precisao,
        UUID idResposta
) {
}
