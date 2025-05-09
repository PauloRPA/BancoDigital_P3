package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.model.PoliticaTaxa;
import com.prpa.bancodigital.model.Tier;
import com.prpa.bancodigital.repository.dao.Dao;
import com.prpa.bancodigital.repository.dao.JoinTierPoliticaTaxa;
import com.prpa.bancodigital.repository.dao.PoliticaTaxaDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PoliticaTaxaRepository implements Dao<PoliticaTaxa> {

    private final PoliticaTaxaDao politicaTaxaDao;
    private final JoinTierPoliticaTaxa joinTierPoliticaTaxa;

    public PoliticaTaxaRepository(PoliticaTaxaDao politicaTaxaDao, JoinTierPoliticaTaxa joinTierPoliticaTaxa) {
        this.politicaTaxaDao = politicaTaxaDao;
        this.joinTierPoliticaTaxa = joinTierPoliticaTaxa;
    }

    @Override
    public Optional<PoliticaTaxa> findById(long id) {
        return politicaTaxaDao.findById(id)
                .map(this::fetchRelations);
    }

    @Override
    public boolean existsById(long id) {
        return politicaTaxaDao.existsById(id);
    }

    @Override
    public List<PoliticaTaxa> findAll() {
        return politicaTaxaDao.findAll().stream()
                .map(this::fetchRelations)
                .toList();
    }

    @Override
    public Page<PoliticaTaxa> findAll(Pageable page) {
        return politicaTaxaDao.findAll(page)
                .map(this::fetchRelations);
    }

    @Override
    public PoliticaTaxa save(PoliticaTaxa toSave) {
        PoliticaTaxa saved = politicaTaxaDao.save(toSave);
        joinTierPoliticaTaxa.insertReferencesForPoliticaTaxa(saved.getId(), toSave);
        return saved;
    }

    @Override
    public void deleteById(long id) {
        joinTierPoliticaTaxa.removeReferencesForPoliticaTaxa(id);
        politicaTaxaDao.deleteById(id);
    }

    public boolean existsByNome(String nome) {
        return politicaTaxaDao.existsByNome(nome);
    }

    private PoliticaTaxa fetchRelations(PoliticaTaxa politicaTaxa) {
        if (politicaTaxa.getId() == null) return politicaTaxa;
        joinTierPoliticaTaxa.findByPoliticaTaxaId(politicaTaxa.getId())
                .forEach(politicaTaxa::addTier);
        return politicaTaxa;
    }

    public List<PoliticaTaxa> findByTiers(Tier tier) {
        if (tier.getId() == null)
            return List.of();
        List<PoliticaTaxa> byTierId = joinTierPoliticaTaxa.findByTierId(tier.getId());
        for (PoliticaTaxa politicaTaxa : byTierId)
            politicaTaxa.addTier(tier);
        return byTierId;
    }

}