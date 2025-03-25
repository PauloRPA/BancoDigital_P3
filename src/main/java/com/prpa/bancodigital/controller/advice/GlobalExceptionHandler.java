package com.prpa.bancodigital.controller.advice;

import com.prpa.bancodigital.exception.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> resourceAlreadyExistsExceptionHandler(ApiException ex, WebRequest request) {
        return Objects.requireNonNull(handleExceptionInternal(ex, null, ex.getHeaders(), ex.getStatusCode(), request));
    }

}
