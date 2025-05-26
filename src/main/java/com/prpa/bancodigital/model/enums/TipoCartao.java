package com.prpa.bancodigital.model.enums;

import com.prpa.bancodigital.model.Cartao;
import com.prpa.bancodigital.model.CartaoCredito;
import com.prpa.bancodigital.model.CartaoDebito;
import lombok.Getter;

import java.util.Optional;

@Getter
public enum TipoCartao {

    CARTAO_DEBITO(CartaoDebito.class),
    CARTAO_CREDITO(CartaoCredito.class);

    private final Class<? extends Cartao> cartaoClass;

    TipoCartao(Class<? extends Cartao> cartao) {
        cartaoClass = cartao;
    }

    public static Optional<TipoCartao> from(String tipo) {
        for (TipoCartao value : values()) {
            if (value.name().equalsIgnoreCase(tipo))
                return Optional.of(value);
        }
        return Optional.empty();
    }

    public static Optional<TipoCartao> fromName(String tipo) {
        for (TipoCartao value : values()) {
            if (value.name().equalsIgnoreCase(tipo))
                return Optional.of(value);
        }
        return Optional.empty();
    }

}