package com.prpa.bancodigital.service;

import com.prpa.bancodigital.exception.*;
import com.prpa.bancodigital.model.*;
import com.prpa.bancodigital.model.dtos.CartaoDTO;
import com.prpa.bancodigital.model.dtos.PagamentoDTO;
import com.prpa.bancodigital.model.dtos.SenhaDTO;
import com.prpa.bancodigital.model.enums.TipoCartao;
import com.prpa.bancodigital.repository.CartaoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CartaoService {

    private final CartaoRepository cartaoRepository;

    public CartaoService(CartaoRepository cartaoRepository) {
        this.cartaoRepository = cartaoRepository;
    }

    public Cartao findById(long id) {
        return cartaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi possível encontrar um cartão com o id informado"));
    }

    public <T extends Cartao> T findById(long id, Class<? extends T> tipoCartao) {
        return cartaoRepository.findById(id)
                .filter(found -> tipoCartao.isAssignableFrom(found.getClass()))
                .map(tipoCartao::cast)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi possível encontrar um cartão com o ID e do tipo informado"));
    }

    public Transacao pagar(long id, PagamentoDTO pagamentoDTO) {
        Cartao found = findById(id);
        Transacao transacao = found.pay(pagamentoDTO);
        cartaoRepository.save(found);
        return transacao;
    }

    public Optional<Transacao> pagarFatura(long id, Double valor) {
        CartaoCredito byId = findById(id, CartaoCredito.class);
        Optional<Transacao> transacao = byId.pagarFatura(valor);
        cartaoRepository.save(byId);
        return transacao;
    }

    public Cartao setLimit(long id, double limite) {
        if (limite < 1)
            throw new InvalidInputParameterException("O limite deve ser um valor maior que 0");
        CartaoCredito byId = findById(id, CartaoCredito.class);
        byId.setLimiteCredito(BigDecimal.valueOf(limite));
        return cartaoRepository.save(byId);
    }

    public Cartao setAtivo(long id, Boolean status) {
        Cartao byId = findById(id);
        byId.setAtivo(status);
        return cartaoRepository.save(byId);
    }

    public Cartao setSenha(long id, SenhaDTO senhaDTO) {
        Cartao foundById = findById(id);
        if (!foundById.isPasswordCorrect(senhaDTO.getSenhaAtual()))
            throw new UnauthorizedOperationException("A senha inserida no campo senha atual não esta correta");

        foundById.setSenha(senhaDTO.getSenha());
        return cartaoRepository.save(foundById);
    }

    public Cartao setDailyLimit(long id, Double limite) {
        if (limite < 1)
            throw new InvalidInputParameterException("O limite deve ser um valor maior que 0");
        CartaoDebito byId = findById(id, CartaoDebito.class);
        byId.setLimiteDiario(BigDecimal.valueOf(limite));
        return cartaoRepository.save(byId);
    }

    public Cartao newCartao(CartaoDTO cartaoDTO, ContaBancaria conta) {
        Class<? extends Cartao> cartaoClass = TipoCartao.from(cartaoDTO.getTipo())
                .map(TipoCartao::getCartaoClass)
                .orElseThrow(() -> new InvalidInputParameterException("O tipo de cartão é inválido"));

        for (Cartao cartao : cartaoRepository.findByConta(conta)) {
            if (cartao.getClass().equals(cartaoClass)) {
                throw new ResourceAlreadyExistsException("Esta conta ja tem um cartão deste tipo");
            }
        }

        Cartao newCartao;
        try {
            String number = Cartao.generateCardNumber();
            while (cartaoRepository.existsByNumero(number))
                number = Cartao.generateCardNumber();

            newCartao = cartaoClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            newCartao.setSenha(cartaoDTO.getSenha());
            newCartao.setNumero(number);
            newCartao.setCcv(Cartao.generateCCV());
            newCartao.setVencimento(Cartao.generateVencimento());
            newCartao.setConta(conta);
        } catch (Exception e) {
            throw new ServerErrorException("Não foi possível criar um novo cartão");
        }

        return cartaoRepository.save(newCartao);
    }
}
