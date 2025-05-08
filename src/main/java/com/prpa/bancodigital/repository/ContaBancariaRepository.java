package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.ContaBancaria;
import com.prpa.bancodigital.repository.dao.CartaoDao;
import com.prpa.bancodigital.repository.dao.ContaDao;
import com.prpa.bancodigital.repository.dao.JoinTierPoliticaTaxa;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

@Component
public class ContaBancariaRepository {

    private final ContaDao contaDao;
    private final ClienteRepository clienteRepository;
    private final JoinTierPoliticaTaxa joinTierPoliticaTaxa;
    private final CartaoDao cartaoDao;

    @Lazy
    public ContaBancariaRepository(ContaDao contaDao, ClienteRepository clienteRepository, JoinTierPoliticaTaxa joinTierPoliticaTaxa, CartaoDao cartaoDao) {
        this.contaDao = contaDao;
        this.clienteRepository = clienteRepository;
        this.joinTierPoliticaTaxa = joinTierPoliticaTaxa;
        this.cartaoDao = cartaoDao;
    }

    public Optional<ContaBancaria> findById(long id) {
        return contaDao.findById(id)
                .map(this::fetchRelations);
    }

    public boolean existsById(long id) {
        return findById(id).isPresent();
    }

    public List<ContaBancaria> findAll() {
        return contaDao.findAll().stream()
                .map(this::fetchRelations)
                .toList();
    }

    public Page<ContaBancaria> findAll(Pageable page) {
        return contaDao.findAll(page)
                .map(this::fetchRelations);
    }

    public ContaBancaria save(ContaBancaria toSave) {
        return contaDao.save(toSave);
    }

    public void deleteById(long id) {
        contaDao.deleteById(id);
    }

    public boolean existsByNumero(String numero) {
        return findByNumero(numero).isPresent();
    }

    public boolean existsByAgencia(String agencia) {
        return findByNumero(agencia).isPresent();
    }

    public Optional<ContaBancaria> findByAgencia(String agencia) {
        return contaDao.findByAgencia(agencia)
                .map(this::fetchRelations);
    }

    public Optional<ContaBancaria> findByNumero(String numero) {
        return contaDao.findByNumero(numero)
                .map(this::fetchRelations);
    }

    public List<ContaBancaria> findByCliente(Cliente cliente) {
        return contaDao.findByCliente(cliente).stream()
                .map(this::fetchRelations)
                .toList();
    }

    public Optional<ContaBancaria> findByNumeroAndAgencia(String numero, String agencia) {
        return contaDao.findByAgenciaAndNumero(agencia, numero)
                .map(this::fetchRelations);
    }

    public ContaBancaria fetchRelations(ContaBancaria contaBancaria) {
        if (!fetchCliente(contaBancaria))
            return contaBancaria;
        fetchPoliticas(contaBancaria);
        return fetchCartoes(contaBancaria);
    }

    private ContaBancaria fetchPoliticas(ContaBancaria contaBancaria) {
        if (isNull(contaBancaria.getCliente().getTier()) || isNull(contaBancaria.getCliente().getTier().getId()))
            return contaBancaria;
        joinTierPoliticaTaxa.findByTierId(contaBancaria.getCliente().getTier().getId())
                .forEach(contaBancaria::addPolitica);
        return contaBancaria;
    }

    private ContaBancaria fetchCartoes(ContaBancaria contaBancaria) {
        cartaoDao.findByConta(contaBancaria)
                .forEach(cartao -> {
                    cartao.setConta(contaBancaria);
                    contaBancaria.addCartao(cartao);
                });
        return contaBancaria;
    }

    private boolean fetchCliente(ContaBancaria contaBancaria) {
        if (isNull(contaBancaria.getCliente()) || isNull(contaBancaria.getCliente().getId()))
            return false;
        Optional<Cliente> clienteFound = clienteRepository.findById(contaBancaria.getCliente().getId());
        clienteFound.ifPresent(contaBancaria::setCliente);
        return clienteFound.isPresent();
    }

}