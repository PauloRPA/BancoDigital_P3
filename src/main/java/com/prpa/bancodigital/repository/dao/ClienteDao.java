package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.repository.QueryResolver;
import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.repository.dao.mapper.ClienteMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class ClienteDao extends AbstractDao<Cliente> {

    public static final String CLIENTE_TABLE_NAME = "cliente";

    public static final String QUERY_PARAM_NOME = "nome";
    public static final String QUERY_PARAM_CPF = "cpf";
    public static final String QUERY_PARAM_DATA_NASCIMENTO = "dataNascimento";
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
        Cliente saved = sql("insert")
                .paramSource(new BeanPropertySqlParameterSource(toSave))
                .query(getRowMapper())
                .single();
        saved.setTier(toSave.getTier());
        saved.setEndereco(toSave.getEndereco());
        return saved;
    }

    private Cliente update(Cliente toUpdate) {
        return sql("update")
                .paramSource(new BeanPropertySqlParameterSource(toUpdate))
                .query(getRowMapper())
                .single();
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

    public List<Cliente> findByTierId(long id) {
        return sql("findByTier")
                .param(QUERY_PARAM_TIER, id)
                .query(getRowMapper())
                .list();
    }

}