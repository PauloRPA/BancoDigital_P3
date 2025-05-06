package com.prpa.bancodigital.repository;

import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.Endereco;
import com.prpa.bancodigital.model.Tier;
import com.prpa.bancodigital.repository.dao.ClienteDao;
import com.prpa.bancodigital.repository.dao.EnderecoDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class ClienteRepository {

    private final ClienteDao clienteDao;
    private final EnderecoDao enderecoDao;
    private final TierRepository tierRepository;

    public ClienteRepository(ClienteDao clienteDao, EnderecoDao enderecoDao, TierRepository tierRepository) {
        this.clienteDao = clienteDao;
        this.enderecoDao = enderecoDao;
        this.tierRepository = tierRepository;
    }

    public Optional<Cliente> findById(long id) {
        return clienteDao.findById(id)
                .map(this::fetchRelations);
    }

    public boolean existsById(long id) {
        return findById(id).isPresent();
    }

    public List<Cliente> findAll() {
        return clienteDao.findAll().stream()
                .map(this::fetchRelations)
                .toList();
    }

    public Page<Cliente> findAll(Pageable page) {
        return clienteDao.findAll(page)
                .map(this::fetchRelations);
    }

    public void deleteById(long id) {
        clienteDao.deleteById(id);
    }

    public Cliente save(Cliente toSave) {
        Endereco saved = enderecoDao.saveIfNotExists(toSave.getEndereco());
        if (saved != null && saved.getId() != null) {
            toSave.setEndereco(saved);
        }
        return clienteDao.save(toSave);
    }

    public Cliente update(Cliente toUpdate) {
        Endereco savedEndereco = enderecoDao.save(toUpdate.getEndereco());
        Tier savedTier = tierRepository.save(toUpdate.getTier());
        Cliente savedCliente = save(toUpdate);
        savedCliente.setEndereco(savedEndereco);
        savedCliente.setTier(savedTier);
        return savedCliente;
    }

    public boolean existsByNome(String nome) {
        return findByNome(nome).isPresent();
    }

    public boolean existsByCpf(String cpf) {
        return findByCpf(cpf).isPresent();
    }

    public Optional<Cliente> findByNome(String nome) {
        return clienteDao.findByNome(nome)
                .map(this::fetchRelations);
    }

    public Optional<Cliente> findByCpf(String cpf) {
        return clienteDao.findByCpf(cpf)
                .map(this::fetchRelations);
    }

    public Optional<Cliente> findByNomeAndDataNascimento(String nome, LocalDate dataNascimento) {
        return clienteDao.findByNomeAndDataNascimento(nome, dataNascimento)
                .map(this::fetchRelations);
    }

    private Cliente fetchRelations(Cliente cliente) {
        Optional.ofNullable(cliente.getEndereco().getId())
                .flatMap(enderecoDao::findById)
                .ifPresent(cliente::setEndereco);

        Optional.ofNullable(cliente.getTier().getId())
                .flatMap(tierRepository::findById)
                .ifPresent(cliente::setTier);

        return cliente;
    }

}