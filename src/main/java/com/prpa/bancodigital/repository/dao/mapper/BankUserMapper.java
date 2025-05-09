package com.prpa.bancodigital.repository.dao.mapper;

import com.prpa.bancodigital.model.BankUser;
import com.prpa.bancodigital.model.Role;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class BankUserMapper implements RowMapper<BankUser> {
    @Override
    public BankUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        String[] roles = ((String[]) rs.getArray("roles").getArray());
        return BankUser.builder()
                .id(rs.getLong("id"))
                .username(rs.getString("username"))
                .password(rs.getString("password"))
                .email(rs.getString("email"))
                .roles(Arrays.stream(roles).map(Role::fromName).toList())
                .build();
    }
}