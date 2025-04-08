package com.prpa.bancodigital.exception;

public class GettersNotFoundForMatchingFields extends RuntimeException {
    public GettersNotFoundForMatchingFields(String msg) {
        super(msg);
    }
}
