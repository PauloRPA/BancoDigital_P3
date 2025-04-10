package com.prpa.bancodigital.controller;

import com.prpa.bancodigital.config.ApplicationConfig;
import com.prpa.bancodigital.exception.InvalidInputParameterException;
import com.prpa.bancodigital.exception.ValidationException;
import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.dtos.ClienteDTO;
import com.prpa.bancodigital.model.validator.groups.PostRequired;
import com.prpa.bancodigital.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(ApplicationConfig.API_V1 + "/clientes")
public class ClienteController {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Operation(summary = "Lista todos clientes cadastrados de acordo com a paginação")
    @GetMapping("")
    public ResponseEntity<List<Cliente>> getClientes(
            @RequestParam(defaultValue = "-1") int page,
            @RequestParam(defaultValue = "-1") int size
    ) {
        final int pageNumber = page < 0 ? DEFAULT_PAGE : page;
        final int pageSize = size < 0 || size > MAX_PAGE_SIZE ? DEFAULT_SIZE : size;

        return ResponseEntity.ok(clienteService.findAll(PageRequest.of(pageNumber, pageSize)));
    }

    @Operation(summary = "Retorna o cliente pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable("id") long id) {
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");

        return ResponseEntity.ok(clienteService.findById(id));
    }

    @Operation(summary = "Insere um novo cliente")
    @PostMapping("")
    public ResponseEntity<Cliente> postCliente(@Validated(PostRequired.class) @RequestBody ClienteDTO cliente, BindingResult result) {
        ValidationException.throwIfHasErros(result);
        Cliente saved = clienteService.newCliente(cliente.toCliente());
        UriComponents location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}").buildAndExpand(saved.getId());
        return ResponseEntity.created(location.toUri()).body(saved);
    }

    @Operation(summary = "Edita as informações de um cliente cadastrado que possua o ID especificado")
    @PatchMapping("/{id}")
    public ResponseEntity<Cliente> patchClienteById(@PathVariable("id") long id,
                                                    @Valid @RequestBody ClienteDTO cliente,
                                                    BindingResult result) {
        ValidationException.throwIfHasErros(result);
        return ResponseEntity.ok(clienteService.patchById(id, cliente.toCliente()));
    }

    @Operation(summary = "Altera as informações do cliente com o ID especificado")
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> putClienteById(@PathVariable("id") long id,
                                                  @Validated(PostRequired.class) @RequestBody ClienteDTO cliente,
                                                  BindingResult result) {
        ValidationException.throwIfHasErros(result);
        return ResponseEntity.ok(clienteService.changeById(id, cliente.toCliente()));
    }

    @Operation(summary = "Remove o cliente com o ID especificado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClienteById(@PathVariable("id") long id) {
        clienteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
