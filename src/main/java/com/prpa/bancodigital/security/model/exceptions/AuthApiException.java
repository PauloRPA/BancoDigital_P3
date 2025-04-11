package com.prpa.bancodigital.security.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AuthApiException extends ResponseStatusException {

    public static final HttpStatus STATUS = HttpStatus.FORBIDDEN;
    public static final String IANA_TYPE_PREFIX = "https://iana.org/assignments/http-problem-types#forbidden";

    public AuthApiException(HttpStatus status, String reason) {
        super(status, reason);
        setType(URI.create(IANA_TYPE_PREFIX));
        setDetail(reason);
        setTitle(status.name().toUpperCase());
    }

    public static AuthApiException from(AuthenticationException exception) {
        return from(STATUS, exception.getMessage());
    }

    public static AuthApiException from(HttpStatus status, String reason) {
        return new AuthApiException(status, reason);
    }

}
