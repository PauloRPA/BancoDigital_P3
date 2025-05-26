package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.repository.QueryResolver;
import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.ContaBancaria;
import com.prpa.bancodigital.repository.dao.mapper.ContaMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ContaDao extends AbstractDao<ContaBancaria> {

    public static final String CONTA_TABLE_NAME = "conta";

    public static final String QUERY_PARAM_AGENCIA = "agencia";
    public static final String QUERY_PARAM_NUMERO = "numero";
    public static final String QUERY_PARAM_CLIENTE = "cliente";

    public ContaDao(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate, QueryResolver resolver) {
        super(jdbcClient, jdbcTemplate, resolver);
    }

    @Override
    protected String getTableName() {
        return CONTA_TABLE_NAME;
    }

    @Override
    protected RowMapper<ContaBancaria> getRowMapper() {
        return new ContaMapper();
    }

    @Override
    public ContaBancaria save(ContaBancaria toSave) {
        if (toSave.getId() != null && findById(toSave.getId()).isPresent())
            return update(toSave);
        ContaBancaria contaBancaria = sql("insert")
                .paramSource(new BeanPropertySqlParameterSource(toSave))
                .query(getRowMapper())
                .single();
        contaBancaria.setCliente(toSave.getCliente());
        return contaBancaria;
    }

    private ContaBancaria update(ContaBancaria toUpdate) {
        return sql("update")
                .paramSource(new BeanPropertySqlParameterSource(toUpdate))
                .query(getRowMapper())
                .single();
    }

    public Optional<ContaBancaria> findByAgencia(String agencia) {
        return sql("findByAgencia")
                .param(QUERY_PARAM_AGENCIA, agencia)
                .query(getRowMapper())
                .optional();
    }

    public Optional<ContaBancaria> findByNumero(String numero) {
        return sql("findByNumero")
                .param(QUERY_PARAM_NUMERO, numero)
                .query(getRowMapper())
                .optional();
    }

    public Optional<ContaBancaria> findByAgenciaAndNumero(String agencia, String numero) {
        return sql("findByNumeroAndAgencia")
                .param(QUERY_PARAM_AGENCIA, agencia)
                .param(QUERY_PARAM_NUMERO, numero)
                .query(getRowMapper())
                .optional();
    }

    public List<ContaBancaria> findByCliente(Cliente cliente) {
        return findByClienteId(cliente.getId());
    }

    public List<ContaBancaria> findByClienteId(long id) {
        return sql("findByCliente")
                .param(QUERY_PARAM_CLIENTE, id)
                .query(getRowMapper())
                .list();
    }

}