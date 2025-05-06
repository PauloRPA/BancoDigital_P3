package com.prpa.bancodigital.config.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;

@Configuration
public class RepositoryConfig {

    @Value("${application.repository.query.path}")
    public String QUERY_PATH;

    @Bean
    public Yaml getYamlParser() {
        return new Yaml();
    }

    @Bean
    public SpringYamlQueryResolver getSpringYamlQueryResolver(@Autowired Yaml parser) {
        return new SpringYamlQueryResolver(parser, QUERY_PATH);
    }

}
