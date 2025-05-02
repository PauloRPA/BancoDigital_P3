package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.model.Tier;
import com.prpa.bancodigital.repository.dao.TierDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TierRepository {

    private final TierDao tierDao;

    public TierRepository(TierDao tierDao) {
        this.tierDao = tierDao;
    }

    public Optional<Tier> findById(long id) {
        return tierDao.findById(id);
    }

    public boolean existsById(long id) {
        return tierDao.existsById(id);
    }

    public List<Tier> findAll() {
        return tierDao.findAll();
    }

    public Page<Tier> findAll(Pageable page) {
        return tierDao.findAll(page);
    }

    public Tier save(Tier toSave) {
        return tierDao.save(toSave);
    }

    public void deleteById(long id) {
        tierDao.deleteById(id);
    }

    public boolean existsByNomeIgnoreCase(String nome) {
        return tierDao.existsByNomeIgnoreCase(nome);
    }

    public Optional<Tier> findByNomeIgnoreCase(String nome) {
        return tierDao.findByNomeIgnoreCase(nome);
    }

}
