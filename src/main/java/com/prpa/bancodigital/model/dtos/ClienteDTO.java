package com.prpa.bancodigital.model.dtos;

import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.Endereco;

import java.time.LocalDate;
import java.util.Objects;

public record ClienteDTO (String nome, String cpf, LocalDate dataNascimento, Endereco endereco) {

    public static ClienteDTO from(Cliente cliente) {
        return new ClienteDTO(cliente.getNome(), cliente.getCpf(), cliente.getDataNascimento(), cliente.getEndereco());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClienteDTO that = (ClienteDTO) o;
        return Objects.equals(cpf, that.cpf) && Objects.equals(nome, that.nome)  && Objects.equals(dataNascimento, that.dataNascimento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, cpf, dataNascimento);
    }
}
