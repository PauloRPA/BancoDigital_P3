package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.repository.QueryResolver;
import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.Endereco;
import com.prpa.bancodigital.model.Tier;
import com.prpa.bancodigital.repository.dao.mapper.ClienteMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Component
public class ClienteDao extends AbstractDao<Cliente> {

    public static final String CLIENTE_TABLE_NAME = "cliente";

    public static final String QUERY_PARAM_NOME = "nome";
    public static final String QUERY_PARAM_CPF = "cpf";
    public static final String QUERY_PARAM_DATA_NASCIMENTO = "dataNascimento";

    public static final String TABLE_COLUMN_ID = "id";
    public static final String TABLE_COLUMN_NOME = "nome";
    public static final String TABLE_COLUMN_CPF = "cpf";
    public static final String TABLE_COLUMN_ENDERECO = "endereco_fk";
    public static final String TABLE_COLUMN_TIER_FK = "tier_fk";
    public static final String TABLE_COLUMN_DATA_NASCIMENTO = "data_nascimento";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public ClienteDao(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate, QueryResolver resolver) {
        super(jdbcClient, jdbcTemplate, resolver);
    }

    @Override
    protected String getTableName() {
        return CLIENTE_TABLE_NAME;
    }

    @Override
    protected RowMapper<Cliente> getRowMapper() {
        return new ClienteMapper();
    }

    @Override
    public Cliente save(Cliente toSave) {
        if (toSave.getId() != null && findById(toSave.getId()).isPresent())
            return update(toSave);
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        sql("insert")
                .paramSource(new BeanPropertySqlParameterSource(toSave))
                .update(generatedKeyHolder);
        Cliente saved = mapKeyHolderToTier(generatedKeyHolder);
        saved.setTier(toSave.getTier());
        saved.setEndereco(toSave.getEndereco());
        return saved;
    }

    private Cliente update(Cliente toUpdate) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        sql("update")
                .paramSource(new BeanPropertySqlParameterSource(toUpdate))
                .update(generatedKeyHolder);
        return mapKeyHolderToTier(generatedKeyHolder);
    }

    public Optional<Cliente> findByNome(String nome) {
        return sql("findByNome")
                .param(QUERY_PARAM_NOME, nome)
                .query(getRowMapper())
                .optional();
    }

    public Optional<Cliente> findByCpf(String cpf) {
        return sql("findByCpf")
                .param(QUERY_PARAM_CPF, cpf)
                .query(getRowMapper())
                .optional();
    }

    public Optional<Cliente> findByNomeAndDataNascimento(String nome, LocalDate dataNascimento) {
        return sql("findByNomeAndDataNascimento")
                .param(QUERY_PARAM_NOME, nome)
                .param(QUERY_PARAM_DATA_NASCIMENTO, dataNascimento)
                .query(getRowMapper())
                .optional();
    }

    private static Cliente mapKeyHolderToTier(GeneratedKeyHolder generatedKeyHolder) {
        Map<String, Object> fields = generatedKeyHolder.getKeys();
        requireNonNull(fields);
        Endereco endereco = new Endereco();
        endereco.setId(parseId(fields, TABLE_COLUMN_ENDERECO));
        Tier tier = new Tier();
        tier.setId(parseId(fields, TABLE_COLUMN_TIER_FK));
        return Cliente.builder()
                .id(parseId(fields, TABLE_COLUMN_ID))
                .nome(fields.get(TABLE_COLUMN_NOME).toString())
                .cpf(fields.get(TABLE_COLUMN_CPF).toString())
                .dataNascimento(parseLocalDate(fields, TABLE_COLUMN_DATA_NASCIMENTO, DATE_FORMAT))
                .tier(tier)
                .build();
    }

}