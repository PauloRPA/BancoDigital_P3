package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.repository.QueryResolver;
import com.prpa.bancodigital.model.Endereco;
import com.prpa.bancodigital.repository.dao.mapper.EnderecoMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class EnderecoDao extends AbstractDao<Endereco> {

    public static final String ENDERECO_TABLE_NAME = "endereco";

    public EnderecoDao(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate, QueryResolver resolver) {
        super(jdbcClient, jdbcTemplate, resolver);
    }

    @Override
    protected String getTableName() {
        return ENDERECO_TABLE_NAME;
    }

    @Override
    protected RowMapper<Endereco> getRowMapper() {
        return new EnderecoMapper();
    }

    @Override
    public Endereco save(Endereco toSave) {
        if (toSave.getId() != null && findById(toSave.getId()).isPresent())
            return update(toSave);
        return sql("insert")
                .paramSource(new BeanPropertySqlParameterSource(toSave))
                .query(getRowMapper())
                .single();
    }

    private Endereco update(Endereco toUpdate) {
        return sql("update")
                .paramSource(new BeanPropertySqlParameterSource(toUpdate))
                .query(getRowMapper())
                .single();
    }

    public Endereco saveIfNotExists(Endereco endereco) {
        return sql("selectByEndereco")
                .paramSource(new BeanPropertySqlParameterSource(endereco))
                .query(getRowMapper())
                .optional().orElseGet(() -> save(endereco));
    }

}