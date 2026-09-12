package br.com.tucunare.apoiodigital.tutorial.agents.PendingValidator;

import br.com.tucunare.apoiodigital.agent.CapturedElementDTO;

import java.util.List;

public record PendingValidatorRequestDTO(String prompt, List<CapturedElementDTO> elementos) {
}
