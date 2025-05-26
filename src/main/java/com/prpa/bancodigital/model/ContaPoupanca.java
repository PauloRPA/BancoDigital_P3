package com.prpa.bancodigital.model;

import com.prpa.bancodigital.model.enums.TipoTaxa;

import java.util.List;

public class ContaPoupanca extends ContaBancaria {


    public ContaPoupanca() {
        super();
    }

    public ContaPoupanca(Long id, String numero, String agencia, Cliente cliente) {
        super(id, numero, agencia, cliente);
        this.politicas = cliente.getTier().getPoliticasTaxa().stream()
                .filter(politicaTaxa -> politicaTaxa.getTipoTaxa().equals(TipoTaxa.RENDIMENTO))
                .toList();
    }

    @Override
    public List<PoliticaTaxa> getPoliticas() {
        this.politicas = cliente.getTier().getPoliticasTaxa().stream()
                .filter(politicaTaxa -> politicaTaxa.getTipoTaxa().equals(TipoTaxa.RENDIMENTO))
                .toList();
        return politicas;
    }

}
