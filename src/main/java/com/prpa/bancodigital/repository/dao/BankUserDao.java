package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.repository.QueryResolver;
import com.prpa.bancodigital.model.BankUser;
import com.prpa.bancodigital.model.Role;
import com.prpa.bancodigital.repository.dao.mapper.BankUserMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.Optional;

@Component
public class BankUserDao extends AbstractDao<BankUser> {

    public static final String BANK_USER_TABLE_NAME = "bank_user";

    public static final String QUERY_PARAM_USERNAME = "username";
    public static final String QUERY_PARAM_EMAIL = "email";
    public static final String QUERY_PARAM_PASSWORD = "password";
    public static final String QUERY_PARAM_ROLES = "roles";

    protected BankUserDao(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate, QueryResolver resolver) {
        super(jdbcClient, jdbcTemplate, resolver);
    }

    @Override
    protected String getTableName() {
        return BANK_USER_TABLE_NAME;
    }

    @Override
    protected RowMapper<BankUser> getRowMapper() {
        return new BankUserMapper();
    }

    @Override
    public BankUser save(BankUser toSave) {
        return sql("insert")
                .param(QUERY_PARAM_USERNAME, toSave.getUsername())
                .param(QUERY_PARAM_EMAIL, toSave.getEmail())
                .param(QUERY_PARAM_PASSWORD, toSave.getPassword())
                .param(QUERY_PARAM_ROLES, toSave.getRoles().stream().map(Role::name).toArray(String[]::new), Types.ARRAY)
                .query(getRowMapper())
                .single();
    }

    public Optional<BankUser> findByUsername(String username) {
        return sql("findByUsername")
                .param(QUERY_PARAM_USERNAME, username)
                .query(getRowMapper())
                .optional();
    }
}