package com.prpa.bancodigital.model;

import com.prpa.bancodigital.model.enums.TipoTaxa;
import com.prpa.bancodigital.model.enums.UnidadeTaxa;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoliticaTaxa {

    private Long id;

    private String nome;

    private BigDecimal quantia;

    private UnidadeTaxa unidade;

    private TipoTaxa tipoTaxa;

    private Set<Tier> tiers;

    public PoliticaTaxa(Long id, String nome, BigDecimal quantia, UnidadeTaxa unidade, TipoTaxa tipoTaxa) {
        this.id = id;
        this.nome = nome;
        this.quantia = quantia;
        this.unidade = unidade;
        this.tipoTaxa = tipoTaxa;
        this.tiers = new HashSet<>();
    }

    public void addTier(Tier tier) {
        if (this.tiers == null)
            this.tiers = new HashSet<>();
        this.tiers.add(tier);
    }
}