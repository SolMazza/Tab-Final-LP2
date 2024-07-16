package br.org.fundatec.cep.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ParametroInvalidoException extends  RuntimeException {

    public ParametroInvalidoException(String mensagem) {
        super(mensagem);
    }
}
