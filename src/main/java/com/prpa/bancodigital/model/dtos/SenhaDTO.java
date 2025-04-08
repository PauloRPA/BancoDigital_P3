package com.prpa.bancodigital.model.dtos;

import com.prpa.bancodigital.model.validator.annotations.FieldMatch;
import jakarta.validation.constraints.NotBlank;

@FieldMatch(fieldName = "senha", confirmFieldName = "confirmarSenha", message = "Ambas senhas não conferem")
public class SenhaDTO {

    @NotBlank(message = "O campo com a senha atual é obrigatório")
    private String senhaAtual;

    @NotBlank(message = "O campo de senha não pode estar vazio")
    private String senha;

    @NotBlank(message = "O campo de confirmar senha não pode estar vazio")
    private String confirmarSenha;

}
