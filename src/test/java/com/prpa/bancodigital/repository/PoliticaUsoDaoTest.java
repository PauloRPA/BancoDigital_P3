package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.config.respository.QueryResolver;
import com.prpa.bancodigital.config.respository.SpringYamlQueryResolver;
import com.prpa.bancodigital.model.PoliticaUso;
import com.prpa.bancodigital.repository.dao.PoliticaUsoDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;
import org.yaml.snakeyaml.Yaml;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@ActiveProfiles("test")
public class PoliticaUsoDaoTest {

    @Autowired
    private JdbcClient client;

    @Autowired
    private JdbcTemplate template;

    private QueryResolver resolver;

    @Value("${application.repository.query.path}")
    public String QUERY_PATH;

    private PoliticaUsoDao politicaUsoDao;

    @BeforeEach
    void setUp() {
        resolver = new SpringYamlQueryResolver(new Yaml(), QUERY_PATH);
        politicaUsoDao = new PoliticaUsoDao(client, template, resolver);
    }

    @Test
    public void whenFindAllOnPoliticaUsoDaoShouldReturnTheThreeRequiredValues() {
        assertEquals(3, politicaUsoDao.findAll().size());
    }

    @Test
    public void whenSavePoliticaUsoShouldSucceed() {
        final long expectedId = 4L;
        BigDecimal expectedLimiteDiario = BigDecimal.valueOf(10);
        BigDecimal expectedLimiteCredito = BigDecimal.valueOf(0.5);
        PoliticaUso saved = politicaUsoDao.save(new PoliticaUso(expectedId, expectedLimiteDiario, expectedLimiteCredito));
        assertEquals(expectedId, saved.getId());
        assertEquals(expectedLimiteDiario, saved.getLimiteDiario());
        assertEquals(expectedLimiteCredito, saved.getLimiteCredito());
    }

}
