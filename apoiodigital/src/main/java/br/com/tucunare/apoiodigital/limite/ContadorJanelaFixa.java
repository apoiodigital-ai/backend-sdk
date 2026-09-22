package br.com.tucunare.apoiodigital.limite;

import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

public class ContadorJanelaFixa {

    private static final int ENTRADAS_ANTES_DA_LIMPEZA = 50_000;

    private record Janela(long inicioMillis, long duracaoMillis, int contagem) {

        boolean expirouEm(long agoraMillis) {
            return agoraMillis - inicioMillis >= duracaoMillis;
        }
    }

    private final Clock clock;
    private final ConcurrentHashMap<String, Janela> janelas = new ConcurrentHashMap<>();

    public ContadorJanelaFixa(Clock clock) {
        this.clock = clock;
    }

    public DecisaoLimite consumir(String chave, int maximo, Duration duracao) {
        long agora = clock.millis();
        long duracaoMillis = duracao.toMillis();

        Janela janela = janelas.compute(chave, (ignorada, atual) ->
                atual == null || atual.expirouEm(agora)
                        ? new Janela(agora, duracaoMillis, 1)
                        : new Janela(atual.inicioMillis(), duracaoMillis, atual.contagem() + 1)
        );

        limparJanelasExpiradasSeNecessario(agora);

        if (janela.contagem() <= maximo) {
            return DecisaoLimite.permitida();
        }
        long millisParaLiberar = janela.inicioMillis() + duracaoMillis - agora;
        return DecisaoLimite.bloqueada(Math.max(1, (millisParaLiberar + 999) / 1000));
    }

    int janelasAtivas() {
        return janelas.size();
    }

    private void limparJanelasExpiradasSeNecessario(long agora) {
        if (janelas.size() > ENTRADAS_ANTES_DA_LIMPEZA) {
            janelas.values().removeIf(janela -> janela.expirouEm(agora));
        }
    }
}
