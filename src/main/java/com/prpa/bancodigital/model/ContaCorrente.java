package com.prpa.bancodigital.model;

import com.prpa.bancodigital.model.enums.TipoTaxa;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.List;

@Entity
@Table(name = "Conta_corrente")
public class ContaCorrente extends ContaBancaria {

    @Transient
    private List<PoliticaTaxa> manutencao;

    public ContaCorrente() {
        super();
    }

    public ContaCorrente(Long id, String numero, String agencia, Cliente cliente) {
        super(id, numero, agencia, cliente);
        this.manutencao = getCliente().getTier().getPoliticasTaxa().stream()
                .filter(pt -> pt.getTipoTaxa().equals(TipoTaxa.MANUTENCAO))
                .toList();
    }

    @Override
    public List<PoliticaTaxa> getPoliticas() {
        this.manutencao = getCliente().getTier().getPoliticasTaxa().stream()
                .filter(pt -> pt.getTipoTaxa().equals(TipoTaxa.MANUTENCAO))
                .toList();
        return manutencao;
    }

}
