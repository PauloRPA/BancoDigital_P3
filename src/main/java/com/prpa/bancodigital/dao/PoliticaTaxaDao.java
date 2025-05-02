package com.prpa.bancodigital.dao;

import com.prpa.bancodigital.dao.mapper.PoliticaTaxaMapper;
import com.prpa.bancodigital.model.PoliticaTaxa;
import com.prpa.bancodigital.model.enums.TipoTaxa;
import com.prpa.bancodigital.model.enums.UnidadeTaxa;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Component
public class PoliticaTaxaDao extends AbstractDao<PoliticaTaxa> {

    public static final String POLITICA_TAXA_TABLE_NAME = "politica_taxa";

    public PoliticaTaxaDao(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate, QueryResolver resolver) {
        super(jdbcClient, jdbcTemplate, resolver);
    }

    @Override
    protected String getTableName() {
        return POLITICA_TAXA_TABLE_NAME;
    }

    @Override
    protected RowMapper<PoliticaTaxa> getRowMapper() {
        return new PoliticaTaxaMapper();
    }

    public Optional<PoliticaTaxa> findByNome(String nome) {
        return jdbcClient.sql(resolver.get(getTableName(), "findByNome"))
                .param("nome", nome)
                .query(getRowMapper())
                .optional();
    }

    public boolean existsByNome(String nome) {
        return findByNome(nome).isPresent();
    }

    @Override
    public PoliticaTaxa save(PoliticaTaxa toSave) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(resolver.get(getTableName(), "insert"))
                .param("nome", toSave.getNome())
                .param("quantidade", toSave.getQuantia())
                .param("tipo_taxa", toSave.getTipoTaxa().name())
                .param("unidade_quantia", toSave.getUnidade().name())
                .update(generatedKeyHolder);
        Map<String, Object> fields = generatedKeyHolder.getKeys();
        requireNonNull(fields);
        return PoliticaTaxa.builder()
                .id(parseId(fields, "id"))
                .nome(fields.get("nome").toString())
                .quantia(((BigDecimal) fields.get("quantidade")))
                .tipoTaxa(TipoTaxa.fromName(fields.get("tipo_taxa").toString()))
                .unidade(UnidadeTaxa.fromName(fields.get("unidade_quantia").toString()))
                .build();
    }

}