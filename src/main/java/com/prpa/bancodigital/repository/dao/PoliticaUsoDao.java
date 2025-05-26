package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.repository.QueryResolver;
import com.prpa.bancodigital.model.PoliticaUso;
import com.prpa.bancodigital.repository.dao.mapper.PoliticaUsoMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@Component
public class PoliticaUsoDao extends AbstractDao<PoliticaUso> {

    public static final String POLITICA_USO_TABLE_NAME = "politica_uso";

    public static final String TABLE_COLUMN_ID = "id";
    public static final String TABLE_COLUMN_LIMITE_CREDITO = "limite_credito";
    public static final String TABLE_COLUMN_LIMITE_DIARIO_USO = "limite_diario_uso";

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
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        sql("insert")
                .paramSource(new BeanPropertySqlParameterSource(toSave))
                .update(generatedKeyHolder);
        return mapKeyHolderToPoliticaUso(generatedKeyHolder);
    }

    private PoliticaUso update(PoliticaUso toSave) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        sql("update")
                .paramSource(new BeanPropertySqlParameterSource(toSave))
                .update(generatedKeyHolder);
        return mapKeyHolderToPoliticaUso(generatedKeyHolder);
    }

    private static PoliticaUso mapKeyHolderToPoliticaUso(GeneratedKeyHolder generatedKeyHolder) {
        Map<String, Object> fields = generatedKeyHolder.getKeys();
        requireNonNull(fields);
        return PoliticaUso.builder()
                .id(parseId(fields, TABLE_COLUMN_ID))
                .limiteCredito(((BigDecimal) fields.get(TABLE_COLUMN_LIMITE_CREDITO)))
                .limiteDiario(((BigDecimal) fields.get(TABLE_COLUMN_LIMITE_DIARIO_USO)))
                .build();
    }
}