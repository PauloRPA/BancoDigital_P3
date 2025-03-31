package com.prpa.bancodigital.model.dtos;

import com.prpa.bancodigital.model.Tier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class TierDTO {

    @NotBlank(message = "O campo de nome para um tier não pode ser vazio")
    @Pattern(message = "Apenas são validas letras como nome para os tiers",regexp = "[A-Za-z]*")
    private String nome;

    public TierDTO() { }

    public TierDTO(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Tier toTier() {
        return new Tier(null, this.getNome().toUpperCase());
    }
}
