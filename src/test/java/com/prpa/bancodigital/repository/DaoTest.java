package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.config.repository.QueryResolver;
import com.prpa.bancodigital.config.repository.SpringYamlQueryResolver;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.yaml.snakeyaml.Yaml;

import static com.prpa.bancodigital.repository.dao.AbstractDao.GENERIC_QUERY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public abstract class DaoTest<T> {

    @MockitoBean
    protected JdbcClient client;

    @MockitoBean
    protected JdbcTemplate template;

    @Mock
    protected JdbcClient.StatementSpec spec;

    @Mock
    protected JdbcClient.ResultQuerySpec resultQuerySpec;

    @Mock
    protected JdbcClient.MappedQuerySpec<T> mappedQuerySpec;

    @Value("${application.repository.query.path}")
    protected String queryPath;

    protected QueryResolver resolver;

    @BeforeEach
    protected void setUp() {
        when(client.sql(any()))
                .thenReturn(spec);
        when(spec.param(any(), any()))
                .thenReturn(spec);
        when(spec.paramSource(any()))
                .thenReturn(spec);
        when(spec.query(ArgumentMatchers.<RowMapper<T>>any()))
                .thenReturn(mappedQuerySpec);
        when(spec.query())
                .thenReturn(resultQuerySpec);
        resolver = new SpringYamlQueryResolver(new Yaml(), queryPath);
    }

    protected String resolveGeneric(String queryName) {
        return resolver.get(GENERIC_QUERY, queryName).formatted(getTableName());
    }

    protected abstract String getTableName();

}