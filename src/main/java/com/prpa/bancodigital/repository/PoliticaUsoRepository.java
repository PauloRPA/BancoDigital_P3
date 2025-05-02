package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.model.PoliticaUso;
import com.prpa.bancodigital.repository.dao.PoliticaUsoDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PoliticaUsoRepository {

    private final PoliticaUsoDao politicaUsoDao;

    public PoliticaUsoRepository(PoliticaUsoDao politicaUsoDao) {
        this.politicaUsoDao = politicaUsoDao;
    }

    public Optional<PoliticaUso> findById(long id) {
        return politicaUsoDao.findById(id);
    }

    public List<PoliticaUso> findAll() {
        return politicaUsoDao.findAll();
    }

    public Page<PoliticaUso> findAll(Pageable page) {
        return politicaUsoDao.findAll(page);
    }

    public PoliticaUso save(PoliticaUso toSave) {
        return politicaUsoDao.save(toSave);
    }

    public void deleteById(long id) {
        politicaUsoDao.deleteById(id);
    }

    // TODO: Relação com Tier
    //    boolean existsByTiers_NomeIgnoreCase(String requiredTier);

}