package com.prpa.bancodigital.dao.mapper;

import com.prpa.bancodigital.model.PoliticaTaxa;
import com.prpa.bancodigital.model.enums.TipoTaxa;
import com.prpa.bancodigital.model.enums.UnidadeTaxa;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PoliticaTaxaMapper implements RowMapper<PoliticaTaxa> {
    @Override
    public PoliticaTaxa mapRow(ResultSet rs, int rowNum) throws SQLException {
        PoliticaTaxa politicaTaxa = new PoliticaTaxa();
        politicaTaxa.setId(rs.getLong("id"));
        politicaTaxa.setNome(rs.getString("nome"));
        politicaTaxa.setQuantia(rs.getBigDecimal("quantidade"));
        politicaTaxa.setTipoTaxa(TipoTaxa.fromName(rs.getString("tipo_taxa")));
        politicaTaxa.setUnidade(UnidadeTaxa.fromName(rs.getString("unidade_quantia")));
        return politicaTaxa;
    }
}
