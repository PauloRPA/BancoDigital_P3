package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.repository.QueryResolver;
import com.prpa.bancodigital.model.*;
import com.prpa.bancodigital.model.enums.TipoCartao;
import com.prpa.bancodigital.repository.dao.mapper.CartaoMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.prpa.bancodigital.model.enums.TipoCartao.CARTAO_CREDITO;
import static java.util.Objects.requireNonNull;

@Component
public class CartaoDao extends AbstractDao<Cartao> {

    public static final String CARTAO_TABLE_NAME = "cartao";

    public static final String QUERY_PARAM_CONTA = "conta";

    public static final String TABLE_COLUMN_ID = "id";
    public static final String TABLE_COLUMN_NUMERO = "numero";
    public static final String TABLE_COLUMN_VENCIMENTO = "vencimento";
    public static final String TABLE_COLUMN_CCV = "ccv";
    public static final String TABLE_COLUMN_ATIVO = "ativo";
    public static final String TABLE_COLUMN_SENHA = "senha";
    public static final String TABLE_COLUMN_LIMITE_CREDITO = "limite_credito";
    public static final String TABLE_COLUMN_LIMITE_DIARIO = "limite_diario";
    public static final String TABLE_COLUMN_TIPO = "tipo";
    public static final String TABLE_COLUMN_CONTA = "conta_fk";

    public static final String DATABASE_DATE_TEXT_FORMAT = "yyyy-MM-dd";

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
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        sql("insert")
                .paramSource(new BeanPropertySqlParameterSource(toSave))
                .update(generatedKeyHolder);
        Cartao cartao = mapKeyHolderToTier(generatedKeyHolder);
        cartao.setConta(toSave.getConta());
        if (toSave instanceof CartaoCredito toSaveCredito && cartao instanceof CartaoCredito savedCredito)
            savedCredito.setFaturas(toSaveCredito.getFaturas());
        return cartao;
    }

    private Cartao update(Cartao toUpdate) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        sql("update")
                .paramSource(new BeanPropertySqlParameterSource(toUpdate))
                .update(generatedKeyHolder);
        Cartao saved = mapKeyHolderToTier(generatedKeyHolder);
        saved.setConta(toUpdate.getConta());
        if (toUpdate instanceof CartaoCredito toUpdateCredito && saved instanceof CartaoCredito savedCredito)
            savedCredito.setFaturas(toUpdateCredito.getFaturas());
        return saved;
    }

    private static Cartao mapKeyHolderToTier(GeneratedKeyHolder generatedKeyHolder) {
        Map<String, Object> fields = generatedKeyHolder.getKeys();
        requireNonNull(fields);
        TipoCartao tipoCartao = TipoCartao.fromName(fields.get(TABLE_COLUMN_TIPO).toString())
                .orElseThrow(() -> new IllegalStateException("Tipo de cartao lido do banco de dados inválida"));

        Cartao cartao = tipoCartao.equals(CARTAO_CREDITO) ? new CartaoCredito() : new CartaoDebito();

        ContaBancaria conta = new ContaPoupanca();
        conta.setId(parseId(fields, TABLE_COLUMN_CONTA));

        cartao.setId(parseId(fields, TABLE_COLUMN_ID));
        cartao.setNumero(fields.get(TABLE_COLUMN_NUMERO).toString());
        cartao.setVencimento(parseLocalDate(fields, TABLE_COLUMN_VENCIMENTO, DATABASE_DATE_TEXT_FORMAT));
        cartao.setCcv(fields.get(TABLE_COLUMN_CCV).toString());
        cartao.setAtivo((Boolean) fields.get(TABLE_COLUMN_ATIVO));
        cartao.setSenha(fields.get(TABLE_COLUMN_SENHA).toString());
        cartao.setLimiteCredito(((BigDecimal) fields.get(TABLE_COLUMN_LIMITE_CREDITO)));
        cartao.setLimiteDiario(((BigDecimal) fields.get(TABLE_COLUMN_LIMITE_DIARIO)));
        cartao.setTipo(tipoCartao);
        return cartao;
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