package br.com.tucunare.apoiodigital.limite;

public record DecisaoLimite(boolean permitido, long segundosParaLiberar) {

    public static DecisaoLimite permitida() {
        return new DecisaoLimite(true, 0);
    }

    public static DecisaoLimite bloqueada(long segundosParaLiberar) {
        return new DecisaoLimite(false, segundosParaLiberar);
    }
}
