package br.com.tucunare.apoiodigital.cliente.exception;

public class ClienteNaoAutenticadoException extends RuntimeException {
    public ClienteNaoAutenticadoException() {
        super("Nenhum cliente autenticado para esta requisição");
    }
}
