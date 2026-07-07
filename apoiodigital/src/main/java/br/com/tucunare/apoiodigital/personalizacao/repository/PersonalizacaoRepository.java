package br.com.tucunare.apoiodigital.personalizacao.repository;

import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.personalizacao.data.Personalizacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PersonalizacaoRepository extends JpaRepository<Personalizacao, UUID> {
    List<Personalizacao> findByCliente(Cliente cliente);
}
