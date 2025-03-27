package com.prpa.bancodigital.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends ApiException {

    public static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;
    public static final String TYPE = ApiException.IANA_TYPE_PREFIX + "validation-error";
    public static final String DETAIL = "Existem campos inválidos";

    public ValidationException(String reason) {
        super(STATUS, reason);
    }

    public ValidationException(String reason, Object errors) {
        super(STATUS, reason, errors);
    }

    public static void throwIfHasErros(BindingResult result) {
        if (!result.hasErrors()) return;
        Map<String, String> fieldsAndValues = new HashMap<>();
        result.getFieldErrors().forEach(fieldError -> {
            fieldsAndValues.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        throw new ValidationException(DETAIL, fieldsAndValues);
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
