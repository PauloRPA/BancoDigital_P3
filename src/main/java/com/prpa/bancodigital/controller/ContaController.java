package com.prpa.bancodigital.controller;

import com.prpa.bancodigital.config.ApplicationConfig;
import com.prpa.bancodigital.exception.InvalidInputParameterException;
import com.prpa.bancodigital.exception.ValidationException;
import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.ContaBancaria;
import com.prpa.bancodigital.model.PoliticaTaxa;
import com.prpa.bancodigital.model.dtos.*;
import com.prpa.bancodigital.service.ClienteService;
import com.prpa.bancodigital.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(ApplicationConfig.API_V1 + "/contas")
public class ContaController {

    private final ContaService contaService;
    private final ClienteService clienteService;

    public ContaController(ContaService contaService, ClienteService clienteService) {
        this.contaService = contaService;
        this.clienteService = clienteService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaBancaria> getContaById(@PathVariable("id") long id) {
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");

        return ResponseEntity.ok(contaService.findById(id));
    }

    @GetMapping("/{id}/saldo")
    public ResponseEntity<Map<String, BigDecimal>> getSaldoContaById(@PathVariable("id") long id) {
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");

        return ResponseEntity.ok(Map.of("saldo", contaService.findById(id).getSaldo()));
    }

    @GetMapping("/{id}/politicas")
    public ResponseEntity<List<PoliticaTaxa>> getContaPoliticas(@PathVariable("id") long id) {
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");
        return ResponseEntity.ok().body(contaService.findById(id).getPoliticas());
    }


    @PostMapping("")
    public ResponseEntity<ContaBancaria> postConta(@Valid @RequestBody NewContaBancariaDTO newAccount, BindingResult result) {
        ValidationException.throwIfHasErros(result);
        Cliente clienteByCpf = clienteService.findByIdOrCpfOrNomeAndDataNascimento(newAccount.getCliente());
        ContaBancaria saved = contaService.newAccount(newAccount, clienteByCpf);

        UriComponents location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(saved.getId());
        return ResponseEntity.created(location.toUri()).body(saved);
    }

    @PostMapping("/{id}/transferencia")
    public ResponseEntity<TransacaoDTO> postContaTransferencia(@PathVariable("id") long id, @Valid @RequestBody TransferenciaDTO alvo, BindingResult result) {
        ValidationException.throwIfHasErros(result);
        return ResponseEntity.ok(TransacaoDTO.from(contaService.transferirById(id, alvo)));
    }

    @PostMapping("/{id}/pix")
    public ResponseEntity<TransacaoDTO> postContaPix(@PathVariable("id") long id, @RequestBody TransferenciaDTO alvo, BindingResult result) {
        ValidationException.throwIfHasErros(result);
        return ResponseEntity.ok(TransacaoDTO.from(contaService.pixById(id, alvo)));
    }

    @PostMapping("/{id}/deposito")
    public ResponseEntity<TransacaoDTO> postContaDeposito(
            @PathVariable("id") long id,
            @Valid @RequestBody DepositoDTO depositoDTO,
            BindingResult result
    ) {
        ValidationException.throwIfHasErros(result);
        return ResponseEntity.ok(TransacaoDTO.from(contaService.depositById(id, depositoDTO.quantia())));
    }

    @PostMapping("/{id}/saque")
    public ResponseEntity<TransacaoDTO> postContaSaque(
            @PathVariable("id") long id,
            @Valid @RequestBody SaqueDTO saqueDTO,
            BindingResult result) {
        ValidationException.throwIfHasErros(result);
        return ResponseEntity.ok(TransacaoDTO.from(contaService.withdrawById(id, saqueDTO.quantia())));
    }

    @PutMapping("/{id}/manutencao")
    public ResponseEntity<List<TransacaoDTO>> postContaManutencao(@PathVariable("id") long id) {
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");
        return ResponseEntity.ok().body(contaService.manutencao(id).stream().map(TransacaoDTO::from).toList());
    }

    @PutMapping("/{id}/rendimentos")
    public ResponseEntity<List<TransacaoDTO>> postContaRendimentos(@PathVariable("id") long id) {
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");
        return ResponseEntity.ok().body(contaService.rendimento(id).stream().map(TransacaoDTO::from).toList());
    }

}