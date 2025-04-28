package com.prpa.bancodigital.model.enums;

import lombok.Getter;

@Getter
public enum TipoTransacao {

    PIX_ORIGEM(true, true),
    PIX_DESTINO(false, true),
    TRANSFERENCIA_ORIGEM(true, true),
    TRANSFERENCIA_DESTINO(false, true),
    TAXA(true, false),
    COMPRA(true, false),
    FATURA(true, false),
    SAQUE(true, false),
    DEPOSITO(false, false),
    RENDIMENTO(false, false);

    private final boolean charge;
    private final boolean doubleSided;

    TipoTransacao(boolean isCharge, boolean doubleSided) {
        this.charge = isCharge;
        this.doubleSided = doubleSided;
    }

}
