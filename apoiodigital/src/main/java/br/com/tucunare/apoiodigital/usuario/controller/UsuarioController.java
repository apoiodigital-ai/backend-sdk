package br.com.tucunare.apoiodigital.usuario.controller;

import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.security.TenantContext;
import br.com.tucunare.apoiodigital.usuario.data.RegistrarUsuarioRequestDTO;
import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import br.com.tucunare.apoiodigital.usuario.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final TenantContext tenantContext;

    public UsuarioController(UsuarioService usuarioService, TenantContext tenantContext) {
        this.usuarioService = usuarioService;
        this.tenantContext = tenantContext;
    }

    @PostMapping("/registrar")
    public ResponseEntity<Usuario> registrar(@RequestBody RegistrarUsuarioRequestDTO request) {
        Cliente cliente = tenantContext.getClienteAtual();
        Usuario usuario = usuarioService.registrar(request.userId(), request.nome(), cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }
}
