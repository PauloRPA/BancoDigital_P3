package com.prpa.bancodigital.model;

import com.prpa.bancodigital.model.enums.TipoTaxa;

import java.util.List;

public class ContaCorrente extends ContaBancaria {

    public ContaCorrente() {
        super();
    }

    public ContaCorrente(Long id, String numero, String agencia, Cliente cliente) {
        super(id, numero, agencia, cliente);
        this.politicas = getCliente().getTier().getPoliticasTaxa().stream()
                .filter(pt -> pt.getTipoTaxa().equals(TipoTaxa.MANUTENCAO))
                .toList();
    }

    @Override
    public List<PoliticaTaxa> getPoliticas() {
        this.politicas = getCliente().getTier().getPoliticasTaxa().stream()
                .filter(pt -> pt.getTipoTaxa().equals(TipoTaxa.MANUTENCAO))
                .toList();
        return politicas;
    }

}
