package com.prpa.bancodigital.config;

import com.prpa.bancodigital.model.Tier;
import com.prpa.bancodigital.repository.TierRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationInitialization {

    public static final String[] REQUIRED_TIERS = {"COMUM", "SUPER", "PREMIUM"};

    @Autowired
    private TierRepository tierRepository;

    @PostConstruct
    public void initRequiredTiers() {
        for (int i = 0; i < REQUIRED_TIERS.length; i++) {
            if (!tierRepository.existsByNomeIgnoreCase(REQUIRED_TIERS[i]))
                tierRepository.save(new Tier(null, REQUIRED_TIERS[i]));
        }
    }

}
