package com.prpa.bancodigital.controller;

import com.prpa.bancodigital.config.ApplicationConfig;
import com.prpa.bancodigital.exception.InvalidInputParameterException;
import com.prpa.bancodigital.exception.ResourceNotFoundException;
import com.prpa.bancodigital.exception.ValidationException;
import com.prpa.bancodigital.model.Cartao;
import com.prpa.bancodigital.model.CartaoCredito;
import com.prpa.bancodigital.model.ContaBancaria;
import com.prpa.bancodigital.model.Fatura;
import com.prpa.bancodigital.model.dtos.CartaoDTO;
import com.prpa.bancodigital.model.dtos.PagamentoDTO;
import com.prpa.bancodigital.model.dtos.SenhaDTO;
import com.prpa.bancodigital.model.dtos.TransacaoDTO;
import com.prpa.bancodigital.model.validator.annotations.SingleField;
import com.prpa.bancodigital.service.CartaoService;
import com.prpa.bancodigital.service.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.util.List;

@RestController
@RequestMapping(ApplicationConfig.API_V1 + "/cartoes")
public class CartaoController {

    private final CartaoService cartaoService;
    private final ContaService contaService;

    public CartaoController(CartaoService cartaoService, ContaService contaService) {
        this.cartaoService = cartaoService;
        this.contaService = contaService;
    }

    @Operation(summary = "Retorna o cartão a partir de seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão encontrado",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Cartao.class))}),
            @ApiResponse(responseCode = "400", description = "ID Inválido inserido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cartão com esse ID não encontrado", content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<Cartao> getCartaoById(@PathVariable("id") long id) {
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");

        return ResponseEntity.ok(cartaoService.findById(id));
    }

    @Operation(summary = "Retorna a fatura em aberto do cartão com o ID especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fatura encontrado",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Fatura.class))}),
            @ApiResponse(responseCode = "400", description = "ID Inválido inserido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cartão com esse ID não encontrado", content = @Content)})
    @GetMapping("/{id}/fatura")
    public ResponseEntity<Fatura> getFaturas(@PathVariable("id") long id) {
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");

        return cartaoService.findById(id, CartaoCredito.class).getFaturaAtual()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @Operation(summary = "Altera o limite de credito do cartão com o ID especificado")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Novo limite de credito a ser adotado", required = true,
            content = @Content(mediaType = "application/json", schemaProperties = @SchemaProperty(name = "limite"),
                    examples = {@ExampleObject("{ \"limite\": 123 }")}))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Limite alterado com sucesso",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Cartao.class))}),
            @ApiResponse(responseCode = "400", description = "ID Inválido inserido", content = @Content),
            @ApiResponse(responseCode = "400", description = "Campo inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "O limite inserido é maior que o máximo permitido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cartão com esse ID não encontrado", content = @Content)})
    @PutMapping("/{id}/limite")
    public ResponseEntity<Cartao> putLimite(
            @PathVariable("id") long id,
            @RequestBody @SingleField(name = "limite") Double limite
    ) {
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");

        return ResponseEntity.ok(cartaoService.setLimit(id, limite));
    }

    @Operation(summary = "Altera o limite diário do cartão com o ID especificado")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Novo limite diário a ser adotado", required = true,
            content = @Content(mediaType = "application/json", schemaProperties = @SchemaProperty(name = "limite"),
                    examples = {@ExampleObject("{ \"limite\": 123 }")}))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Limite alterado com sucesso",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Cartao.class))}),
            @ApiResponse(responseCode = "400", description = "ID Inválido inserido", content = @Content),
            @ApiResponse(responseCode = "400", description = "Campo inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "O limite inserido é maior que o máximo permitido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cartão com esse ID não encontrado", content = @Content)})
    @PutMapping("/{id}/limite-diario")
    public ResponseEntity<Cartao> putLimiteDiario(
            @PathVariable("id") long id,
            @RequestBody @SingleField(name = "limite") Double limite
    ) {
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");

        return ResponseEntity.ok(cartaoService.setDailyLimit(id, limite));
    }

    @Operation(summary = "Altera o status como ativo ou inativo do cartão com o ID especificado")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Se o cartão deve estar ativo ou inativo (true, false)", required = true,
            content = @Content(mediaType = "application/json", schemaProperties = @SchemaProperty(name = "status"),
                    examples = {@ExampleObject("{ \"status\": true }")}))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status alterado com sucesso",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Cartao.class))}),
            @ApiResponse(responseCode = "400", description = "ID Inválido inserido", content = @Content),
            @ApiResponse(responseCode = "400", description = "Campo inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cartão com esse ID não encontrado", content = @Content)})
    @PutMapping("/{id}/status")
    public ResponseEntity<Cartao> putStatus(
            @PathVariable("id") long id,
            @RequestBody @SingleField(name = "status") Boolean status
    ) {
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");

        return ResponseEntity.ok(cartaoService.setAtivo(id, status));
    }

    @Operation(summary = "Altera a senha do cartão com o ID especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Cartao.class))}),
            @ApiResponse(responseCode = "400", description = "ID Inválido inserido", content = @Content),
            @ApiResponse(responseCode = "400", description = "Campo inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cartão com esse ID não encontrado", content = @Content)})
    @PutMapping("/{id}/senha")
    public ResponseEntity<Cartao> putSenha(
            @PathVariable("id") long id,
            @RequestBody @Valid SenhaDTO senhaDTO,
            BindingResult result
    ) {
        ValidationException.throwIfHasErros(result);
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");

        return ResponseEntity.ok(cartaoService.setSenha(id, senhaDTO));
    }

    @Operation(summary = "Insere um novo cartão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão adicionado com sucesso",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Cartao.class))}),
            @ApiResponse(responseCode = "400", description = "Campo inválido", content = @Content),
            @ApiResponse(responseCode = "409", description = "Essa conta ja possui um cartão desse tipo", content = @Content)})
    @PostMapping("")
    public ResponseEntity<Cartao> postNovoCartao(@Valid @RequestBody CartaoDTO cartaoDTO, BindingResult result) {
        ValidationException.throwIfHasErros(result);
        ContaBancaria conta = contaService.findByNumeroAndAgencia(cartaoDTO.getNumero(), cartaoDTO.getAgencia())
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrada nenhuma conta com o numero e agencia informados"));
        Cartao saved = cartaoService.newCartao(cartaoDTO, conta);
        UriComponents location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(saved.getId());
        return ResponseEntity.created(location.toUri()).body(saved);
    }

    @Operation(summary = "Realiza um pagamento com o cartão especificado pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamento realizado com sucesso",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Cartao.class))}),
            @ApiResponse(responseCode = "400", description = "ID Inválido inserido", content = @Content),
            @ApiResponse(responseCode = "400", description = "Campo inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Saldo insuficiente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cartão com esse ID não encontrado", content = @Content)})
    @PostMapping("/{id}/pagamento")
    public ResponseEntity<TransacaoDTO> postNovoPagamento(
            @PathVariable("id") long id,
            @Valid @RequestBody PagamentoDTO pagamentoDTO,
            BindingResult result
    ) {
        ValidationException.throwIfHasErros(result);
        return ResponseEntity.ok(TransacaoDTO.from(cartaoService.pagar(id, pagamentoDTO)));
    }

    @Operation(summary = "Realiza o pagamento da fatura em aberto do cartão com o ID fornecido")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Valor a ser pago", required = true,
            content = @Content(mediaType = "application/json", schemaProperties = @SchemaProperty(name = "valor"),
                    examples = {@ExampleObject("{ \"valor\": 123 }")}))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamento realizado com sucesso",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Cartao.class))}),
            @ApiResponse(responseCode = "400", description = "ID Inválido inserido", content = @Content),
            @ApiResponse(responseCode = "400", description = "Campo inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Não é possível pagar um valor maior que o requerido pela fatura", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cartão com esse ID não encontrado", content = @Content)})
    @PostMapping("/{id}/fatura/pagamento")
    public ResponseEntity<List<TransacaoDTO>> postPagarFatura(
            @PathVariable("id") long id,
            @RequestBody @SingleField(name = "valor") Double valor
    ) {
        if (valor < 0)
            throw new InvalidInputParameterException("O valor a pagar não pode ser menor que 0");

        List<TransacaoDTO> transacoes = cartaoService.pagarFatura(id, valor).stream()
                .map(TransacaoDTO::from).toList();
        return transacoes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(transacoes);
    }

}