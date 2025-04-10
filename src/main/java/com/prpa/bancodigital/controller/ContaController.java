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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Busca uma conta pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta encontrada",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ContaBancaria.class))}),
            @ApiResponse(responseCode = "400", description = "ID Inválido inserido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Conta com esse ID não encontrada", content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<ContaBancaria> getContaById(@PathVariable("id") long id) {
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");

        return ResponseEntity.ok(contaService.findById(id));
    }

    @Operation(summary = "Busca o saldo de uma conta pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta encontrada",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ContaBancaria.class))}),
            @ApiResponse(responseCode = "400", description = "ID Inválido inserido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Conta com esse ID não encontrada", content = @Content)})
    @GetMapping("/{id}/saldo")
    public ResponseEntity<Map<String, BigDecimal>> getSaldoContaById(@PathVariable("id") long id) {
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");

        return ResponseEntity.ok(Map.of("saldo", contaService.findById(id).getSaldo()));
    }

    @Operation(summary = "Lista as policias de taxas e rendimento de uma conta a partir do ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Politicas listadas com sucesso",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PoliticaTaxa.class)))}),
            @ApiResponse(responseCode = "400", description = "ID Inválido inserido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Conta com esse ID não encontrada", content = @Content)})
    @GetMapping("/{id}/politicas")
    public ResponseEntity<List<PoliticaTaxa>> getContaPoliticas(@PathVariable("id") long id) {
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");
        return ResponseEntity.ok().body(contaService.findById(id).getPoliticas());
    }

    @Operation(summary = "Adiciona uma nova conta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conta adicionada",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ContaBancaria.class))}),
            @ApiResponse(responseCode = "400", description = "ID Inválido inserido", content = @Content),
            @ApiResponse(responseCode = "400", description = "Campos inválidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "Já existe uma conta com esses dados", content = @Content)})
    @PostMapping("")
    public ResponseEntity<ContaBancaria> postConta(@Valid @RequestBody NewContaBancariaDTO newAccount, BindingResult result) {
        ValidationException.throwIfHasErros(result);
        Cliente clienteByCpf = clienteService.findByIdOrCpfOrNomeAndDataNascimento(newAccount.getCliente());
        ContaBancaria saved = contaService.newAccount(newAccount, clienteByCpf);

        UriComponents location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(saved.getId());
        return ResponseEntity.created(location.toUri()).body(saved);
    }

    @Operation(summary = "Realiza uma transferencia entre a conta com o ID inserido e a conta alvo inserida no corpo da requisição")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transferencia realizada com sucesso",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransacaoDTO.class))}),
            @ApiResponse(responseCode = "400", description = "ID Inválido inserido", content = @Content),
            @ApiResponse(responseCode = "400", description = "Campos inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Saldo insuficiente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Conta com esse ID não encontrada", content = @Content)})
    @PostMapping("/{id}/transferencia")
    public ResponseEntity<TransacaoDTO> postContaTransferencia(@PathVariable("id") long id, @Valid @RequestBody TransferenciaDTO alvo, BindingResult result) {
        ValidationException.throwIfHasErros(result);
        return ResponseEntity.ok(TransacaoDTO.from(contaService.transferirById(id, alvo)));
    }

    @Operation(summary = "Realiza um PIX entre a conta com o ID inserido e a conta alvo inserida no corpo da requisição")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PIX realizado com sucesso",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransacaoDTO.class))}),
            @ApiResponse(responseCode = "400", description = "ID Inválido inserido", content = @Content),
            @ApiResponse(responseCode = "400", description = "Campos inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Saldo insuficiente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Conta com esse ID não encontrada", content = @Content)})
    @PostMapping("/{id}/pix")
    public ResponseEntity<TransacaoDTO> postContaPix(@PathVariable("id") long id, @RequestBody TransferenciaDTO alvo, BindingResult result) {
        ValidationException.throwIfHasErros(result);
        return ResponseEntity.ok(TransacaoDTO.from(contaService.pixById(id, alvo)));
    }

    @Operation(summary = "Realiza um deposito na conta com o ID inserido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposito realizado com sucesso",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransacaoDTO.class))}),
            @ApiResponse(responseCode = "400", description = "ID Inválido inserido", content = @Content),
            @ApiResponse(responseCode = "400", description = "Campos inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Conta com esse ID não encontrada", content = @Content)})
    @PostMapping("/{id}/deposito")
    public ResponseEntity<TransacaoDTO> postContaDeposito(
            @PathVariable("id") long id,
            @Valid @RequestBody DepositoDTO depositoDTO,
            BindingResult result
    ) {
        ValidationException.throwIfHasErros(result);
        return ResponseEntity.ok(TransacaoDTO.from(contaService.depositById(id, depositoDTO.quantia())));
    }

    @Operation(summary = "Realiza um saque na conta com o ID inserido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saque realizado com sucesso",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransacaoDTO.class))}),
            @ApiResponse(responseCode = "400", description = "ID Inválido inserido", content = @Content),
            @ApiResponse(responseCode = "400", description = "Campos inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Saldo insuficiente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Conta com esse ID não encontrada", content = @Content)})
    @PostMapping("/{id}/saque")
    public ResponseEntity<TransacaoDTO> postContaSaque(
            @PathVariable("id") long id,
            @Valid @RequestBody SaqueDTO saqueDTO,
            BindingResult result) {
        ValidationException.throwIfHasErros(result);
        return ResponseEntity.ok(TransacaoDTO.from(contaService.withdrawById(id, saqueDTO.quantia())));
    }

    @Operation(summary = "Cobra uma taxa de manutenção da conta com o ID especificado de acordo com a politica de taxa vigente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Taxa de manutenção cobrada com sucesso",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransacaoDTO.class))}),
            @ApiResponse(responseCode = "400", description = "ID Inválido inserido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Conta com esse ID não encontrada", content = @Content)})
    @PutMapping("/{id}/manutencao")
    public ResponseEntity<List<TransacaoDTO>> postContaManutencao(@PathVariable("id") long id) {
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");
        return ResponseEntity.ok().body(contaService.manutencao(id).stream().map(TransacaoDTO::from).toList());
    }

    @Operation(summary = "Calcula os rendimentos da conta com o ID especificado de acordo com a politica de taxa vigente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rendimento depositado com sucesso",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TransacaoDTO.class))}),
            @ApiResponse(responseCode = "400", description = "ID Inválido inserido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Conta com esse ID não encontrada", content = @Content)})
    @PutMapping("/{id}/rendimentos")
    public ResponseEntity<List<TransacaoDTO>> postContaRendimentos(@PathVariable("id") long id) {
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");
        return ResponseEntity.ok().body(contaService.rendimento(id).stream().map(TransacaoDTO::from).toList());
    }

}