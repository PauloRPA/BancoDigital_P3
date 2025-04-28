package com.prpa.bancodigital.model.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class PagamentoDTO {

    protected String instituicao;

    @NotBlank(message = "A descrição do pagamento é obrigatória")
    protected String descricao;

    @NotBlank(message = "A senha para o pagamento é obrigatória")
    protected String senha;

    @NotNull(message = "O valor do pagamento é obrigatório")
    @Min(message = "Um pagamento não pode ser menor que 0", value = 0)
    protected BigDecimal valor;

    public PagamentoDTO() { }

    public PagamentoDTO(String instituicao, String descricao, String senha, BigDecimal valor) {
        this.instituicao = instituicao;
        this.descricao = descricao;
        this.senha = senha;
        this.valor = valor;
    }

}
