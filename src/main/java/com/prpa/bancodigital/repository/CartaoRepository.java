package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.model.Cartao;
import com.prpa.bancodigital.model.ContaBancaria;
import com.prpa.bancodigital.repository.dao.CartaoDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Component
public class CartaoRepository {

    private final CartaoDao cartaoDao;
    private final ContaBancariaRepository contaBancariaRepository;

    public CartaoRepository(CartaoDao cartaoDao, ContaBancariaRepository contaBancariaRepository) {
        this.cartaoDao = cartaoDao;
        this.contaBancariaRepository = contaBancariaRepository;
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
        return cartaoDao.save(toSave);
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
        return cartao;
    }

}