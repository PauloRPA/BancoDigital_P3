package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.repository.QueryResolver;
import com.prpa.bancodigital.model.Cartao;
import com.prpa.bancodigital.model.CartaoCredito;
import com.prpa.bancodigital.model.Fatura;
import com.prpa.bancodigital.repository.dao.mapper.FaturaMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

@Component
public class FaturaDao extends AbstractDao<Fatura> {

    public static final String FATURA_TABLE_NAME = "fatura";

    public static final String TABLE_COLUMN_ID = "id";
    public static final String TABLE_COLUMN_ABERTURA = "abertura";
    public static final String TABLE_COLUMN_FECHAMENTO = "fechamento";
    public static final String TABLE_COLUMN_VALOR = "valor";
    public static final String TABLE_COLUMN_TAXA_UTILIZACAO = "taxa_utilizacao_cobrada";
    public static final String TABLE_COLUMN_PAGA = "pago";
    public static final String TABLE_COLUMN_CARTAO = "cartao_fk";

    public static final String QUERY_PARAM_CARTAO = "cartao";
    public static final String DATABASE_DATE_FORMAT = "yyyy-MM-dd";

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
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        sql("insert")
                .paramSource(new BeanPropertySqlParameterSource(toSave))
                .update(generatedKeyHolder);
        Fatura fatura = mapKeyHolderToTier(generatedKeyHolder);
        fatura.setCartao(toSave.getCartao());
        return fatura;
    }

    private Fatura update(Fatura toUpdate) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        sql("update")
                .paramSource(new BeanPropertySqlParameterSource(toUpdate))
                .update(generatedKeyHolder);
        Fatura fatura = mapKeyHolderToTier(generatedKeyHolder);
        fatura.setCartao(toUpdate.getCartao());
        return fatura;
    }

    private static Fatura mapKeyHolderToTier(GeneratedKeyHolder generatedKeyHolder) {
        Map<String, Object> fields = generatedKeyHolder.getKeys();
        requireNonNull(fields);
        CartaoCredito cartao = new CartaoCredito();
        cartao.setId(parseId(fields, TABLE_COLUMN_CARTAO));
        return Fatura.builder()
                .id(parseId(fields, TABLE_COLUMN_ID))
                .abertura(parseLocalDate(fields, TABLE_COLUMN_ABERTURA, DATABASE_DATE_FORMAT))
                .fechamento(parseLocalDate(fields, TABLE_COLUMN_FECHAMENTO, DATABASE_DATE_FORMAT))
                .valor(((BigDecimal) fields.get(TABLE_COLUMN_VALOR)))
                .taxaUtilizacao(((Boolean) fields.get(TABLE_COLUMN_TAXA_UTILIZACAO)))
                .pago((Boolean) fields.get(TABLE_COLUMN_PAGA))
                .cartao(cartao)
                .build();
    }

}