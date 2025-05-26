package com.prpa.bancodigital.repository.dao.mapper;

import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.ContaBancaria;
import com.prpa.bancodigital.model.ContaCorrente;
import com.prpa.bancodigital.model.ContaPoupanca;
import com.prpa.bancodigital.model.enums.TipoConta;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.prpa.bancodigital.model.enums.TipoConta.CONTA_CORRENTE;

public class ContaMapper implements RowMapper<ContaBancaria> {
    @Override
    public ContaBancaria mapRow(ResultSet rs, int rowNum) throws SQLException {
        TipoConta tipoConta = TipoConta.fromName(rs.getString("tipo"))
                .orElseThrow(() -> new IllegalStateException("Tipo de conta lida do banco de dados inválida"));
        ContaBancaria conta = tipoConta.equals(CONTA_CORRENTE) ? new ContaCorrente() : new ContaPoupanca();
        Cliente cliente = new Cliente();
        cliente.setId(rs.getLong("cliente_fk"));
        conta.setId(rs.getLong("id"));
        conta.setNumero(rs.getString("numero"));
        conta.setAgencia(rs.getString("agencia"));
        conta.setSaldo(rs.getBigDecimal("saldo"));
        conta.setTipo(tipoConta);
        conta.setCartoes(new ArrayList<>());
        conta.setCliente(cliente);
        return conta;
    }

}