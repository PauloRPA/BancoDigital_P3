package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.repository.QueryResolver;
import com.prpa.bancodigital.model.Cartao;
import com.prpa.bancodigital.model.CartaoCredito;
import com.prpa.bancodigital.model.ContaBancaria;
import com.prpa.bancodigital.repository.dao.mapper.CartaoMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CartaoDao extends AbstractDao<Cartao> {

    public static final String CARTAO_TABLE_NAME = "cartao";
    public static final String QUERY_PARAM_CONTA = "conta";
    public static final String TABLE_COLUMN_NUMERO = "numero";

    public CartaoDao(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate, QueryResolver resolver) {
        super(jdbcClient, jdbcTemplate, resolver);
    }

    @Override
    protected String getTableName() {
        return CARTAO_TABLE_NAME;
    }

    @Override
    protected RowMapper<Cartao> getRowMapper() {
        return new CartaoMapper();
    }

    @Override
    public Cartao save(Cartao toSave) {
        if (toSave.getId() != null && findById(toSave.getId()).isPresent())
            return update(toSave);
        Cartao cartao = sql("insert")
                .paramSource(new BeanPropertySqlParameterSource(toSave))
                .query(getRowMapper())
                .single();
        cartao.setConta(toSave.getConta());
        if (toSave instanceof CartaoCredito toSaveCredito && cartao instanceof CartaoCredito savedCredito)
            savedCredito.setFaturas(toSaveCredito.getFaturas());
        return cartao;
    }

    private Cartao update(Cartao toUpdate) {
        Cartao saved = sql("update")
                .paramSource(new BeanPropertySqlParameterSource(toUpdate))
                .query(getRowMapper())
                .single();
        saved.setConta(toUpdate.getConta());
        if (toUpdate instanceof CartaoCredito toUpdateCredito && saved instanceof CartaoCredito savedCredito)
            savedCredito.setFaturas(toUpdateCredito.getFaturas());
        return saved;
    }

    public Optional<Cartao> findByNumero(String numero) {
        return sql("findByNumero")
                .param(TABLE_COLUMN_NUMERO, numero)
                .query(getRowMapper())
                .optional();
    }

    public List<Cartao> findByConta(ContaBancaria conta) {
        return sql("findByConta")
                .param(QUERY_PARAM_CONTA, conta.getId())
                .query(getRowMapper())
                .list();
    }

}