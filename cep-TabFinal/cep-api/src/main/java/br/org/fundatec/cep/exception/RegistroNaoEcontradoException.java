package br.org.fundatec.cep.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class RegistroNaoEcontradoException extends RuntimeException {

    public RegistroNaoEcontradoException(String mensagem) {
        super(mensagem);
    }
}
