package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.repository.QueryResolver;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;
import java.util.Optional;

@Getter
public abstract class AbstractDao<T> implements Dao<T> {

    public static final String GENERIC_QUERY = "generic";

    public static final String QUERY_PARAM_ID = "id";
    public static final String QUERY_PARAM_OFFSET = "offset";
    public static final String QUERY_PARAM_SIZE = "size";

    protected final JdbcClient jdbcClient;
    protected final JdbcTemplate jdbcTemplate;
    protected final QueryResolver resolver;

    protected AbstractDao(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate, QueryResolver resolver) {
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
                .param(QUERY_PARAM_ID, id)
                .query(getRowMapper()).optional();
    }

    @Override
    public List<T> findAll() {
        String query = resolver.get(GENERIC_QUERY, "findAll").formatted(getTableName());
        return jdbcClient.sql(query)
                .query(getRowMapper()).list();
    }

    @Override
    public Page<T> findAll(Pageable page) {
        String query = resolver.get(GENERIC_QUERY, "findAllPageable").formatted(getTableName());
        return new PageImpl<>(jdbcClient.sql(query)
                .param(QUERY_PARAM_OFFSET, page.getOffset())
                .param(QUERY_PARAM_SIZE, page.getPageSize())
                .query(getRowMapper())
                .list());
    }

    @Override
    public void deleteById(long id) {
        String query = resolver.get(GENERIC_QUERY, "deleteById").formatted(getTableName());
        jdbcClient.sql(query)
                .param(QUERY_PARAM_ID, id)
                .query()
                .rowSet();
    }

    protected JdbcClient.StatementSpec sql(String queryName) {
        return jdbcClient.sql(resolver.get(getTableName(), queryName));
    }

}