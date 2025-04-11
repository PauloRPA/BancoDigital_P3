package com.prpa.bancodigital.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Map;

@Slf4j
public abstract class ApiException extends ResponseStatusException {

    public static final String IANA_TYPE_PREFIX = "https://iana.org/assignments/http-problem-types#";
    public static final String MESSAGE_FIELD_NAME = "messages";
    protected final Object messages;

    public ApiException(HttpStatusCode status, String reason) {
        this(status, reason, null);
    }

    public ApiException(HttpStatusCode status, String reason, Object messages) {
        this(status, reason, MESSAGE_FIELD_NAME, messages);
    }

    public ApiException(HttpStatusCode status, String reason, String messageFieldName, Object messages) {
        super(status, reason);
        setDetail(reason);
        setType(URI.create(getType()));
        this.messages = messages;
        if (messages != null)
            getBody().setProperties(Map.of(messageFieldName, messages));
    }

    public abstract String getType();

}
