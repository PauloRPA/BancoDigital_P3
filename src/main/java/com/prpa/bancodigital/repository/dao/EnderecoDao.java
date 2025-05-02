package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.respository.QueryResolver;
import com.prpa.bancodigital.model.Endereco;
import com.prpa.bancodigital.repository.dao.mapper.EnderecoMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.util.Objects.requireNonNull;

@Component
public class EnderecoDao extends AbstractDao<Endereco> {

    public static final String ENDERECO_TABLE_NAME = "endereco";

    public EnderecoDao(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate, QueryResolver resolver) {
        super(jdbcClient, jdbcTemplate, resolver);
    }

    @Override
    protected String getTableName() {
        return ENDERECO_TABLE_NAME;
    }

    @Override
    protected RowMapper<Endereco> getRowMapper() {
        return new EnderecoMapper();
    }

    @Override
    public Endereco save(Endereco toSave) {
        if (toSave.getId() != null && findById(toSave.getId()).isPresent())
            return update(toSave);
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(resolver.get(getTableName(), "insert"))
                .param("cep", toSave.getCep())
                .param("complemento", toSave.getComplemento())
                .param("numero", toSave.getNumero())
                .param("rua", toSave.getRua())
                .param("bairro", toSave.getBairro())
                .param("cidade", toSave.getCidade())
                .param("estado", toSave.getEstado())
                .update(generatedKeyHolder);
        return mapKeyHolderToEndereco(generatedKeyHolder);
    }

    private Endereco update(Endereco toUpdate) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(resolver.get(getTableName(), "update"))
                .param("id", toUpdate.getId())
                .param("cep", toUpdate.getCep())
                .param("complemento", toUpdate.getComplemento())
                .param("numero", toUpdate.getNumero())
                .param("rua", toUpdate.getRua())
                .param("bairro", toUpdate.getBairro())
                .param("cidade", toUpdate.getCidade())
                .param("estado", toUpdate.getEstado())
                .update(generatedKeyHolder);
        return mapKeyHolderToEndereco(generatedKeyHolder);
    }

    private static Endereco mapKeyHolderToEndereco(GeneratedKeyHolder generatedKeyHolder) {
        Map<String, Object> fields = generatedKeyHolder.getKeys();
        requireNonNull(fields);
        return Endereco.builder()
                .cep(fields.get("cep").toString())
                .complemento(fields.get("complemento").toString())
                .numero((Integer) fields.get("numero"))
                .rua(fields.get("rua").toString())
                .bairro(fields.get("bairro").toString())
                .cidade(fields.get("cidade").toString())
                .estado(fields.get("estado").toString())
                .build();
    }
}