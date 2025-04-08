package com.prpa.bancodigital.model.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SaqueDTO(
        @NotNull(message = "A quantia a ser sacada é obrigatória")
        @Min(message = "O valor mínimo a ser sacado é 0", value = 0)
        BigDecimal quantia
) { }
