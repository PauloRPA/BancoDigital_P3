package com.prpa.bancodigital.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedOperationException extends ApiException {

    public static final HttpStatus STATUS = HttpStatus.FORBIDDEN;
    public static final String TYPE = ApiException.IANA_TYPE_PREFIX + "unauthorized-operation";

    public UnauthorizedOperationException(String reason) {
        super(STATUS, reason);
    }

    public UnauthorizedOperationException(String reason, Object errors) {
        super(STATUS, reason, errors);
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
