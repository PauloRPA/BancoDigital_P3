package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.repository.QueryResolver;
import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.Endereco;
import com.prpa.bancodigital.model.Tier;
import com.prpa.bancodigital.repository.dao.mapper.ClienteMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Component
public class ClienteDao extends AbstractDao<Cliente> {

    public static final String CLIENTE_TABLE_NAME = "cliente";

    public static final String QUERY_PARAM_DATA_NASCIMENTO = "dataNascimento";
    public static final String QUERY_PARAM_CPF = "cpf";
    public static final String QUERY_PARAM_NOME = "nome";
    public static final String QUERY_PARAM_ENDERECO = "endereco";
    public static final String QUERY_PARAM_TIER = "tier";

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
        jdbcClient.sql(resolver.get(getTableName(), "insert"))
                .param(QUERY_PARAM_NOME, toSave.getNome())
                .param(QUERY_PARAM_CPF, toSave.getCpf())
                .param(QUERY_PARAM_DATA_NASCIMENTO, toSave.getDataNascimento())
                .param(QUERY_PARAM_ENDERECO, toSave.getEndereco().getId())
                .param(QUERY_PARAM_TIER, toSave.getTier().getId())
                .update(generatedKeyHolder);
        Cliente saved = mapKeyHolderToTier(generatedKeyHolder);
        saved.setTier(toSave.getTier());
        saved.setEndereco(toSave.getEndereco());
        return saved;
    }

    private Cliente update(Cliente toUpdate) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(resolver.get(getTableName(), "update"))
                .param("id", toUpdate.getId())
                .param(QUERY_PARAM_NOME, toUpdate.getNome())
                .param(QUERY_PARAM_CPF, toUpdate.getCpf())
                .param(QUERY_PARAM_DATA_NASCIMENTO, toUpdate.getDataNascimento())
                .param(QUERY_PARAM_ENDERECO, toUpdate.getEndereco().getId())
                .param(QUERY_PARAM_TIER, toUpdate.getTier().getId())
                .update(generatedKeyHolder);
        return mapKeyHolderToTier(generatedKeyHolder);
    }

    private static Cliente mapKeyHolderToTier(GeneratedKeyHolder generatedKeyHolder) {
        Map<String, Object> fields = generatedKeyHolder.getKeys();
        requireNonNull(fields);
        Endereco endereco = new Endereco();
        endereco.setId(parseId(fields, "endereco_fk"));
        Tier tier = new Tier();
        tier.setId(parseId(fields, "tier_fk"));
        return Cliente.builder()
                .id(parseId(fields, "id"))
                .nome(fields.get("nome").toString())
                .cpf(fields.get("cpf").toString())
                .dataNascimento(LocalDate.from(DateTimeFormatter.ofPattern("yyyy-MM-dd").parse(fields.get("data_nascimento").toString())))
                .tier(tier)
                .endereco(endereco)
                .build();
    }

    public Optional<Cliente> findByNome(String nome) {
        return jdbcClient.sql(resolver.get(getTableName(), "findByNome"))
                .param(QUERY_PARAM_NOME, nome)
                .query(getRowMapper())
                .optional();
    }

    public Optional<Cliente> findByCpf(String cpf) {
        return jdbcClient.sql(resolver.get(getTableName(), "findByCpf"))
                .param(QUERY_PARAM_CPF, cpf)
                .query(getRowMapper())
                .optional();
    }

    public Optional<Cliente> findByNomeAndDataNascimento(String nome, LocalDate dataNascimento) {
        return jdbcClient.sql(resolver.get(getTableName(), "findByNomeAndDataNascimento"))
                .param(QUERY_PARAM_NOME, nome)
                .param(QUERY_PARAM_DATA_NASCIMENTO, dataNascimento)
                .query(getRowMapper())
                .optional();
    }
}