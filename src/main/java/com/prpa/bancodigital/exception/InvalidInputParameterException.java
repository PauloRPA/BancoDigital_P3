package com.prpa.bancodigital.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidInputParameterException extends ApiException {

    public static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;
    public static final String TYPE = ApiException.IANA_TYPE_PREFIX + "invalid-input";

    public InvalidInputParameterException(String reason) {
        super(STATUS, reason);
    }

    public InvalidInputParameterException(String reason, Object errors) {
        super(STATUS, reason, errors);
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
