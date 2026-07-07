package br.com.tucunare.apoiodigital.pedido.repository;

import br.com.tucunare.apoiodigital.pedido.data.Pedido;
import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
    List<Pedido> findByUsuario(Usuario usuario);
    Optional<Pedido> findFirstByPromptAndUsuarioOrderByTimestampDesc(String prompt, Usuario usuario);
}
