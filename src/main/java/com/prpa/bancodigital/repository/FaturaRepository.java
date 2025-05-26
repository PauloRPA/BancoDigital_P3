package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.model.CartaoCredito;
import com.prpa.bancodigital.model.Fatura;
import com.prpa.bancodigital.repository.dao.CartaoDao;
import com.prpa.bancodigital.repository.dao.FaturaDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FaturaRepository {

    private final FaturaDao faturaDao;
    private final CartaoDao cartaoDao;

    public FaturaRepository(FaturaDao faturaDao, CartaoDao cartaoDao) {
        this.faturaDao = faturaDao;
        this.cartaoDao = cartaoDao;
    }

    public Optional<Fatura> findById(long id) {
        return faturaDao.findById(id)
                .map(this::fetchRelations);
    }

    public boolean existsById(long id) {
        return faturaDao.existsById(id);
    }

    public List<Fatura> findAll() {
        return faturaDao.findAll().stream()
                .map(this::fetchRelations)
                .toList();
    }

    public Page<Fatura> findAll(Pageable page) {
        return faturaDao.findAll(page)
                .map(this::fetchRelations);
    }

    public Fatura save(Fatura toSave) {
        return faturaDao.save(toSave);
    }

    public void deleteById(long id) {
        faturaDao.deleteById(id);
    }

    public Fatura fetchRelations(Fatura fatura) {
        Optional.ofNullable(fatura.getCartao())
                .map(CartaoCredito::getId)
                .flatMap(cartaoDao::findById)
                .filter(cartao -> cartao.getClass().isAssignableFrom(CartaoCredito.class))
                .map(CartaoCredito.class::cast)
                .ifPresent(fatura::setCartao);
        return fatura;
    }

}