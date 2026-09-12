package br.com.tucunare.apoiodigital.usuario.service;

import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import br.com.tucunare.apoiodigital.usuario.exception.UsuarioDoesNotExistException;
import br.com.tucunare.apoiodigital.usuario.repository.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Resolves the partner-supplied anonymized {@code userId} to a Usuario scoped to the
     * given tenant, creating the row on first use. Auto-provisioning is what lets the SDK's
     * {@code registerUser({userId})} stay a purely local call (per the SDK contract): the
     * first {@code /resposta/*} request that mentions a new external id materializes it here.
     * A blank/absent id is rejected rather than provisioned.
     */
    public Usuario resolverOuCriar(String externalId, Cliente cliente) {
        if (externalId == null || externalId.isBlank()) {
            throw new UsuarioDoesNotExistException();
        }
        return usuarioRepository.findByExternalIdAndClienteId(externalId, cliente.getId())
                .orElseGet(() -> criar(externalId, null, cliente));
    }

    /**
     * Optional explicit pre-registration ({@code POST /usuario/registrar}) — idempotent: if
     * the external id already exists for this tenant, the existing row is returned (with
     * {@code nome} updated when a non-blank one is supplied).
     */
    public Usuario registrar(String externalId, String nome, Cliente cliente) {
        if (externalId == null || externalId.isBlank()) {
            throw new UsuarioDoesNotExistException();
        }
        Usuario usuario = usuarioRepository.findByExternalIdAndClienteId(externalId, cliente.getId())
                .orElseGet(() -> criar(externalId, nome, cliente));
        if (nome != null && !nome.isBlank() && !nome.equals(usuario.getNome())) {
            usuario.setNome(nome);
            usuario = usuarioRepository.save(usuario);
        }
        return usuario;
    }

    /**
     * Two concurrent first requests for the same external id can race past the find; the
     * unique (id_cliente, external_id) constraint turns the loser's insert into a
     * DataIntegrityViolationException, which we resolve by re-reading the winner's row.
     */
    private Usuario criar(String externalId, String nome, Cliente cliente) {
        try {
            return usuarioRepository.saveAndFlush(new Usuario(externalId, nome, cliente));
        } catch (DataIntegrityViolationException e) {
            return usuarioRepository.findByExternalIdAndClienteId(externalId, cliente.getId())
                    .orElseThrow(() -> e);
        }
    }
}
