package com.prpa.bancodigital.repository.dao.mapper;

import com.prpa.bancodigital.model.PoliticaUso;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PoliticaUsoMapper implements RowMapper<PoliticaUso> {
    @Override
    public PoliticaUso mapRow(ResultSet rs, int rowNum) throws SQLException {
        PoliticaUso politicaUso = new PoliticaUso();
        politicaUso.setId(rs.getLong("id"));
        politicaUso.setLimiteDiario(rs.getBigDecimal("limite_diario_uso"));
        politicaUso.setLimiteCredito(rs.getBigDecimal("limite_credito"));
        return politicaUso;
    }
}
