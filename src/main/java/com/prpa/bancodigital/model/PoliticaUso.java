package com.prpa.bancodigital.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
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

}