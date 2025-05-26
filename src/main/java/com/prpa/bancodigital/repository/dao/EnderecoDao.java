package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.repository.QueryResolver;
import com.prpa.bancodigital.model.Endereco;
import com.prpa.bancodigital.repository.dao.mapper.EnderecoMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.util.Objects.requireNonNull;

@Component
public class EnderecoDao extends AbstractDao<Endereco> {

    public static final String ENDERECO_TABLE_NAME = "endereco";

    public static final String TABLE_COLUMN_ID = "id";
    public static final String TABLE_COLUMN_CEP = "cep";
    public static final String TABLE_COLUMN_COMPLEMENTO = "complemento";
    public static final String TABLE_COLUMN_NUMERO = "numero";
    public static final String TABLE_COLUMN_RUA = "rua";
    public static final String TABLE_COLUMN_BAIRRO = "bairro";
    public static final String TABLE_COLUMN_CIDADE = "cidade";
    public static final String TABLE_COLUMN_ESTADO = "estado";

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
        sql("insert")
                .paramSource(new BeanPropertySqlParameterSource(toSave))
                .update(generatedKeyHolder);
        return mapKeyHolderToEndereco(generatedKeyHolder);
    }

    private Endereco update(Endereco toUpdate) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        sql("update")
                .paramSource(new BeanPropertySqlParameterSource(toUpdate))
                .update(generatedKeyHolder);
        return mapKeyHolderToEndereco(generatedKeyHolder);
    }

    public Endereco saveIfNotExists(Endereco endereco) {
        return sql("selectByEndereco")
                .paramSource(new BeanPropertySqlParameterSource(endereco))
                .query(getRowMapper())
                .optional().orElseGet(() -> save(endereco));
    }

    private static Endereco mapKeyHolderToEndereco(GeneratedKeyHolder generatedKeyHolder) {
        Map<String, Object> fields = generatedKeyHolder.getKeys();
        requireNonNull(fields);
        return Endereco.builder()
                .id(parseId(fields, TABLE_COLUMN_ID))
                .cep(fields.get(TABLE_COLUMN_CEP).toString())
                .complemento(fields.get(TABLE_COLUMN_COMPLEMENTO).toString())
                .numero((Integer) fields.get(TABLE_COLUMN_NUMERO))
                .rua(fields.get(TABLE_COLUMN_RUA).toString())
                .bairro(fields.get(TABLE_COLUMN_BAIRRO).toString())
                .cidade(fields.get(TABLE_COLUMN_CIDADE).toString())
                .estado(fields.get(TABLE_COLUMN_ESTADO).toString())
                .build();
    }

}