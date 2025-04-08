package com.prpa.bancodigital.controller.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prpa.bancodigital.model.validator.annotations.SingleField;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;

@RestControllerAdvice
public class SingleFieldControllerAdvice implements RequestBodyAdvice {

    private ApplicationContext applicationContext;
    private ObjectMapper objectMapper;
    private String fieldName;

    public SingleFieldControllerAdvice(ApplicationContext applicationContext, ObjectMapper objectMapper) {
        this.applicationContext = applicationContext;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        this.fieldName = Arrays.stream(methodParameter.getParameterAnnotations())
                .filter(ann -> ann.annotationType().isAssignableFrom(SingleField.class))
                .findFirst()
                .map(SingleField.class::cast)
                .map(SingleField::name)
                .orElse("");

        return !fieldName.isBlank();
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        if (applicationContext.getBean(converterType).canRead(HashMap.class, MediaType.APPLICATION_JSON)) {
            HashMap<?,?> hashMap = objectMapper.readValue(inputMessage.getBody(), HashMap.class);
            if (hashMap.containsKey(fieldName)) {
                return new HttpInputMessage() {
                    @Override
                    public InputStream getBody() throws IOException {
                        return new ByteArrayInputStream(hashMap.get(fieldName).toString().getBytes());
                    }

                    @Override
                    public HttpHeaders getHeaders() {
                        return inputMessage.getHeaders();
                    }
                };
            }
        }
        return inputMessage;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }
}
