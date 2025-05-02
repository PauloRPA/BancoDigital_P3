package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.repository.dao.Dao;
import com.prpa.bancodigital.repository.dao.PoliticaTaxaDao;
import com.prpa.bancodigital.model.PoliticaTaxa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PoliticaTaxaRepository implements Dao<PoliticaTaxa> {

    private final PoliticaTaxaDao politicaTaxaDao;

    public PoliticaTaxaRepository(PoliticaTaxaDao politicaTaxaDao) {
        this.politicaTaxaDao = politicaTaxaDao;
    }

    @Override
    public Optional<PoliticaTaxa> findById(long id) {
        return politicaTaxaDao.findById(id);
    }

    @Override
    public boolean existsById(long id) {
        return politicaTaxaDao.existsById(id);
    }

    @Override
    public List<PoliticaTaxa> findAll() {
        return politicaTaxaDao.findAll();
    }

    @Override
    public Page<PoliticaTaxa> findAll(Pageable page) {
        return politicaTaxaDao.findAll(page);
    }

    @Override
    public PoliticaTaxa save(PoliticaTaxa toSave) {
        return politicaTaxaDao.save(toSave);
    }

    @Override
    public void deleteById(long id) {
        politicaTaxaDao.deleteById(id);
    }

    public boolean existsByNome(String nome) {
        return politicaTaxaDao.existsByNome(nome);
    }

    // TODO: relação com Tiers
    //    public List<PoliticaTaxa> findByTiers(Tier tier) {
    //        return List.of();
    //    }

    // TODO: relação com Tiers
    //    public boolean existsByTiers_NomeIgnoreCase(String nome) {
    //        return false;
    //    }

}