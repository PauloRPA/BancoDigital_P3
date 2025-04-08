package com.prpa.bancodigital.service;

import com.prpa.bancodigital.exception.ResourceAlreadyExistsException;
import com.prpa.bancodigital.exception.ResourceNotFoundException;
import com.prpa.bancodigital.exception.ValidationException;
import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.Endereco;
import com.prpa.bancodigital.model.Tier;
import com.prpa.bancodigital.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNullElse;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final TierService tierService;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository,
                          TierService tierService) {
        this.clienteRepository = clienteRepository;
        this.tierService = tierService;
    }

    public List<Cliente> findAll(PageRequest pageRequest) {
        return clienteRepository.findAll(pageRequest).getContent();
    }

    public Cliente findById(long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrado nenhum cliente com o id especificado"));
    }

    public Cliente findByIdOrCpfOrNomeAndDataNascimento(Cliente cliente) {
        return Optional.ofNullable(cliente.getId()).flatMap(clienteRepository::findById)
                .or(() -> Optional.ofNullable(cliente.getCpf()).flatMap(clienteRepository::findByCpf))
                .or(() -> Optional.ofNullable(cliente.getNome())
                        .filter(nome -> Objects.nonNull(cliente.getDataNascimento()))
                        .flatMap(nome -> clienteRepository.findByNomeAndDataNascimento(nome, cliente.getDataNascimento())))
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum cliente com os dados fornecidos foi encontrado"));
    }

    public Cliente newCliente(Cliente cliente) {
        throwOnConflicts(cliente);
        if (cliente.getTier() == null)
            throw new ValidationException("O tier não pode ser nulo");

        Tier clienteTier = cliente.getTier();
        Tier tierFound = Optional.of(clienteTier)
                .flatMap((t) -> t.getId() != null ? tierService.findById(t.getId()) : Optional.empty())
                .or(() -> clienteTier.getNome() != null ? tierService.findByNomeIgnoreCase(clienteTier.getNome()) : Optional.empty())
                .orElseThrow(() -> new ResourceNotFoundException("O Tier especificado não foi encontrado"));
        cliente.setTier(tierFound);

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

        Tier tier = Optional.ofNullable(newCliente.getTier())
                .flatMap(t -> t.getNome() != null ? tierService.findByNomeIgnoreCase(t.getNome()) : Optional.empty())
                .orElseThrow(() -> new ResourceNotFoundException("O Tier especificado não foi encontrado"));

        persisted.setNome(getOrDefault(newCliente.getNome(), persisted.getNome()));
        persisted.setCpf(getOrDefault(newCliente.getCpf(), persisted.getCpf()));
        persisted.setDataNascimento(requireNonNullElse(newCliente.getDataNascimento(), persisted.getDataNascimento()));
        persisted.setTier(requireNonNullElse(tier, persisted.getTier()));

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
