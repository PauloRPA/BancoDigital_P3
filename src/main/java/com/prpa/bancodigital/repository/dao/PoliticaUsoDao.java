package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.repository.QueryResolver;
import com.prpa.bancodigital.model.PoliticaUso;
import com.prpa.bancodigital.repository.dao.mapper.PoliticaUsoMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

import static java.util.Objects.requireNonNull;

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
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(resolver.get(getTableName(), "insert"))
                .param("limite_credito", toSave.getLimiteCredito())
                .param("limite_diario_uso", toSave.getLimiteDiario())
                .update(generatedKeyHolder);
        return mapKeyHolderToPoliticaUso(generatedKeyHolder);
    }

    private PoliticaUso update(PoliticaUso toSave) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(resolver.get(getTableName(), "update"))
                .param("id", toSave.getId())
                .param("limite_credito", toSave.getLimiteCredito())
                .param("limite_diario_uso", toSave.getLimiteDiario())
                .update(generatedKeyHolder);
        return mapKeyHolderToPoliticaUso(generatedKeyHolder);
    }

    private static PoliticaUso mapKeyHolderToPoliticaUso(GeneratedKeyHolder generatedKeyHolder) {
        Map<String, Object> fields = generatedKeyHolder.getKeys();
        requireNonNull(fields);
        return PoliticaUso.builder()
                .id(parseId(fields, "id"))
                .limiteCredito(((BigDecimal) fields.get("limite_credito")))
                .limiteDiario(((BigDecimal) fields.get("limite_diario_uso")))
                .build();
    }
}