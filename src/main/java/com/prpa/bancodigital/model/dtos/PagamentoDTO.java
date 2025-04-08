package com.prpa.bancodigital.model.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PagamentoDTO {

    @NotBlank(message = "A descrição do pagamento é obrigatória")
    private String descricao;

    @NotNull(message = "O valor do pagamento é obrigatório")
    @Min(message = "Um pagamento não pode ser menor que 0", value = 0)
    private BigDecimal valor;


}
