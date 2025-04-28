package com.prpa.bancodigital.model.dtos;

import com.prpa.bancodigital.model.validator.annotations.FieldMatch;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@FieldMatch(fieldName = "senha", confirmFieldName = "confirmarSenha", message = "Ambas senhas não conferem")
public class SenhaDTO {

    @NotBlank(message = "O campo com a senha atual é obrigatório")
    private String senhaAtual;

    @NotBlank(message = "O campo de senha não pode estar vazio")
    @Length(message = "A senha deve ter ao menos {min} caracteres", min = 6)
    private String senha;

    @NotBlank(message = "O campo de confirmar senha não pode estar vazio")
    private String confirmarSenha;

    public SenhaDTO() { }

    public SenhaDTO(String senhaAtual, String senha, String confirmarSenha) {
        this.senhaAtual = senhaAtual;
        this.senha = senha;
        this.confirmarSenha = confirmarSenha;
    }

}
