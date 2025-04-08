package com.prpa.bancodigital.model;

import com.prpa.bancodigital.model.enums.TipoTaxa;
import com.prpa.bancodigital.model.enums.UnidadeTaxa;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PoliticaTaxa")
public class PoliticaTaxa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", unique = true, nullable = false)
    private String nome;

    @Column(name = "quantidade", nullable = false, scale = 2)
    private BigDecimal quantia;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidade_quantia", nullable = false)
    private UnidadeTaxa unidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_taxa", nullable = false)
    private TipoTaxa tipoTaxa;

    @ManyToMany
    @JoinTable(name = "politica_taxa_join_tier",
            joinColumns = @JoinColumn(name = "politica_taxa_id_fk"),
            inverseJoinColumns = @JoinColumn(name = "tier_id_fk"))
    private Set<Tier> tiers;

    public PoliticaTaxa() {
    }

    public PoliticaTaxa(Long id, String nome, BigDecimal quantia, UnidadeTaxa unidade, TipoTaxa tipoTaxa) {
        this.id = id;
        this.nome = nome;
        this.quantia = quantia;
        this.unidade = unidade;
        this.tipoTaxa = tipoTaxa;
        this.tiers = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getQuantia() {
        return quantia;
    }

    public void setQuantia(BigDecimal quantia) {
        this.quantia = quantia;
    }

    public UnidadeTaxa getUnidade() {
        return unidade;
    }

    public void setUnidade(UnidadeTaxa unidade) {
        this.unidade = unidade;
    }

    public TipoTaxa getTipoTaxa() {
        return tipoTaxa;
    }

    public void setTipoTaxa(TipoTaxa tipoTaxa) {
        this.tipoTaxa = tipoTaxa;
    }

    public Set<Tier> getTiers() {
        return tiers;
    }

    public void setTiers(Set<Tier> tiers) {
        this.tiers = tiers;
    }

}