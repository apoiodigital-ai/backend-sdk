package br.com.tucunare.apoiodigital.componente.repository;

import br.com.tucunare.apoiodigital.componente.data.Componente;
import br.com.tucunare.apoiodigital.resposta.data.Resposta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ComponenteRepository extends JpaRepository<Componente, UUID> {
    List<Componente> findByResposta(Resposta resposta);
}
