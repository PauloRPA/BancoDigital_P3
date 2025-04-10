package com.prpa.bancodigital.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PoliticaUso")
public class PoliticaUso {

    public static final BigDecimal ILIMITADO = BigDecimal.valueOf(-100, 2);
    public static final PoliticaUso SEM_POLITICA = new PoliticaUso(null, ILIMITADO, ILIMITADO);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "limite_diario_uso", scale = 2)
    private BigDecimal limiteDiario;

    @Column(name = "limite_credito", scale = 2)
    private BigDecimal limiteCredito;

    @OneToMany(mappedBy = "politicaUso")
    private Set<Tier> tiers;

    public PoliticaUso() {
    }

    public PoliticaUso(Long id, BigDecimal limiteDiario, BigDecimal limiteCredito) {
        this.id = id;
        this.limiteDiario = limiteDiario;
        this.limiteCredito = limiteCredito;
        this.tiers = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(BigDecimal limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    public BigDecimal getLimiteDiario() {
        return limiteDiario;
    }

    public void setLimiteDiario(BigDecimal limiteDiario) {
        this.limiteDiario = limiteDiario;
    }

    public Set<Tier> getTiers() {
        return tiers;
    }

    public void setTiers(Set<Tier> tiers) {
        this.tiers = tiers;
    }

}