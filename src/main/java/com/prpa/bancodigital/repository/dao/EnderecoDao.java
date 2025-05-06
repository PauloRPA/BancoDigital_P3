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

    public static final String ENDERECO_FIELD_COMPLEMENTO = "complemento";
    public static final String ENDERECO_FIELD_NUMERO = "numero";
    public static final String ENDERECO_FIELD_RUA = "rua";
    public static final String ENDERECO_FIELD_BAIRRO = "bairro";
    public static final String ENDERECO_FIELD_CIDADE = "cidade";
    public static final String ENDERECO_FIELD_ESTADO = "estado";
    public static final String ENDERECO_FIELD_CEP = "cep";

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
                .param(ENDERECO_FIELD_CEP, toSave.getCep())
                .param(ENDERECO_FIELD_COMPLEMENTO, toSave.getComplemento())
                .param(ENDERECO_FIELD_NUMERO, toSave.getNumero())
                .param(ENDERECO_FIELD_RUA, toSave.getRua())
                .param(ENDERECO_FIELD_BAIRRO, toSave.getBairro())
                .param(ENDERECO_FIELD_CIDADE, toSave.getCidade())
                .param(ENDERECO_FIELD_ESTADO, toSave.getEstado())
                .update(generatedKeyHolder);
        return mapKeyHolderToEndereco(generatedKeyHolder);
    }

    private Endereco update(Endereco toUpdate) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(resolver.get(getTableName(), "update"))
                .param("id", toUpdate.getId())
                .param(ENDERECO_FIELD_CEP, toUpdate.getCep())
                .param(ENDERECO_FIELD_COMPLEMENTO, toUpdate.getComplemento())
                .param(ENDERECO_FIELD_NUMERO, toUpdate.getNumero())
                .param(ENDERECO_FIELD_RUA, toUpdate.getRua())
                .param(ENDERECO_FIELD_BAIRRO, toUpdate.getBairro())
                .param(ENDERECO_FIELD_CIDADE, toUpdate.getCidade())
                .param(ENDERECO_FIELD_ESTADO, toUpdate.getEstado())
                .update(generatedKeyHolder);
        return mapKeyHolderToEndereco(generatedKeyHolder);
    }

    private static Endereco mapKeyHolderToEndereco(GeneratedKeyHolder generatedKeyHolder) {
        Map<String, Object> fields = generatedKeyHolder.getKeys();
        requireNonNull(fields);
        return Endereco.builder()
                .id(parseId(fields, "id"))
                .cep(fields.get(ENDERECO_FIELD_CEP).toString())
                .complemento(fields.get(ENDERECO_FIELD_COMPLEMENTO).toString())
                .numero((Integer) fields.get(ENDERECO_FIELD_NUMERO))
                .rua(fields.get(ENDERECO_FIELD_RUA).toString())
                .bairro(fields.get(ENDERECO_FIELD_BAIRRO).toString())
                .cidade(fields.get(ENDERECO_FIELD_CIDADE).toString())
                .estado(fields.get(ENDERECO_FIELD_ESTADO).toString())
                .build();
    }

    public Endereco saveIfNotExists(Endereco endereco) {
        return jdbcClient.sql(resolver.get(getTableName(), "selectByEndereco"))
                .param(ENDERECO_FIELD_CEP, endereco.getCep())
                .param(ENDERECO_FIELD_COMPLEMENTO, endereco.getComplemento())
                .param(ENDERECO_FIELD_NUMERO, endereco.getNumero())
                .param(ENDERECO_FIELD_RUA, endereco.getRua())
                .param(ENDERECO_FIELD_BAIRRO, endereco.getBairro())
                .param(ENDERECO_FIELD_CIDADE, endereco.getCidade())
                .param(ENDERECO_FIELD_ESTADO, endereco.getEstado())
                .query(getRowMapper())
                .optional().orElseGet(() -> save(endereco));
    }

}