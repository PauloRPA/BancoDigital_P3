package com.prpa.bancodigital.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.ZoneId;
import java.util.TimeZone;

@Slf4j
@Configuration
public class ApplicationConfig {

    @Value("${application.bank_name}")
    public static String BANK_NAME = "";

    @Value("${application.timezone.zone_id}")
    public String TIMEZONE;

    @Value("${application.external.viacep.timeout_in_millis}")
    public long TIMEOUT_MILLIS;

    public static final String API_V1 = "/api/v1";

    @Bean
    public RestTemplate getRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .connectTimeout(Duration.ofMillis(TIMEOUT_MILLIS))
                .readTimeout(Duration.ofMillis(TIMEOUT_MILLIS))
                .build();
    }

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