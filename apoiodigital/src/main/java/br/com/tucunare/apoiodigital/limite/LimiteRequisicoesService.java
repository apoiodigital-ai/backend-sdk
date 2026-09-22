package br.com.tucunare.apoiodigital.limite;

import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.limite.exception.LimiteRequisicoesExcedidoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;

@Service
public class LimiteRequisicoesService {

    private final ContadorJanelaFixa contador;
    private final int maximoPorUsuario;
    private final Duration janelaPorUsuario;
    private final int maximoPorChave;
    private final Duration janelaPorChave;

    @Autowired
    public LimiteRequisicoesService(
            @Value("${app.rate-limit.por-usuario.maximo:20}") int maximoPorUsuario,
            @Value("${app.rate-limit.por-usuario.janela-segundos:60}") long janelaPorUsuarioSegundos,
            @Value("${app.rate-limit.por-chave.maximo:600}") int maximoPorChave,
            @Value("${app.rate-limit.por-chave.janela-segundos:60}") long janelaPorChaveSegundos
    ) {
        this(
                new ContadorJanelaFixa(Clock.systemUTC()),
                maximoPorUsuario, Duration.ofSeconds(janelaPorUsuarioSegundos),
                maximoPorChave, Duration.ofSeconds(janelaPorChaveSegundos)
        );
    }

    LimiteRequisicoesService(
            ContadorJanelaFixa contador,
            int maximoPorUsuario,
            Duration janelaPorUsuario,
            int maximoPorChave,
            Duration janelaPorChave
    ) {
        this.contador = contador;
        this.maximoPorUsuario = maximoPorUsuario;
        this.janelaPorUsuario = janelaPorUsuario;
        this.maximoPorChave = maximoPorChave;
        this.janelaPorChave = janelaPorChave;
    }

    public DecisaoLimite consumirChave(Cliente cliente) {
        return contador.consumir("chave:" + cliente.getId(), maximoPorChave, janelaPorChave);
    }

    public void verificarUsuario(Cliente cliente, String userId) {
        if (userId == null || userId.isBlank()) {
            return;
        }
        DecisaoLimite decisao = contador.consumir(
                "usuario:" + cliente.getId() + ":" + userId, maximoPorUsuario, janelaPorUsuario
        );
        if (!decisao.permitido()) {
            throw new LimiteRequisicoesExcedidoException(decisao.segundosParaLiberar());
        }
    }
}
