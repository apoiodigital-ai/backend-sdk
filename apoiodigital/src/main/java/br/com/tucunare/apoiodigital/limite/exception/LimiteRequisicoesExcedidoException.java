package br.com.tucunare.apoiodigital.limite.exception;

public class LimiteRequisicoesExcedidoException extends RuntimeException {

    private final long segundosParaLiberar;

    public LimiteRequisicoesExcedidoException(long segundosParaLiberar) {
        super("Limite de requisições excedido. Tente novamente em " + segundosParaLiberar + " segundos.");
        this.segundosParaLiberar = segundosParaLiberar;
    }

    public long getSegundosParaLiberar() {
        return segundosParaLiberar;
    }
}
