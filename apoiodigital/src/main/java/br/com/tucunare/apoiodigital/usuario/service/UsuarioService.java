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

    public Usuario resolverOuCriar(String externalId, Cliente cliente) {
        if (externalId == null || externalId.isBlank()) {
            throw new UsuarioDoesNotExistException();
        }
        return usuarioRepository.findByExternalIdAndClienteId(externalId, cliente.getId())
                .orElseGet(() -> criar(externalId, null, cliente));
    }

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

    private Usuario criar(String externalId, String nome, Cliente cliente) {
        try {
            return usuarioRepository.saveAndFlush(new Usuario(externalId, nome, cliente));
        } catch (DataIntegrityViolationException e) {
            return usuarioRepository.findByExternalIdAndClienteId(externalId, cliente.getId())
                    .orElseThrow(() -> e);
        }
    }
}
