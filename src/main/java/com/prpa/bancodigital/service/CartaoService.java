package com.prpa.bancodigital.service;

import com.prpa.bancodigital.model.Cartao;
import com.prpa.bancodigital.model.CartaoCredito;
import com.prpa.bancodigital.model.Transacao;
import com.prpa.bancodigital.model.dtos.CartaoDTO;
import com.prpa.bancodigital.model.dtos.PagamentoDTO;
import com.prpa.bancodigital.model.dtos.PagamentoFaturaDTO;
import com.prpa.bancodigital.model.dtos.SenhaDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class CartaoService {

    public Cartao findById(long id) {
        throw new UnsupportedOperationException("Unimplemented method");
    }

    public Transacao pagar(long id, @Valid PagamentoDTO pagamentoDTO) {
        throw new UnsupportedOperationException("Unimplemented method");
    }

    public Transacao pagarFatura(long id, @Valid PagamentoFaturaDTO faturaDTO) {
        throw new UnsupportedOperationException("Unimplemented method");
    }

    public Cartao setLimit(long id, double limite) {
        throw new UnsupportedOperationException("Unimplemented method");
    }

    public Cartao setStatus(long id, Boolean status) {
        throw new UnsupportedOperationException("Unimplemented method");
    }

    public Cartao setSenha(long id, @Valid SenhaDTO senhaDTO) {
        throw new UnsupportedOperationException("Unimplemented method");
    }

    public Cartao setDailyLimit(long id, Double limite) {
        throw new UnsupportedOperationException("Unimplemented method");
    }

    public CartaoCredito findById(long id, Class<? extends Cartao> tipoCartao) {
        throw new UnsupportedOperationException("Unimplemented method");
    }

    public Cartao save(CartaoDTO cartaoDTO) {
        throw new UnsupportedOperationException("Unimplemented method");
    }
}
