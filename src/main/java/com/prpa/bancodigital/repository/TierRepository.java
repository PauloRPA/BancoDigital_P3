package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.exception.InvalidDeletionException;
import com.prpa.bancodigital.model.PoliticaUso;
import com.prpa.bancodigital.model.Tier;
import com.prpa.bancodigital.repository.dao.ClienteDao;
import com.prpa.bancodigital.repository.dao.JoinTierPoliticaTaxa;
import com.prpa.bancodigital.repository.dao.PoliticaUsoDao;
import com.prpa.bancodigital.repository.dao.TierDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TierRepository {

    private final TierDao tierDao;
    private final JoinTierPoliticaTaxa joinTierPoliticaTaxa;
    private final PoliticaUsoDao politicaUsoDao;
    private final ClienteDao clienteDao;

    public TierRepository(TierDao tierDao, JoinTierPoliticaTaxa joinTierPoliticaTaxa, PoliticaUsoDao politicaUsoDao, ClienteDao clienteDao) {
        this.tierDao = tierDao;
        this.joinTierPoliticaTaxa = joinTierPoliticaTaxa;
        this.politicaUsoDao = politicaUsoDao;
        this.clienteDao = clienteDao;
    }

    public Optional<Tier> findById(long id) {
        Optional<Tier> found = tierDao.findById(id);
        return found.map(this::fetchRelations);
    }

    private Tier fetchRelations(Tier tier) {
        tier.getPoliticaUso()
                .map(PoliticaUso::getId)
                .flatMap(politicaUsoDao::findById)
                .ifPresent(tier::setPoliticaUso);

        if (tier.getId() == null) return tier;
        joinTierPoliticaTaxa.findByTierId(tier.getId())
                .forEach(politicaTaxa -> {
                    tier.addPoliticaTaxa(politicaTaxa);
                    politicaTaxa.addTier(tier);
                });
        return tier;
    }

    public boolean existsById(long id) {
        return tierDao.existsById(id);
    }

    public List<Tier> findAll() {
        return tierDao.findAll().stream()
                .map(this::fetchRelations)
                .toList();
    }

    public Page<Tier> findAll(Pageable page) {
        return tierDao.findAll(page)
                .map(this::fetchRelations);
    }

    public Tier save(Tier toSave) {
        return tierDao.save(toSave);
    }

    public void deleteById(long id) {
        if (!clienteDao.findByTierId(id).isEmpty())
            throw new InvalidDeletionException("Existem clientes associados a este Tier");
        joinTierPoliticaTaxa.removeReferencesForTier(id);
        tierDao.deleteById(id);
    }

    public boolean existsByNomeIgnoreCase(String nome) {
        return tierDao.existsByNomeIgnoreCase(nome);
    }

    public Optional<Tier> findByNomeIgnoreCase(String nome) {
        return tierDao.findByNomeIgnoreCase(nome);
    }

}