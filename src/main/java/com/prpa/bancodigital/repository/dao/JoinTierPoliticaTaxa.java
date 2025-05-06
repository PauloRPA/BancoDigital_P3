package com.prpa.bancodigital.repository.dao;

import com.prpa.bancodigital.config.repository.QueryResolver;
import com.prpa.bancodigital.model.PoliticaTaxa;
import com.prpa.bancodigital.model.Tier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JoinTierPoliticaTaxa {

    public static final String POLITICA_TAXA_JOIN_TIER_TABLE_NAME = "politica_taxa_join_tier";
    private final TierDao tierDao;
    private final PoliticaTaxaDao politicaTaxaDao;
    private final JdbcClient jdbcClient;
    private final QueryResolver resolver;

    public JoinTierPoliticaTaxa(TierDao tierDao, PoliticaTaxaDao politicaTaxaDao, JdbcClient jdbcClient, QueryResolver resolver) {
        this.tierDao = tierDao;
        this.politicaTaxaDao = politicaTaxaDao;
        this.jdbcClient = jdbcClient;
        this.resolver = resolver;
    }

    public List<Tier> findByPoliticaTaxaId(Long politicaTaxaId) {
        return jdbcClient.sql(resolver.get(tierDao.getTableName(), "findByPoliticaTaxaId"))
                .param("id", politicaTaxaId)
                .query(tierDao.getRowMapper())
                .list();
    }

    public List<PoliticaTaxa> findByTierId(Long tierId) {
        return jdbcClient.sql(resolver.get(politicaTaxaDao.getTableName(), "findByTierId"))
                .param("id", tierId)
                .query(politicaTaxaDao.getRowMapper())
                .list();
    }

    private void deleteByPoliticaTaxaId(long politicaTaxaId) {
        jdbcClient.sql(resolver.get(getTableName(), "deleteByPoliticaTaxaId"))
                .param("id", politicaTaxaId)
                .update();
    }

    private void deleteByTierId(long tierId) {
        jdbcClient.sql(resolver.get(getTableName(), "deleteByTierId"))
                .param("id", tierId)
                .update();
    }

    public void removeReferencesForTier(long id) {
        Optional<Tier> byId = tierDao.findById(id);
        if (byId.isEmpty()) return;
        deleteByTierId(id);
    }

    public void removeReferencesForPoliticaTaxa(long id) {
        Optional<PoliticaTaxa> byId = politicaTaxaDao.findById(id);
        if (byId.isEmpty()) return;
        deleteByPoliticaTaxaId(id);
    }

    public void insertReferencesForPoliticaTaxa(Long savedId, PoliticaTaxa toSave) {
        for (Tier tier : toSave.getTiers()) {
            if (tier.getId() != null && tierDao.findById(tier.getId()).isPresent()) {
                jdbcClient.sql(resolver.get(getTableName(), "insert"))
                        .param("tier_id_fk", tier.getId())
                        .param("politica_taxa_id_fk", savedId)
                        .update();
            }
        }
    }

    private String getTableName() {
        return POLITICA_TAXA_JOIN_TIER_TABLE_NAME;
    }

}