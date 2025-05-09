package com.prpa.bancodigital.model.dtos;

import jakarta.validation.constraints.NotBlank;

public record BankUserDTO (

        @NotBlank(message = "O campo de username não pode estar vazio")
        String username,

        @NotBlank(message = "O campo de username não pode estar vazio")
        String password

) {
}
