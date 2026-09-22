package br.com.tucunare.apoiodigital.resposta.data;

import br.com.tucunare.apoiodigital.agent.CapturedElementDTO;

import java.util.List;
import java.util.UUID;

public record AcharRespostaRequestDTO(String userId, String prompt, List<CapturedElementDTO> elementos, UUID idPedido) {
}
