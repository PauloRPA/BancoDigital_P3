package com.prpa.bancodigital.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "PoliticaUso")
public class PoliticaUso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "limite_maximo_uso", scale = 2)
    private BigDecimal limiteMaximo;

    @ManyToMany
    @JoinTable(name = "politica_uso_join_tier",
            joinColumns = @JoinColumn(name = "politica_uso_id_fk"),
            inverseJoinColumns = @JoinColumn(name = "tier_id_fk"))
    private Set<Tier> tiers;

    public PoliticaUso() { }

    public PoliticaUso(Long id, BigDecimal limiteMaximo, Set<Tier> tiers) {
        this.id = id;
        this.limiteMaximo = limiteMaximo;
        this.tiers = tiers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getLimiteMaximo() {
        return limiteMaximo;
    }

    public void setLimiteMaximo(BigDecimal limiteMaximo) {
        this.limiteMaximo = limiteMaximo;
    }

    public Set<Tier> getTiers() {
        return tiers;
    }

    public void setTiers(Set<Tier> tiers) {
        this.tiers = tiers;
    }

}