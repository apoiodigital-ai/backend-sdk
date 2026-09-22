package br.com.tucunare.apoiodigital.limite;

import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.limite.exception.LimiteRequisicoesExcedidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LimiteRequisicoesServiceTest {

    private RelogioAjustavel relogio;
    private LimiteRequisicoesService limite;
    private Cliente parceiroA;
    private Cliente parceiroB;

    @BeforeEach
    void setup() {
        relogio = new RelogioAjustavel();
        limite = new LimiteRequisicoesService(
                new ContadorJanelaFixa(relogio),
                3, Duration.ofSeconds(60),
                5, Duration.ofSeconds(60)
        );
        parceiroA = cliente();
        parceiroB = cliente();
    }

    private static Cliente cliente() {
        Cliente cliente = new Cliente("Parceiro", "chave-" + UUID.randomUUID(), "saude");
        cliente.setId(UUID.randomUUID());
        return cliente;
    }

    @Test
    @DisplayName("blocks a user after the per-user limit and reports when it frees up")
    void bloqueiaUsuarioAposLimite() {
        for (int i = 0; i < 3; i++) {
            limite.verificarUsuario(parceiroA, "usr_1");
        }

        LimiteRequisicoesExcedidoException excecao = assertThrows(
                LimiteRequisicoesExcedidoException.class,
                () -> limite.verificarUsuario(parceiroA, "usr_1")
        );
        assertEquals(60, excecao.getSegundosParaLiberar());
    }

    @Test
    @DisplayName("counts each user separately, including the same userId under another partner")
    void contaUsuariosSeparadamente() {
        for (int i = 0; i < 3; i++) {
            limite.verificarUsuario(parceiroA, "usr_1");
        }

        assertDoesNotThrow(() -> limite.verificarUsuario(parceiroA, "usr_2"));
        assertDoesNotThrow(() -> limite.verificarUsuario(parceiroB, "usr_1"));
    }

    @Test
    @DisplayName("frees the user again once the window expires")
    void liberaAposJanela() {
        for (int i = 0; i < 3; i++) {
            limite.verificarUsuario(parceiroA, "usr_1");
        }
        relogio.avancar(Duration.ofSeconds(45));
        assertThrows(LimiteRequisicoesExcedidoException.class, () -> limite.verificarUsuario(parceiroA, "usr_1"));

        relogio.avancar(Duration.ofSeconds(15));
        assertDoesNotThrow(() -> limite.verificarUsuario(parceiroA, "usr_1"));
    }

    @Test
    @DisplayName("limits the whole key so a caller cannot dodge the per-user limit by inventing userIds")
    void limitaChaveInteira() {
        for (int i = 0; i < 5; i++) {
            assertTrue(limite.consumirChave(parceiroA).permitido());
        }

        DecisaoLimite bloqueada = limite.consumirChave(parceiroA);
        assertFalse(bloqueada.permitido());
        assertEquals(60, bloqueada.segundosParaLiberar());
        assertTrue(limite.consumirChave(parceiroB).permitido());
    }

    @Test
    @DisplayName("ignores requests without userId, which are rejected later by the user lookup")
    void ignoraUserIdVazio() {
        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> limite.verificarUsuario(parceiroA, " "));
        }
    }
}
