package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.respository.QueryResolver;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;

@Getter
public abstract class AbstractDao<T> implements Dao<T> {

    public static final String GENERIC_QUERY = "generic";
    protected final JdbcClient jdbcClient;
    protected final JdbcTemplate jdbcTemplate;
    protected final QueryResolver resolver;

    public AbstractDao(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate, QueryResolver resolver) {
        this.jdbcClient = jdbcClient;
        this.jdbcTemplate = jdbcTemplate;
        this.resolver = resolver;
    }

    protected abstract String getTableName();

    protected abstract RowMapper<T> getRowMapper();

    @Override
    public boolean existsById(long id) {
        return findById(id).isPresent();
    }

    @Override
    public Optional<T> findById(long id) {
        String query = resolver.get(GENERIC_QUERY, "findById").formatted(getTableName());
        return jdbcClient.sql(query)
                .param("table", getTableName())
                .param("id", id)
                .query(getRowMapper()).optional();
    }

    @Override
    public List<T> findAll() {
        String query = resolver.get(GENERIC_QUERY, "findAll").formatted(getTableName());
        return jdbcClient.sql(query)
                .param("table", getTableName())
                .query(getRowMapper()).list();
    }

    @Override
    public Page<T> findAll(Pageable page) {
        String query = resolver.get(GENERIC_QUERY, "findAllPageable").formatted(getTableName());
        return new PageImpl<>(jdbcClient.sql(query)
                .param("offset", page.getOffset())
                .param("size", page.getPageSize())
                .query(getRowMapper()).list());
    }

    @Override
    public void deleteById(long id) {
        String query = resolver.get(GENERIC_QUERY, "deleteById").formatted(getTableName());
        jdbcClient.sql(query)
                .param("id", id)
                .update();
    }

    protected static Long parseId(Map<String, Object> fields, String fieldName) {
        Object fieldValue = fields.get(fieldName);
        return isNull(fieldValue) ? null : Long.parseLong(fieldValue.toString());
    }

    protected JdbcClient.StatementSpec sql(String queryName) {
        return jdbcClient.sql(resolver.get(getTableName(), queryName));
    }

}
