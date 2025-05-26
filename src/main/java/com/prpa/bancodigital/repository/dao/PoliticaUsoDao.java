package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.repository.QueryResolver;
import com.prpa.bancodigital.model.PoliticaUso;
import com.prpa.bancodigital.repository.dao.mapper.PoliticaUsoMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class PoliticaUsoDao extends AbstractDao<PoliticaUso> {

    public static final String POLITICA_USO_TABLE_NAME = "politica_uso";

    public PoliticaUsoDao(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate, QueryResolver resolver) {
        super(jdbcClient, jdbcTemplate, resolver);
    }

    @Override
    protected String getTableName() {
        return POLITICA_USO_TABLE_NAME;
    }

    @Override
    protected RowMapper<PoliticaUso> getRowMapper() {
        return new PoliticaUsoMapper();
    }

    @Override
    public PoliticaUso save(PoliticaUso toSave) {
        if (toSave.getId() != null && findById(toSave.getId()).isPresent())
            return update(toSave);
        return sql("insert")
                .paramSource(new BeanPropertySqlParameterSource(toSave))
                .query(getRowMapper())
                .single();
    }

    private PoliticaUso update(PoliticaUso toSave) {
        return sql("update")
                .paramSource(new BeanPropertySqlParameterSource(toSave))
                .query(getRowMapper())
                .single();
    }

}