package br.edu.fernandotelheiro.continuumcontrol.domain.exception;

public class TransicaoInvalidaException extends RuntimeException {

    public TransicaoInvalidaException(String message) {
        super(message);
    }
}