package com.prpa.bancodigital.model.enums;

import com.prpa.bancodigital.model.ContaBancaria;
import com.prpa.bancodigital.model.ContaCorrente;
import com.prpa.bancodigital.model.ContaPoupanca;

import java.util.Optional;

public enum TipoConta {

    CONTA_CORRENTE(ContaCorrente.class),
    CONTA_POUPANCA(ContaPoupanca.class);

    private final Class<? extends ContaBancaria> contaBancariaClass;

    TipoConta(Class<? extends ContaBancaria> contaClass) {
        contaBancariaClass = contaClass;
    }

    public Class<?> getContaBancariaClass() {
        return contaBancariaClass;
    }

    public static Optional<TipoConta> fromName(String name) {
        for (TipoConta value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

}
