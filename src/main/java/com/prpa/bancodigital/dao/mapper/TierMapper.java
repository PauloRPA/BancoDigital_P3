package com.prpa.bancodigital.dao.mapper;

import com.prpa.bancodigital.model.PoliticaUso;
import com.prpa.bancodigital.model.Tier;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TierMapper implements RowMapper<Tier> {
    @Override
    public Tier mapRow(ResultSet rs, int rowNum) throws SQLException {
        Tier tier = new Tier();
        PoliticaUso politicaUso = new PoliticaUso();
        tier.setId(rs.getLong("id"));
        tier.setNome(rs.getString("nome"));
        politicaUso.setId(rs.getLong("politica_uso_fk"));
        tier.setPoliticaUso(politicaUso);
        return tier;
    }
}
