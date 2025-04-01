package com.prpa.bancodigital.config;

import com.prpa.bancodigital.model.PoliticaTaxa;
import com.prpa.bancodigital.model.Tier;
import com.prpa.bancodigital.repository.PoliticaTaxaRepository;
import com.prpa.bancodigital.repository.TierRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.prpa.bancodigital.model.enums.TipoTaxa.MANUTENCAO;
import static com.prpa.bancodigital.model.enums.TipoTaxa.RENDIMENTO;
import static com.prpa.bancodigital.model.enums.UnidadeTaxa.FIXO;
import static com.prpa.bancodigital.model.enums.UnidadeTaxa.PORCENTAGEM;

@Configuration
public class ApplicationInitialization {

    public static final String[] REQUIRED_TIERS = {"COMUM", "SUPER", "PREMIUM"};

    // Key = REQUIRED_TIER index. 0 = comum, 1 = super, 2 = premium
    public static final Map<Integer, BigDecimal> REQUIRED_MAINTENCE_TAX = Map.of(
            0, BigDecimal.valueOf(12),
            1, BigDecimal.valueOf(8)
    );

    // Key = REQUIRED_TIER index. 0 = comum, 1 = super, 2 = premium
    public static final Map<Integer, BigDecimal> REQUIRED_INCOME = Map.of(
            0, BigDecimal.valueOf(0.5),
            1, BigDecimal.valueOf(0.7),
            2, BigDecimal.valueOf(0.9)
    );

    @Autowired
    private TierRepository tierRepository;

    @Autowired
    private PoliticaTaxaRepository politicaTaxaRepository;

    @PostConstruct
    public void init() {
        initRequiredTiers();
        initRequiredPoliticasDeTaxas();
    }

    private void initRequiredPoliticasDeTaxas() {
        List<Tier> tiers = Arrays.stream(REQUIRED_TIERS)
                .map(tierRepository::findByNomeIgnoreCase)
                .map(Optional::orElseThrow)
                .toList();

        String prefixNomeManutencao = "Manutenção conta corrente para clientes ";
        for (var entry : REQUIRED_MAINTENCE_TAX.entrySet()) {
            String tierName = prefixNomeManutencao + REQUIRED_TIERS[entry.getKey()];
            PoliticaTaxa taxaManutencao = new PoliticaTaxa(null, tierName, entry.getValue(), FIXO, MANUTENCAO);
            taxaManutencao.getTiers().add(tiers.get(entry.getKey()));
            politicaTaxaRepository.save(taxaManutencao);
        }

        String prefixNomeRendimento = "Rendimento conta poupança para clientes ";
        for (var entry : REQUIRED_INCOME.entrySet()) {
            String tierName = prefixNomeRendimento + REQUIRED_TIERS[entry.getKey()];
            PoliticaTaxa taxaRendimento = new PoliticaTaxa(null, tierName, entry.getValue(), PORCENTAGEM, RENDIMENTO);
            taxaRendimento.getTiers().add(tiers.get(entry.getKey()));
            politicaTaxaRepository.save(taxaRendimento);
        }
    }

    public void initRequiredTiers() {
        for (int i = 0; i < REQUIRED_TIERS.length; i++) {
            if (!tierRepository.existsByNomeIgnoreCase(REQUIRED_TIERS[i]))
                tierRepository.save(new Tier(null, REQUIRED_TIERS[i]));
        }
    }

}
