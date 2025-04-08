package com.prpa.bancodigital.model.enums;

public enum UnidadeTaxa {

    PORCENTAGEM,
    FIXO;

    public static UnidadeTaxa fromName(String unidade) {
        for (UnidadeTaxa value : values()) {
            if (unidade.equalsIgnoreCase(value.name())) return value;
        }
        return null;
    }

}