package br.challenge.dbserver.domain.adapters.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationRulingException extends RuntimeException {
    public ValidationRulingException(String m) {
        super(m);
    }
}

