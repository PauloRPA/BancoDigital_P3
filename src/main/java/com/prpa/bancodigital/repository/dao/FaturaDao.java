package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.repository.QueryResolver;
import com.prpa.bancodigital.model.Cartao;
import com.prpa.bancodigital.model.Fatura;
import com.prpa.bancodigital.repository.dao.mapper.FaturaMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.isNull;

@Component
public class FaturaDao extends AbstractDao<Fatura> {

    public static final String FATURA_TABLE_NAME = "fatura";

    public static final String QUERY_PARAM_CARTAO = "cartao";

    public FaturaDao(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate, QueryResolver resolver) {
        super(jdbcClient, jdbcTemplate, resolver);
    }

    @Override
    protected String getTableName() {
        return FATURA_TABLE_NAME;
    }

    @Override
    protected RowMapper<Fatura> getRowMapper() {
        return new FaturaMapper();
    }

    public List<Fatura> findByCartao(Cartao cartao) {
        if (isNull(cartao.getId()))
            return List.of();
        return sql("findByCartao")
                .param(QUERY_PARAM_CARTAO, cartao.getId())
                .query(getRowMapper())
                .list();
    }

    @Override
    public Fatura save(Fatura toSave) {
        if (toSave.getId() != null && findById(toSave.getId()).isPresent())
            return update(toSave);
        Fatura fatura = sql("insert")
                .paramSource(new BeanPropertySqlParameterSource(toSave))
                .query(getRowMapper())
                .single();
        fatura.setCartao(toSave.getCartao());
        return fatura;
    }

    private Fatura update(Fatura toUpdate) {
        Fatura fatura = sql("update")
                .paramSource(new BeanPropertySqlParameterSource(toUpdate))
                .query(getRowMapper())
                .single();
        fatura.setCartao(toUpdate.getCartao());
        return fatura;
    }

}