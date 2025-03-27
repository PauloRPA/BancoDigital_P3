package com.prpa.bancodigital.service;

import com.prpa.bancodigital.exception.ResourceAlreadyExistsException;
import com.prpa.bancodigital.exception.ResourceNotFoundException;
import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.Endereco;
import com.prpa.bancodigital.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNullElse;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> findAll(PageRequest pageRequest) {
        return clienteRepository.findAll(pageRequest).getContent();
    }

    public Cliente findById(long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrado nenhum cliente com o id especificado"));
    }

    public Cliente newCliente(Cliente cliente) {
        throwOnConflicts(cliente);
        return clienteRepository.save(cliente);
    }

    public Cliente changeById(long id, Cliente cliente) {
        if (!clienteRepository.existsById(id))
            throw new ResourceNotFoundException("Não foi encontrado nenhum cliente com o id especificado");
        return patchById(id, cliente);
    }

    public Cliente patchById(long id, Cliente newCliente) {
        Cliente persisted = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrado nenhum cliente com o id especificado"));

        throwOnConflicts(newCliente, persisted);

        persisted.setNome(getOrDefault(newCliente.getNome(), persisted.getNome()));
        persisted.setCpf(getOrDefault(newCliente.getCpf(), persisted.getCpf()));
        persisted.setDataNascimento(requireNonNullElse(newCliente.getDataNascimento(), persisted.getDataNascimento()));

        if (newCliente.getEndereco() == null)
            return clienteRepository.save(persisted);

        Endereco persistedEndereco = persisted.getEndereco();
        persistedEndereco.setCep(getOrDefault(newCliente.getEndereco().getCep(), persistedEndereco.getCep()));
        persistedEndereco.setComplemento(getOrDefault(newCliente.getEndereco().getComplemento(), persistedEndereco.getComplemento()));
        persistedEndereco.setNumero(requireNonNullElse(newCliente.getEndereco().getNumero(), persistedEndereco.getNumero()));
        persistedEndereco.setRua(getOrDefault(newCliente.getEndereco().getRua(), persistedEndereco.getRua()));
        persistedEndereco.setBairro(getOrDefault(newCliente.getEndereco().getBairro(), persistedEndereco.getBairro()));
        persistedEndereco.setCidade(getOrDefault(newCliente.getEndereco().getCidade(), persistedEndereco.getCidade()));
        persistedEndereco.setEstado(getOrDefault(newCliente.getEndereco().getEstado(), persistedEndereco.getEstado()));
        persisted.setEndereco(persistedEndereco);
        return clienteRepository.save(persisted);
    }

    public void deleteById(long id) {
        clienteRepository.deleteById(id);
    }

    private String getOrDefault(String string, String defaultValue) {
        return string == null || string.isBlank() ? defaultValue : string;
    }

    private void throwOnConflicts(Cliente newCliente) {
        throwOnConflicts(newCliente, null);
    }

    private void throwOnConflicts(Cliente newCliente, Cliente persisted) {
        final boolean isPersistedAndNewTheSameByName = persisted != null && persisted.getNome().equals(newCliente.getNome());
        final boolean isPersistedAndNewTheSameByCPF = persisted != null && persisted.getCpf().equals(newCliente.getCpf());
        if (!isPersistedAndNewTheSameByName && clienteRepository.existsByNome(newCliente.getNome()))
            throw new ResourceAlreadyExistsException("Ja existe um cliente com esse nome");
        if (!isPersistedAndNewTheSameByCPF && clienteRepository.existsByCpf(newCliente.getCpf()))
            throw new ResourceAlreadyExistsException("Ja existe um cliente com esse cpf");
    }

}
