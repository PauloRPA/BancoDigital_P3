package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.respository.QueryResolver;
import com.prpa.bancodigital.model.PoliticaUso;
import com.prpa.bancodigital.model.Tier;
import com.prpa.bancodigital.repository.dao.mapper.TierMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Component
public class TierDao extends AbstractDao<Tier>{

    public static final String TIER_TABLE_NAME = "tier";

    public TierDao(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate, QueryResolver resolver) {
        super(jdbcClient, jdbcTemplate, resolver);
    }

    public boolean existsByNomeIgnoreCase(String nome) {
        return findByNomeIgnoreCase(nome).isPresent();
    }

    public Optional<Tier> findByNomeIgnoreCase(String nome) {
        return jdbcClient.sql(resolver.get(getTableName(), "findByNome"))
                .param("nome", nome)
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
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(resolver.get(getTableName(), "insert"))
                .param("nome", toSave.getNome())
                .param("politica_uso_fk", toSave.getPoliticaUso().map(PoliticaUso::getId).orElse(null))
                .update(generatedKeyHolder);
        Map<String, Object> fields = generatedKeyHolder.getKeys();
        requireNonNull(fields);
        PoliticaUso politicaUso = new PoliticaUso();
        politicaUso.setId(parseId(fields, "politica_uso_fk"));
        return Tier.builder()
                .id(parseId(fields, "id"))
                .nome(fields.get("nome").toString())
                .politicaUso(politicaUso)
                .build();
    }
}
