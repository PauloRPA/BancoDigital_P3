package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.repository.QueryResolver;
import com.prpa.bancodigital.model.BankUser;
import com.prpa.bancodigital.model.Role;
import com.prpa.bancodigital.repository.dao.mapper.BankUserMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Array;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Component
public class BankUserDao extends AbstractDao<BankUser> {

    public static final String BANK_USER_TABLE_NAME = "bank_user";

    public static final String QUERY_PARAM_USERNAME = "username";
    public static final String QUERY_PARAM_EMAIL = "email";
    public static final String QUERY_PARAM_PASSWORD = "password";
    public static final String QUERY_PARAM_ROLES = "roles";

    public static final String TABLE_COLUMN_ID = "id";
    public static final String TABLE_COLUMN_USERNAME = "username";
    public static final String TABLE_COLUMN_EMAIL = "email";
    public static final String TABLE_COLUMN_PASSWORD = "password";
    public static final String TABLE_COLUMN_ROLES = "roles";

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
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        sql("insert")
                .param(QUERY_PARAM_USERNAME, toSave.getUsername())
                .param(QUERY_PARAM_EMAIL, toSave.getEmail())
                .param(QUERY_PARAM_PASSWORD, toSave.getPassword())
                .param(QUERY_PARAM_ROLES, toSave.getRoles().stream().map(Role::name).toArray(String[]::new), Types.ARRAY)
                .update(generatedKeyHolder);
        return mapKeyHolderToBankUser(generatedKeyHolder);
    }

    private static BankUser mapKeyHolderToBankUser(GeneratedKeyHolder generatedKeyHolder)  {
        Map<String, Object> fields = generatedKeyHolder.getKeys();
        requireNonNull(fields);
        String[] roles;
        try {
            roles = (String[]) ((Array) fields.get(TABLE_COLUMN_ROLES)).getArray();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível obter roles do banco de dados");
        }
        return BankUser.builder()
                .id(parseId(fields, TABLE_COLUMN_ID))
                .username(fields.get(TABLE_COLUMN_USERNAME).toString())
                .email(fields.get(TABLE_COLUMN_EMAIL).toString())
                .password(fields.get(TABLE_COLUMN_PASSWORD).toString())
                .roles(Arrays.stream(roles).map(Role::fromName).toList())
                .build();
    }

    public Optional<BankUser> findByUsername(String username) {
        return sql("findByUsername")
                .param(QUERY_PARAM_USERNAME, username)
                .query(getRowMapper())
                .optional();
    }
}