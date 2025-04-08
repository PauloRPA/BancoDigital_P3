package com.prpa.bancodigital.model;

import com.prpa.bancodigital.model.enums.TipoTaxa;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.List;

@Entity
@Table(name = "Conta_poupanca")
public class ContaPoupanca extends ContaBancaria {

    @Transient
    private List<PoliticaTaxa> rendimento;

    public ContaPoupanca() {
        super();
    }

    public ContaPoupanca(Long id, String numero, String agencia, Cliente cliente) {
        super(id, numero, agencia, cliente);
        this.rendimento = cliente.getTier().getPoliticasTaxa().stream()
                .filter(politicaTaxa -> politicaTaxa.getTipoTaxa().equals(TipoTaxa.RENDIMENTO))
                .toList();
    }

    @Override
    public List<PoliticaTaxa> getPoliticas() {
        this.rendimento = cliente.getTier().getPoliticasTaxa().stream()
                .filter(politicaTaxa -> politicaTaxa.getTipoTaxa().equals(TipoTaxa.RENDIMENTO))
                .toList();
        return rendimento;
    }

}
