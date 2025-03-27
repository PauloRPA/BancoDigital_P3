package com.prpa.bancodigital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;

@Configuration
public class ApplicationConfig {

    public static final String API_V1 = "/api/v1";

    @Bean
    public Validator validator() {
        return new OptionalValidatorFactoryBean();
    }

}
