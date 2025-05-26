package com.prpa.bancodigital.repository.dao.mapper;

import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.Endereco;
import com.prpa.bancodigital.model.Tier;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteMapper implements RowMapper<Cliente> {

    @Override
    public Cliente mapRow(ResultSet rs, int rowNum) throws SQLException {
        Endereco endereco = new Endereco();
        endereco.setId(rs.getLong("endereco_fk"));
        Tier tier = new Tier();
        tier.setId(rs.getLong("tier_fk"));
        return Cliente.builder()
                .id(rs.getLong("id"))
                .nome(rs.getString("nome"))
                .cpf(rs.getString("cpf"))
                .dataNascimento(rs.getDate("data_nascimento").toLocalDate())
                .endereco(endereco)
                .tier(tier)
                .build();
    }

}