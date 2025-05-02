package com.prpa.bancodigital.repository.dao.mapper;

import com.prpa.bancodigital.model.Endereco;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EnderecoMapper implements RowMapper<Endereco> {

    @Override
    public Endereco mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Endereco.builder()
                .id(rs.getLong("id"))
                .cep(rs.getString("cep"))
                .complemento(rs.getString("complemento"))
                .numero(rs.getInt("numero"))
                .rua(rs.getString("rua"))
                .bairro(rs.getString("bairro"))
                .cidade(rs.getString("cidade"))
                .estado(rs.getString("estado"))
                .build();
    }

}