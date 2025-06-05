package com.prpa.bancodigital.model.dtos;

import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.Endereco;
import com.prpa.bancodigital.model.Tier;
import com.prpa.bancodigital.model.validator.annotations.ElapsedTimeInYears;
import com.prpa.bancodigital.model.validator.groups.PostRequired;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.util.Objects;

public record ClienteDTO(
        @NotBlank(message = "O nome não pode estar vazio", groups = PostRequired.class)
        @Pattern(message = "O nome deve ter apenas letras e espaços", regexp = "[a-zA-Z ]*")
        @Length(message = "O nome deve ter de 2 a 100 caracteres", min = 2, max = 100)
        String nome,

        @CPF(message = "O CPF informado é inválido")
        @NotBlank(message = "O campo CPF não pode estar vazio", groups = PostRequired.class)
        String cpf,

        @NotNull(message = "A data de nascimento não pode estar vazia", groups = PostRequired.class)
        @ElapsedTimeInYears(greater = 18, message = "O usuário deve ao menos ter 18 anos de idade")
        LocalDate dataNascimento,

        @Valid
        @NotNull(message = "O endereço não pode estar vazio", groups = PostRequired.class)
        EnderecoDTO endereco,

        @Valid
        @NotNull(message = "O tier não pode estar vazio", groups = PostRequired.class)
        TierDTO tier) {

    public static ClienteDTO from(Cliente cliente) {
        EnderecoDTO enderecoDTO = EnderecoDTO.from(cliente.getEndereco());
        TierDTO tierDTO = TierDTO.from(cliente.getTier());
        return new ClienteDTO(cliente.getNome(), cliente.getCpf(), cliente.getDataNascimento(), enderecoDTO, tierDTO);
    }

    public Cliente toCliente() {
        String cleanCPF = cpf;
        if (cpf != null && cpf.length() == 14) {
            cleanCPF = cpf.replace(".", "").replace("-", "");
        }
        Endereco enderecoCliente = endereco == null ? null : endereco.toEndereco();
        Tier tierCliente = tier.toTier();
        return new Cliente(null, nome, cleanCPF, dataNascimento, enderecoCliente, tierCliente);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClienteDTO that = (ClienteDTO) o;
        return Objects.equals(cpf, that.cpf) && Objects.equals(nome, that.nome) && Objects.equals(endereco, that.endereco) && Objects.equals(dataNascimento, that.dataNascimento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, cpf, dataNascimento, endereco);
    }
}
