package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.model.PoliticaUso;
import com.prpa.bancodigital.repository.dao.PoliticaUsoDao;
import com.prpa.bancodigital.repository.dao.TierDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PoliticaUsoRepository {

    private final PoliticaUsoDao politicaUsoDao;
    private final TierDao tierDao;

    public PoliticaUsoRepository(PoliticaUsoDao politicaUsoDao, TierDao tierDao) {
        this.politicaUsoDao = politicaUsoDao;
        this.tierDao = tierDao;
    }

    public Optional<PoliticaUso> findById(long id) {
        return politicaUsoDao.findById(id)
                .map(this::fetchRelations);
    }

    public List<PoliticaUso> findAll() {
        return politicaUsoDao.findAll().stream()
                .map(this::fetchRelations)
                .toList();
    }

    public Page<PoliticaUso> findAll(Pageable page) {
        return politicaUsoDao.findAll(page)
                .map(this::fetchRelations);
    }

    public PoliticaUso save(PoliticaUso toSave) {
        return politicaUsoDao.save(toSave);
    }

    public void deleteById(long id) {
        politicaUsoDao.deleteById(id);
    }

    private PoliticaUso fetchRelations(PoliticaUso politicaUso) {
        if (politicaUso.getId() == null) return politicaUso;
        tierDao.findByPoliticaUsoId(politicaUso.getId())
                .forEach(politicaUso::addTier);
        return politicaUso;
    }

}