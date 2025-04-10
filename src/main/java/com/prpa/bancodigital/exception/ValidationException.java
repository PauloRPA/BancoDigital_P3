package com.prpa.bancodigital.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends ApiException {

    public static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;
    public static final String TYPE = ApiException.IANA_TYPE_PREFIX + "validation-error";
    public static final String DETAIL = "Existem campos inválidos";
    public static final String GLOBAL_ERROR_FIELD_NAME = "global";

    public ValidationException(String reason) {
        super(STATUS, reason);
    }

    public ValidationException(String reason, Object errors) {
        super(STATUS, reason, errors);
    }

    public static void throwIfHasErros(BindingResult result) {
        if (!result.hasErrors()) return;
        Map<String, Object> fieldsAndValues = new HashMap<>();
        result.getFieldErrors().forEach(fieldError -> {
            fieldsAndValues.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        if (result.hasGlobalErrors()) {
            List<String> globalErrorMessages = result.getGlobalErrors().stream()
                    .map(ObjectError::getDefaultMessage).toList();
            fieldsAndValues.put(GLOBAL_ERROR_FIELD_NAME, globalErrorMessages);
        }

        throw new ValidationException(DETAIL, fieldsAndValues);
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
