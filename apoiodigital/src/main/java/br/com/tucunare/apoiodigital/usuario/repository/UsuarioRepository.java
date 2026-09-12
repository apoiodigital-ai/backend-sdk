package br.com.tucunare.apoiodigital.usuario.repository;

import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    /**
     * The only safe way to resolve a client-supplied {@code userId}: the lookup is scoped to
     * the authenticated tenant, so an external id belonging to another partner's user never
     * resolves here (same anti-IDOR posture as the old findByIdAndClienteId, applied to the
     * partner-minted identifier the SDK actually sends).
     */
    Optional<Usuario> findByExternalIdAndClienteId(String externalId, UUID clienteId);
}
