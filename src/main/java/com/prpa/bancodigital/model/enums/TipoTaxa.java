package com.prpa.bancodigital.model.enums;

public enum TipoTaxa {

    MANUTENCAO("Manutenção"),
    RENDIMENTO("Rendimento");

    private final String nome;

    TipoTaxa(String nome) {
        this.nome = nome;
    }

    public static TipoTaxa fromName(String tipoTaxa) {
        for (TipoTaxa value : values()) {
            if (tipoTaxa.equalsIgnoreCase(value.name())) return value;
        }
        return null;
    }

}