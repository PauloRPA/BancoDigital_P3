package com.prpa.bancodigital.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;

import java.time.ZoneId;
import java.util.TimeZone;

@Slf4j
@Configuration
public class ApplicationConfig {

    @Value("${application.timezone.zone_id}")
    public String TIMEZONE;

    public static final String API_V1 = "/api/v1";

    @Bean
    public Validator validator() {
        return new OptionalValidatorFactoryBean();
    }

    @PostConstruct
    public void setTimeZone() {
        log.info("Definindo timezone padrão para: {}", TIMEZONE);
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(TIMEZONE)));
        log.info("Timezone: {} definida", TIMEZONE);
    }

}