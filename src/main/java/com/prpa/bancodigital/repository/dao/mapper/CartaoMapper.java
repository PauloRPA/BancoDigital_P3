package com.prpa.bancodigital.repository.dao.mapper;

import com.prpa.bancodigital.model.*;
import com.prpa.bancodigital.model.enums.TipoCartao;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CartaoMapper implements RowMapper<Cartao> {

    @Override
    public Cartao mapRow(ResultSet rs, int rowNum) throws SQLException {
        TipoCartao tipo = TipoCartao.fromName(rs.getString("tipo"))
                .orElseThrow(() -> new IllegalStateException("TipoCartao lido do banco de dados invalido"));
        Cartao cartao = tipo.equals(TipoCartao.CARTAO_CREDITO) ? new CartaoCredito() : new CartaoDebito();
        cartao.setId(rs.getLong("id"));
        cartao.setNumero(rs.getString("numero"));
        cartao.setVencimento(rs.getDate("vencimento").toLocalDate());
        cartao.setCcv(rs.getString("ccv"));
        cartao.setAtivo(rs.getBoolean("ativo"));
        cartao.setSenha(rs.getString("senha"));
        cartao.setLimiteCredito(rs.getBigDecimal("limite_credito"));
        cartao.setLimiteDiario(rs.getBigDecimal("limite_diario"));
        cartao.setTipo(tipo);
        ContaBancaria conta = new ContaPoupanca();
        conta.setId(rs.getLong("conta_fk"));
        cartao.setConta(conta);
        return cartao;
    }

}