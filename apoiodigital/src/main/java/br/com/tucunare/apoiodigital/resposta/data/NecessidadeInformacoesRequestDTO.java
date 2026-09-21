package br.com.tucunare.apoiodigital.resposta.data;

import br.com.tucunare.apoiodigital.agent.CapturedElementDTO;

import java.util.List;

public record NecessidadeInformacoesRequestDTO(String userId, String prompt, List<CapturedElementDTO> elementos) {
}
