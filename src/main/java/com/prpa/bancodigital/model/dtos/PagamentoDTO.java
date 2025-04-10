package com.prpa.bancodigital.model.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

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

    public String getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
