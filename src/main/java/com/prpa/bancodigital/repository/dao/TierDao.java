package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.repository.QueryResolver;
import com.prpa.bancodigital.model.PoliticaUso;
import com.prpa.bancodigital.model.Tier;
import com.prpa.bancodigital.repository.dao.mapper.TierMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Component
public class TierDao extends AbstractDao<Tier> {

    public static final String TIER_TABLE_NAME = "tier";

    public static final String QUERY_PARAM_ID = "id";
    public static final String QUERY_PARAM_NOME = "nome";
    public static final String QUERY_PARAM_POLITICA_USO = "politica_uso_fk";

    public static final String TABLE_COLUMN_POLITICA_USO = "politica_uso_fk";
    public static final String TABLE_COLUMN_ID = "id";
    public static final String TABLE_COLUMN_NOME = "nome";

    public TierDao(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate, QueryResolver resolver) {
        super(jdbcClient, jdbcTemplate, resolver);
    }

    public boolean existsByNomeIgnoreCase(String nome) {
        return findByNomeIgnoreCase(nome).isPresent();
    }

    public Optional<Tier> findByNomeIgnoreCase(String nome) {
        return sql("findByNome")
                .param(QUERY_PARAM_NOME, nome)
                .query(getRowMapper())
                .optional();
    }

    @Override
    protected String getTableName() {
        return TIER_TABLE_NAME;
    }

    @Override
    protected RowMapper<Tier> getRowMapper() {
        return new TierMapper();
    }

    public Tier save(Tier toSave) {
        if (toSave.getId() != null && findById(toSave.getId()).isPresent())
            return update(toSave);
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        sql("insert")
                .param(QUERY_PARAM_NOME, toSave.getNome())
                .param(QUERY_PARAM_POLITICA_USO, toSave.getPoliticaUso().map(PoliticaUso::getId).orElse(null))
                .update(generatedKeyHolder);
        return mapKeyHolderToTier(generatedKeyHolder);
    }

    private Tier update(Tier toUpdate) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        Optional<Long> politicaUsoId = toUpdate.getPoliticaUso()
                .map(PoliticaUso::getId)
                .filter(id -> id != 0);
        sql("update")
                .param(QUERY_PARAM_ID, toUpdate.getId())
                .param(QUERY_PARAM_NOME, toUpdate.getNome())
                .param(QUERY_PARAM_POLITICA_USO, politicaUsoId.orElse(null))
                .update(generatedKeyHolder);
        return mapKeyHolderToTier(generatedKeyHolder);
    }

    public List<Tier> findByPoliticaUsoId(Long politicaUsoId) {
        return sql("findByPoliticaUsoId")
                .param(QUERY_PARAM_ID, politicaUsoId)
                .query(getRowMapper())
                .list();
    }

    private static Tier mapKeyHolderToTier(GeneratedKeyHolder generatedKeyHolder) {
        Map<String, Object> fields = generatedKeyHolder.getKeys();
        requireNonNull(fields);
        PoliticaUso politicaUso = new PoliticaUso();
        politicaUso.setId(parseId(fields, TABLE_COLUMN_POLITICA_USO));
        return Tier.builder()
                .id(parseId(fields, TABLE_COLUMN_ID))
                .nome(fields.get(TABLE_COLUMN_NOME).toString())
                .politicaUso(politicaUso)
                .build();
    }

}