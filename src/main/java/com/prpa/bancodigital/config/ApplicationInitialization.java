package com.prpa.bancodigital.config;

import com.prpa.bancodigital.model.PoliticaTaxa;
import com.prpa.bancodigital.model.PoliticaUso;
import com.prpa.bancodigital.model.Tier;
import com.prpa.bancodigital.repository.PoliticaTaxaRepository;
import com.prpa.bancodigital.repository.PoliticaUsoRepository;
import com.prpa.bancodigital.repository.TierRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.prpa.bancodigital.model.PoliticaUso.ILIMITADO;
import static com.prpa.bancodigital.model.enums.TipoTaxa.MANUTENCAO;
import static com.prpa.bancodigital.model.enums.TipoTaxa.RENDIMENTO;
import static com.prpa.bancodigital.model.enums.UnidadeTaxa.FIXO;
import static com.prpa.bancodigital.model.enums.UnidadeTaxa.PORCENTAGEM;

@Configuration
public class ApplicationInitialization {

    public static final String[] REQUIRED_TIERS = {"COMUM", "SUPER", "PREMIUM"};

    public static final double TAXA_UTILIZACAO = 0.05;
    public static final double APLICAR_AO_EXCEDER = 0.8;

    // Key = REQUIRED_TIER index. 0 = comum, 1 = super, 2 = premium
    public static final Map<Integer, BigDecimal> REQUIRED_MAINTENANCE_TAX = Map.of(
            0, BigDecimal.valueOf(12),
            1, BigDecimal.valueOf(8)
    );

    // Key = REQUIRED_TIER index. 0 = comum, 1 = super, 2 = premium
    public static final Map<Integer, BigDecimal> REQUIRED_INCOME = Map.of(
            0, BigDecimal.valueOf(0.5),
            1, BigDecimal.valueOf(0.7),
            2, BigDecimal.valueOf(0.9)
    );

    // Key = REQUIRED_TIER index. 0 = comum, 1 = super, 2 = premium
    public static final Map<Integer, BigDecimal> REQUIRED_CREDIT_LIMIT = Map.of(
            0, BigDecimal.valueOf(1000),
            1, BigDecimal.valueOf(5000),
            2, BigDecimal.valueOf(10000)
    );

    @Autowired
    private TierRepository tierRepository;

    @Autowired
    private PoliticaTaxaRepository politicaTaxaRepository;
    @Autowired
    private PoliticaUsoRepository politicaUsoRepository;

    @PostConstruct
    public void init() {
//        initRequiredTiers();
//        initRequiredPoliticasDeTaxas();
//        initRequiredPoliticasDeUso();
    }

    private void initRequiredPoliticasDeUso() {
        List<Tier> tiers = Arrays.stream(REQUIRED_TIERS)
                .map(tierRepository::findByNomeIgnoreCase)
                .map(Optional::orElseThrow)
                .toList();

        for (var entry : REQUIRED_CREDIT_LIMIT.entrySet()) {
//            if (politicaUsoRepository.existsByTiers_NomeIgnoreCase(REQUIRED_TIERS[entry.getKey()]))
//                continue;
            PoliticaUso politicaUso = new PoliticaUso(null, ILIMITADO, entry.getValue());
            politicaUso.getTiers().add(tiers.get(entry.getKey()));
            tiers.get(entry.getKey()).setPoliticaUso(politicaUso);
            politicaUsoRepository.save(politicaUso);
            tierRepository.save(tiers.get(entry.getKey()));
        }
    }

    private void initRequiredPoliticasDeTaxas() {
        List<Tier> tiers = Arrays.stream(REQUIRED_TIERS)
                .map(tierRepository::findByNomeIgnoreCase)
                .map(Optional::orElseThrow)
                .toList();

        String prefixNomeManutencao = "Manutenção conta corrente para clientes ";
        for (var entry : REQUIRED_MAINTENANCE_TAX.entrySet()) {
//            if (politicaTaxaRepository.existsByTiers_NomeIgnoreCase(REQUIRED_TIERS[entry.getKey()]))
//                continue;
            String tierName = prefixNomeManutencao + REQUIRED_TIERS[entry.getKey()];
            PoliticaTaxa taxaManutencao = new PoliticaTaxa(null, tierName, entry.getValue(), FIXO, MANUTENCAO);
            taxaManutencao.getTiers().add(tiers.get(entry.getKey()));
            politicaTaxaRepository.save(taxaManutencao);
        }

        String prefixNomeRendimento = "Rendimento conta poupança para clientes ";
        for (var entry : REQUIRED_INCOME.entrySet()) {
//            if (politicaTaxaRepository.existsByTiers_NomeIgnoreCase(REQUIRED_TIERS[entry.getKey()]))
//                continue;
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
