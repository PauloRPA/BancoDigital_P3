package com.prpa.bancodigital.model.dtos;

import com.prpa.bancodigital.model.Tier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TierDTO {

    @NotBlank(message = "O campo de nome para um tier não pode ser vazio")
    @Pattern(message = "Apenas são validas letras como nome para os tiers", regexp = "[A-Za-z]*")
    private String nome;

    public TierDTO() { }

    public TierDTO(String nome) {
        this.nome = nome;
    }

    public static TierDTO from(Tier tier) {
        return new TierDTO(tier.getNome().toUpperCase());
    }

    public Tier toTier() {
        return new Tier(null, this.getNome().toUpperCase());
    }
}
