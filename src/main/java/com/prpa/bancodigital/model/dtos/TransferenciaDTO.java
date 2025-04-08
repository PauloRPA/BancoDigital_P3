package com.prpa.bancodigital.model.dtos;

import com.prpa.bancodigital.model.Transacao;
import com.prpa.bancodigital.model.enums.TipoTransacao;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public class TransferenciaDTO {

    @NotBlank(message = "O numero da conta para a qual deve ser transferido o valor é obrigatório")
    @Pattern(message = "O numero da conta alvo deve apenas ser composto por 6 números no formato xxxx-xx ou xxxxxx", regexp = "[0-9]{4}-?[0-9]{2}")
    private String numero;

    @NotBlank(message = "A agencia da conta para a qual deve ser transferido o valor é obrigatório")
    @Pattern(message = "O numero da conta alvo deve apenas ser composto por 6 números no formato xxx-x ou xxxx", regexp = "[0-9]{3}-?[0-9]{1}")
    private String agencia;

    @Min(message = "O valor minimo para transferencia é de 1 Real", value = 1)
    @NotNull(message = "Deve, obrigatoriamente, ser especificado o valor a ser transferido")
    private BigDecimal quantia;


    public Transacao toTransacao() {
        return new Transacao("Transferencia no valor de %s".formatted(quantia), quantia, TipoTransacao.TRANSFERENCIA_ORIGEM);
    }

    public TransferenciaDTO() { }

    public TransferenciaDTO(String numero, String agencia, BigDecimal quantia) {
        this.numero = numero;
        this.agencia = agencia;
        this.quantia = quantia;
    }

    public String getNumero() {
        return numero;
    }

    public String getAgencia() {
        return agencia;
    }

    public BigDecimal getQuantia() {
        return quantia;
    }
}
