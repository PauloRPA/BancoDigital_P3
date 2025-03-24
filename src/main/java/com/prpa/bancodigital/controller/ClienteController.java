package com.prpa.bancodigital.controller;

import com.prpa.bancodigital.config.ApplicationConfig;
import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.dtos.ClienteDTO;
import com.prpa.bancodigital.service.ClienteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(ApplicationConfig.API_V1 + "/clientes")
public class ClienteController {

    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("")
    public ResponseEntity<List<Cliente>> getClientes(
            @RequestParam(defaultValue = "-1") int page,
            @RequestParam(defaultValue = "-1") int size
    ) {
        final int pageNumber = page < 0 ? DEFAULT_PAGE : page;
        final int pageSize = size < 0 || size > MAX_PAGE_SIZE ? DEFAULT_SIZE : size;

        return ResponseEntity.ok(clienteService.findAll(PageRequest.of(pageNumber, pageSize)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable("id") long id) {
        return ResponseEntity.ok(clienteService.findById(id));
    }

    @PostMapping("")
    public ResponseEntity<Cliente> postCliente(@RequestBody ClienteDTO cliente) {
        Cliente saved = clienteService.newCliente(cliente);
        UriComponents location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}").buildAndExpand(saved.getId());
        return ResponseEntity.created(location.toUri()).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> putClienteById(@PathVariable("id") long id, @RequestBody ClienteDTO cliente) {
        return ResponseEntity.ok(clienteService.changeById(id, cliente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClienteById(@PathVariable("id") long id) {
        clienteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
