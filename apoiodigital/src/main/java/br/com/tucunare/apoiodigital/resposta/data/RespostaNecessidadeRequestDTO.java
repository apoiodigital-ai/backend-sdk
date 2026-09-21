package br.com.tucunare.apoiodigital.resposta.data;

import java.util.UUID;

public record RespostaNecessidadeRequestDTO(String userId, UUID idPedido, String resposta) {
}
