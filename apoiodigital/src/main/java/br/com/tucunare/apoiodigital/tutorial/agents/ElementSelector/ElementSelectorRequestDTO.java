package br.com.tucunare.apoiodigital.tutorial.agents.ElementSelector;

import br.com.tucunare.apoiodigital.agent.CapturedElementDTO;

import java.util.List;

public record ElementSelectorRequestDTO(String prompt, List<CapturedElementDTO> elementos) {
}
