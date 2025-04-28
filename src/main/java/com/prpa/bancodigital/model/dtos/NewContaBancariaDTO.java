package com.prpa.bancodigital.model.dtos;

import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.enums.TipoConta;
import com.prpa.bancodigital.model.validator.annotations.IsEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NewContaBancariaDTO {

    @NotNull(message = "O cliente o qual a conta pertence deve ser informado. Insira ao menos seu CPF ou ID")
    private Cliente cliente;

    @NotBlank(message = "O tipo de conta a ser criada é obrigatório. Use: CONTA_POUPANCA, CONTA_CORRENTE")
    @IsEnum(message = "Este campo deve conter um dos seguintes valores: CONTA_POUPANCA, CONTA_CORRENTE", type = TipoConta.class)
    private String tipo;

    public NewContaBancariaDTO() {
    }

    public NewContaBancariaDTO(Cliente cliente, String tipo) {
        this.cliente = cliente;
        this.tipo = tipo;
    }

}