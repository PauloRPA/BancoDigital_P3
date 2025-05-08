package com.prpa.bancodigital.repository.dao.mapper;

import com.prpa.bancodigital.model.Fatura;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FaturaMapper implements RowMapper<Fatura> {

    @Override
    public Fatura mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Fatura.builder()
                .id(rs.getLong("id"))
                .abertura(rs.getDate("abertura").toLocalDate())
                .fechamento(rs.getDate("fechamento").toLocalDate())
                .valor(rs.getBigDecimal("valor"))
                .taxaUtilizacao(rs.getBoolean("taxa_utilizacao_cobrada"))
                .pago(rs.getBoolean("pago"))
                .build();
    }

}