package com.prpa.bancodigital.service;

import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.dtos.ClienteDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    public List<Cliente> findAll(PageRequest pageRequest) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public Cliente findById(long id) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public Cliente newCliente(ClienteDTO cliente) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Transactional
    public Cliente changeById(long id, ClienteDTO cliente) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void deleteById(long id) {
        throw new UnsupportedOperationException("Method not implemented");
    }

}
