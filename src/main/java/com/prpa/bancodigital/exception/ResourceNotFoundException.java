package com.prpa.bancodigital.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends ApiException {

    public static final HttpStatus STATUS = HttpStatus.NOT_FOUND;
    public static final String TYPE = ApiException.IANA_TYPE_PREFIX + "not-found";

    public ResourceNotFoundException(String reason) {
        super(STATUS, reason);
    }

    public ResourceNotFoundException(String reason, Object errors) {
        super(STATUS, reason, errors);
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
