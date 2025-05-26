package com.prpa.bancodigital.model.dtos;

import com.prpa.bancodigital.model.Transacao;
import com.prpa.bancodigital.model.enums.TipoTransacao;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciaDTO {

    @NotBlank(message = "O numero da conta para a qual deve ser transferido o valor é obrigatório")
    @Pattern(message = "O numero da conta alvo deve apenas ser composto por 6 números no formato xxxx-xx ou xxxxxx", regexp = "\\d{4}-?\\d{2}")
    private String numero;

    @NotBlank(message = "A agencia da conta para a qual deve ser transferido o valor é obrigatório")
    @Pattern(message = "O numero da conta alvo deve apenas ser composto por 6 números no formato xxx-x ou xxxx", regexp = "\\d{3}-?\\d")
    private String agencia;

    @Min(message = "O valor minimo para transferencia é de 1 Real", value = 1)
    @NotNull(message = "Deve, obrigatoriamente, ser especificado o valor a ser transferido")
    private BigDecimal quantia;


    public Transacao toTransacao() {
        String name = "Transferencia no valor de %s".formatted(quantia);
        return new Transacao(name, quantia, TipoTransacao.TRANSFERENCIA_ORIGEM);
    }

}
