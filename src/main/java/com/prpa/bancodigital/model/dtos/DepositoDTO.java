package com.prpa.bancodigital.model.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositoDTO(
        @NotNull(message = "A quantia a ser depositada é obrigatória")
        @Min(message = "O valor mínimo a ser depositado é 0", value = 0)
        BigDecimal quantia
) { }
