package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.model.Cartao;
import com.prpa.bancodigital.model.CartaoCredito;
import com.prpa.bancodigital.model.ContaBancaria;
import com.prpa.bancodigital.model.Fatura;
import com.prpa.bancodigital.repository.dao.CartaoDao;
import com.prpa.bancodigital.repository.dao.FaturaDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class CartaoRepository {

    private final CartaoDao cartaoDao;
    private final ContaBancariaRepository contaBancariaRepository;
    private final FaturaDao faturaDao;

    public CartaoRepository(CartaoDao cartaoDao, ContaBancariaRepository contaBancariaRepository, FaturaDao faturaDao) {
        this.cartaoDao = cartaoDao;
        this.contaBancariaRepository = contaBancariaRepository;
        this.faturaDao = faturaDao;
    }

    public Optional<Cartao> findById(long id) {
        return cartaoDao.findById(id)
                .map(this::fetchRelations);
    }

    public boolean existsById(long id) {
        return cartaoDao.findById(id).isPresent();
    }

    public List<Cartao> findAll() {
        return cartaoDao.findAll().stream()
                .map(this::fetchRelations)
                .toList();
    }

    public Page<Cartao> findAll(Pageable page) {
        return cartaoDao.findAll(page)
                .map(this::fetchRelations);
    }

    public Cartao save(Cartao toSave) {
        Cartao saved = cartaoDao.save(toSave);
        if (isNull(toSave.getId()) && saved instanceof CartaoCredito savedCredito)
            savedCredito.addFatura(faturaDao.save(new Fatura(null, savedCredito)));
        else if (saved instanceof CartaoCredito savedCredito)
            savedCredito.getFaturas().forEach(faturaDao::save);
        Optional.ofNullable(toSave.getConta())
                .filter(conta -> nonNull(conta.getId()))
                .ifPresent(contaBancariaRepository::save);
        return saved;
    }

    public void deleteById(long id) {
        cartaoDao.deleteById(id);
    }

    public boolean existsByNumero(String numero) {
        return cartaoDao.findByNumero(numero).isPresent();
    }

    public List<Cartao> findByConta(ContaBancaria conta) {
        return cartaoDao.findByConta(conta).stream()
                .map(this::fetchRelations)
                .toList();
    }

    private Cartao fetchRelations(Cartao cartao) {
        Optional.ofNullable(cartao.getConta())
                .filter(conta -> nonNull(conta.getId()))
                .map(ContaBancaria::getId)
                .flatMap(contaBancariaRepository::findById)
                .ifPresent(cartao::setConta);

        if (cartao instanceof CartaoCredito cartaoCredito) {
            fetchFaturas(cartaoCredito);
        }
        return cartao;
    }

    private void fetchFaturas(CartaoCredito cartaoCredito) {
        faturaDao.findByCartao(cartaoCredito)
                .forEach(fatura -> {
                    fatura.setCartao(cartaoCredito);
                    cartaoCredito.addFatura(fatura);
                });
    }

}