package com.prpa.bancodigital.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExistsException extends ApiException {

    public static final HttpStatus STATUS = HttpStatus.CONFLICT;
    public static final String TYPE = ApiException.IANA_TYPE_PREFIX + "resource-exists";

    public ResourceAlreadyExistsException(String reason) {
        super(STATUS, reason);
    }

    public ResourceAlreadyExistsException(String reason, Object errors) {
        super(STATUS, reason, errors);
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
