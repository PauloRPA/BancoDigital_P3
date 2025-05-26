package com.prpa.bancodigital.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoliticaUso {

    public static final BigDecimal ILIMITADO = BigDecimal.valueOf(-100, 2);
    public static final PoliticaUso SEM_POLITICA = new PoliticaUso(null, ILIMITADO, ILIMITADO);

    private Long id;

    private BigDecimal limiteDiario;

    private BigDecimal limiteCredito;

    private Set<Tier> tiers;

    public PoliticaUso(Long id, BigDecimal limiteDiario, BigDecimal limiteCredito) {
        this.id = id;
        this.limiteDiario = limiteDiario;
        this.limiteCredito = limiteCredito;
        this.tiers = new HashSet<>();
    }

    public void addTier(Tier tier) {
        if (this.tiers == null)
            this.tiers = new HashSet<>();
        this.tiers.add(tier);
    }
}