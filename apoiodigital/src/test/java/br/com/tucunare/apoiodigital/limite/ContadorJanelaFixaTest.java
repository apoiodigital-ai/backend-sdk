package br.com.tucunare.apoiodigital.limite;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ContadorJanelaFixaTest {

    @Test
    @DisplayName("rounds the remaining wait up to whole seconds")
    void arredondaEsperaParaCima() {
        RelogioAjustavel relogio = new RelogioAjustavel();
        ContadorJanelaFixa contador = new ContadorJanelaFixa(relogio);

        contador.consumir("chave", 1, Duration.ofSeconds(10));
        relogio.avancar(Duration.ofMillis(9_500));

        DecisaoLimite decisao = contador.consumir("chave", 1, Duration.ofSeconds(10));
        assertFalse(decisao.permitido());
        assertEquals(1, decisao.segundosParaLiberar());
    }

    @Test
    @DisplayName("keeps one window per key")
    void umaJanelaPorChave() {
        ContadorJanelaFixa contador = new ContadorJanelaFixa(new RelogioAjustavel());

        contador.consumir("a", 1, Duration.ofSeconds(10));
        contador.consumir("a", 1, Duration.ofSeconds(10));
        contador.consumir("b", 1, Duration.ofSeconds(10));

        assertEquals(2, contador.janelasAtivas());
    }
}
