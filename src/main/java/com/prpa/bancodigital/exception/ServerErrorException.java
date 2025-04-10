package com.prpa.bancodigital.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ServerErrorException extends ApiException {

    public static final HttpStatus STATUS = HttpStatus.INTERNAL_SERVER_ERROR;
    public static final String TYPE = ApiException.IANA_TYPE_PREFIX + "server-error";

    public ServerErrorException(String reason) {
        super(STATUS, reason);
    }

    public ServerErrorException(String reason, Object errors) {
        super(STATUS, reason, errors);
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
