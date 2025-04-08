package com.prpa.bancodigital.model.enums;

import com.prpa.bancodigital.model.Cartao;
import com.prpa.bancodigital.model.CartaoCredito;
import com.prpa.bancodigital.model.CartaoDebito;

public enum TipoCartao {

    CARTAO_DEBITO(CartaoDebito.class),
    CARTAO_CREDITO(CartaoCredito.class);

    private final Class<? extends Cartao> cartaoClass;

    TipoCartao(Class<? extends Cartao> cartao) {
        cartaoClass = cartao;
    }

    public Class<? extends Cartao> getCartaoClass() {
        return cartaoClass;
    }
}
