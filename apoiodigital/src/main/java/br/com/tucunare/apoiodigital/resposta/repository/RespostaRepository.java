package br.com.tucunare.apoiodigital.resposta.repository;

import br.com.tucunare.apoiodigital.pedido.data.Pedido;
import br.com.tucunare.apoiodigital.resposta.data.Resposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RespostaRepository extends JpaRepository<Resposta, UUID> {
    List<Resposta> findByPedido(Pedido pedido);
    
    @Query("SELECT r.mensagem as mensagem, r.timestamp as timestamp, r.raciocinio as raciocinio " +
            "FROM Resposta r WHERE r.pedido.id = :id " +
            "ORDER BY r.timestamp DESC")
    List<Object[]> listarRespostaPorIdPedido(@Param("id") UUID pedidoId);
}
