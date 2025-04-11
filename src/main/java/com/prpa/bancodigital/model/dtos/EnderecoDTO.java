package com.prpa.bancodigital.model.dtos;

import com.prpa.bancodigital.model.Endereco;
import com.prpa.bancodigital.model.validator.annotations.Cep;
import com.prpa.bancodigital.model.validator.groups.PostRequired;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public record EnderecoDTO(
        @NotNull(message = "O campo complemento não pode ser nulo", groups = PostRequired.class)
        String complemento,
        @Cep(message = "O campo de cep possui um formato inválido")
        @NotNull(message = "O campo cep não pode ser vazio", groups = PostRequired.class)
        String cep,
        @Min(message = "O numero deve ser um valor maior que 0", value = 1)
        @NotNull(message = "O campo numero não pode ser vazio", groups = PostRequired.class)
        Integer numero,
        @NotBlank(message = "O campo rua não pode ser vazio", groups = PostRequired.class)
        String rua,
        @NotBlank(message = "O campo bairro não pode ser vazio", groups = PostRequired.class)
        String bairro,
        @NotBlank(message = "O campo cidade não pode ser vazio", groups = PostRequired.class)
        String cidade,
        @NotBlank(message = "O campo estado não pode ser vazio", groups = PostRequired.class)
        String estado) {

    public static EnderecoDTO from(Endereco endereco) {
        return new EnderecoDTO(endereco.getComplemento(),
                endereco.getCep(),
                endereco.getNumero(),
                endereco.getRua(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado());
    }

    public Endereco toEndereco() {
        String cleanCep = cep;
        if (cep.length() == 9)
            cleanCep = cep.replaceFirst("-", "");
        return new Endereco(null, cleanCep, complemento, numero, rua, bairro, cidade, estado);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EnderecoDTO that = (EnderecoDTO) o;
        return Objects.equals(cep, that.cep) && Objects.equals(rua, that.rua) && Objects.equals(bairro, that.bairro) && Objects.equals(cidade, that.cidade) && Objects.equals(estado, that.estado) && Objects.equals(numero, that.numero) && Objects.equals(complemento, that.complemento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(complemento, cep, numero, rua, bairro, cidade, estado);
    }
}