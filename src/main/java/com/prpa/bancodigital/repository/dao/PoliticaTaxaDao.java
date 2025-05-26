package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.repository.QueryResolver;
import com.prpa.bancodigital.model.PoliticaTaxa;
import com.prpa.bancodigital.repository.dao.mapper.PoliticaTaxaMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PoliticaTaxaDao extends AbstractDao<PoliticaTaxa> {

    public static final String POLITICA_TAXA_TABLE_NAME = "politica_taxa";

    public static final String QUERY_PARAM_NOME = "nome";

    public PoliticaTaxaDao(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate, QueryResolver resolver) {
        super(jdbcClient, jdbcTemplate, resolver);
    }

    @Override
    protected String getTableName() {
        return POLITICA_TAXA_TABLE_NAME;
    }

    @Override
    protected RowMapper<PoliticaTaxa> getRowMapper() {
        return new PoliticaTaxaMapper();
    }

    public Optional<PoliticaTaxa> findByNome(String nome) {
        return sql("findByNome")
                .param(QUERY_PARAM_NOME, nome)
                .query(getRowMapper())
                .optional();
    }

    public boolean existsByNome(String nome) {
        return findByNome(nome).isPresent();
    }

    @Override
    public PoliticaTaxa save(PoliticaTaxa toSave) {
        if (toSave.getId() != null && findById(toSave.getId()).isPresent())
            return update(toSave);
        PoliticaTaxa politicaTaxa = sql("insert")
                .paramSource(new BeanPropertySqlParameterSource(toSave))
                .query(getRowMapper())
                .single();
        politicaTaxa.setTiers(toSave.getTiers());
        return politicaTaxa;
    }

    private PoliticaTaxa update(PoliticaTaxa toUpdate) {
        return jdbcClient.sql(resolver.get(getTableName(), "update"))
                .paramSource(new BeanPropertySqlParameterSource(toUpdate))
                .query(getRowMapper())
                .single();
    }

}