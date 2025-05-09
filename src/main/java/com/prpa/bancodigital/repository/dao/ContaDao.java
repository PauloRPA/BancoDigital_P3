package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.repository.QueryResolver;
import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.ContaBancaria;
import com.prpa.bancodigital.model.ContaCorrente;
import com.prpa.bancodigital.model.ContaPoupanca;
import com.prpa.bancodigital.model.enums.TipoConta;
import com.prpa.bancodigital.repository.dao.mapper.ContaMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.prpa.bancodigital.model.enums.TipoConta.CONTA_CORRENTE;
import static java.util.Objects.requireNonNull;

@Component
public class ContaDao extends AbstractDao<ContaBancaria> {

    public static final String CONTA_TABLE_NAME = "conta";

    public static final String QUERY_PARAM_AGENCIA = "agencia";
    public static final String QUERY_PARAM_NUMERO = "numero";
    public static final String QUERY_PARAM_CLIENTE = "cliente";

    public static final String TABLE_COLUMN_ID = "id";
    public static final String TABLE_COLUMN_AGENCIA = "agencia";
    public static final String TABLE_COLUMN_NUMERO = "numero";
    public static final String TABLE_COLUMN_SALDO = "saldo";
    public static final String TABLE_COLUMN_TIPO = "tipo";
    private static final String TABLE_COLUMN_CLIENTE_FK = "cliente_fk";

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
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        sql("insert")
                .paramSource(new BeanPropertySqlParameterSource(toSave))
                .update(generatedKeyHolder);
        ContaBancaria contaBancaria = mapKeyHolderToTier(generatedKeyHolder);
        contaBancaria.setCliente(toSave.getCliente());
        return contaBancaria;
    }

    private ContaBancaria update(ContaBancaria toUpdate) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        sql("update")
                .paramSource(new BeanPropertySqlParameterSource(toUpdate))
                .update(generatedKeyHolder);
        return mapKeyHolderToTier(generatedKeyHolder);
    }

    private static ContaBancaria mapKeyHolderToTier(GeneratedKeyHolder generatedKeyHolder) {
        Map<String, Object> fields = generatedKeyHolder.getKeys();
        requireNonNull(fields);
        Cliente cliente = new Cliente();
        cliente.setId(parseId(fields, TABLE_COLUMN_CLIENTE_FK));
        TipoConta tipoConta = TipoConta.fromName(fields.get(TABLE_COLUMN_TIPO).toString())
                .orElseThrow(() -> new IllegalStateException("Tipo de conta lida do banco de dados inválida"));
        ContaBancaria conta = tipoConta.equals(CONTA_CORRENTE) ? new ContaCorrente() : new ContaPoupanca();
        conta.setId(parseId(fields, TABLE_COLUMN_ID));
        conta.setNumero(fields.get(TABLE_COLUMN_NUMERO).toString());
        conta.setAgencia(fields.get(TABLE_COLUMN_AGENCIA).toString());
        conta.setSaldo(((BigDecimal) fields.get(TABLE_COLUMN_SALDO)));
        conta.setCliente(cliente);
        conta.setTipo(tipoConta);
        conta.setPoliticas(new ArrayList<>());
        conta.setCartoes(new ArrayList<>());
        return conta;
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