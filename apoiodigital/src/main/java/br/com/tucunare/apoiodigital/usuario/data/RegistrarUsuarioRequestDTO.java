package br.com.tucunare.apoiodigital.usuario.data;

/**
 * Body of {@code POST /usuario/registrar}. Optional pre-registration: {@code userId} is the
 * partner-minted anonymized identifier (the same value the SDK forwards on every
 * {@code /resposta/*} call — which auto-provisions the Usuario anyway, so calling this
 * endpoint is never required). {@code nome} is optional and, per the anonymization
 * requirement, should never be a real name tying back to a real-world identity.
 */
public record RegistrarUsuarioRequestDTO(String userId, String nome) {
}
