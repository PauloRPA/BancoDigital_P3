package com.prpa.bancodigital.model.dtos;

import com.prpa.bancodigital.model.PoliticaTaxa;
import com.prpa.bancodigital.model.enums.TipoTaxa;
import com.prpa.bancodigital.model.enums.UnidadeTaxa;
import com.prpa.bancodigital.model.validator.annotations.IsEnum;
import com.prpa.bancodigital.model.validator.groups.PostRequired;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

public record PoliticaTaxaDTO(

        @NotBlank(message = "O campo nome não pode ser vazio", groups = PostRequired.class)
        String nome,

        @Min(value = 0, message = "O campo quantia não pode ser menor que 1")
        @NotNull(message = "O campo quantia não pode ser vazio", groups = PostRequired.class)
        BigDecimal quantia,

        @IsEnum(message = "Este campo deve conter um dos seguintes valores: FIXO, PORCENTAGEM", type = UnidadeTaxa.class)
        @NotNull(message = "O campo unidade não pode ser vazio", groups = PostRequired.class)
        String unidade,

        @IsEnum(message = "Este campo deve conter um dos seguintes valores: RENDIMENTO, MANUTENCAO", type = TipoTaxa.class)
        @NotNull(message = "O campo tipoTaxa não pode ser vazio", groups = PostRequired.class)
        String tipoTaxa,

        Set<TierDTO> tiers
) {

    public PoliticaTaxa toPoliticaTaxa() {
        PoliticaTaxa politicaTaxa = new PoliticaTaxa(null, nome, quantia, UnidadeTaxa.fromName(unidade), TipoTaxa.fromName(tipoTaxa));
        politicaTaxa.setTiers(tiers.stream().map(TierDTO::toTier).collect(Collectors.toSet()));
        return politicaTaxa;
    }
}