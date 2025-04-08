package com.prpa.bancodigital.controller;

import com.prpa.bancodigital.config.ApplicationConfig;
import com.prpa.bancodigital.exception.InvalidInputParameterException;
import com.prpa.bancodigital.exception.ValidationException;
import com.prpa.bancodigital.model.Cartao;
import com.prpa.bancodigital.model.CartaoCredito;
import com.prpa.bancodigital.model.Fatura;
import com.prpa.bancodigital.model.Transacao;
import com.prpa.bancodigital.model.dtos.*;
import com.prpa.bancodigital.model.validator.annotations.SingleField;
import com.prpa.bancodigital.service.CartaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

@RestController
@RequestMapping(ApplicationConfig.API_V1 + "/cartoes")
public class CartaoController {

    private final CartaoService cartaoService;

    public CartaoController(CartaoService cartaoService) {
        this.cartaoService = cartaoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cartao> getCartaoById(@PathVariable("id") long id) {
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");

        return ResponseEntity.ok(cartaoService.findById(id));
    }

    @GetMapping("/{id}/fatura")
    public ResponseEntity<Fatura> getFaturas(@PathVariable("id") long id) {
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");

        return ResponseEntity.ok(cartaoService.findById(id, CartaoCredito.class).getFaturaAtual());
    }

    @PutMapping("/{id}/limite")
    public ResponseEntity<Cartao> putLimite(
            @PathVariable("id") long id,
            @RequestBody @SingleField(name = "limite") Double limite
    ) {
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");

        return ResponseEntity.ok(cartaoService.setLimit(id, limite));
    }

    @PutMapping("/{id}/limite-diario")
    public ResponseEntity<Cartao> putLimiteDiario(
            @PathVariable("id") long id,
            @RequestBody @SingleField(name = "limite") Double limite
    ) {
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");

        return ResponseEntity.ok(cartaoService.setDailyLimit(id, limite));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Cartao> putStatus(
            @PathVariable("id") long id,
            @RequestBody @SingleField(name = "status") Boolean status
    ) {
        if (id < 0)
            throw new InvalidInputParameterException("O parâmetro id deve ser maior ou igual a 0");

        return ResponseEntity.ok(cartaoService.setStatus(id, status));
    }

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

    @PostMapping("")
    public ResponseEntity<Cartao> postNovoCartao(@Valid @RequestBody CartaoDTO cartaoDTO, BindingResult result) {
        ValidationException.throwIfHasErros(result);
        Cartao saved = cartaoService.save(cartaoDTO);
        UriComponents location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(saved.getId());
        return ResponseEntity.created(location.toUri()).body(saved);
    }

    @PostMapping("/{id}/pagamento")
    public ResponseEntity<TransacaoDTO> postNovoPagamento(
            @PathVariable("id") long id,
            @Valid @RequestBody PagamentoDTO pagamentoDTO,
            BindingResult result
    ) {
        ValidationException.throwIfHasErros(result);
        return ResponseEntity.ok(TransacaoDTO.from(cartaoService.pagar(id, pagamentoDTO)));
    }

    @PostMapping("/{id}/fatura/pagamento")
    public ResponseEntity<TransacaoDTO> postPagarFatura(
            @PathVariable("id") long id,
            @Valid @RequestBody PagamentoFaturaDTO faturaDTO,
            BindingResult result
    ) {
        ValidationException.throwIfHasErros(result);
        return ResponseEntity.ok(TransacaoDTO.from(cartaoService.pagarFatura(id, faturaDTO)));
    }

}
