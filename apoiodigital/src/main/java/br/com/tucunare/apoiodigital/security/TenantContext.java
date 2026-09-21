package br.com.tucunare.apoiodigital.security;

import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.cliente.exception.ClienteNaoAutenticadoException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class TenantContext {

    public Cliente getClienteAtual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Cliente cliente)) {
            throw new ClienteNaoAutenticadoException();
        }
        return cliente;
    }
}
