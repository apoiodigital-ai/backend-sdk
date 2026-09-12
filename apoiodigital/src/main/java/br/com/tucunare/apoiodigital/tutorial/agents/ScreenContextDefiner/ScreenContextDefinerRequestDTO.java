package br.com.tucunare.apoiodigital.tutorial.agents.ScreenContextDefiner;

import br.com.tucunare.apoiodigital.agent.CapturedElementDTO;

public record ScreenContextDefinerRequestDTO(String prompt, String raciocinio, CapturedElementDTO elemento) {
}
