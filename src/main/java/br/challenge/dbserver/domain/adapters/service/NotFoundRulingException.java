package br.challenge.dbserver.domain.adapters.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundRulingException extends RuntimeException {
    public NotFoundRulingException(String m) {
        super(m);
    }
}
