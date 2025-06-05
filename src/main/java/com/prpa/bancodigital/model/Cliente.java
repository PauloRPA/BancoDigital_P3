package com.prpa.bancodigital.model;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@NoArgsConstructor
@EqualsAndHashCode
public class Cliente {

    private Long id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;

    public Cliente(Long id, String nome, String cpf, LocalDate dataNascimento, Endereco endereco, Tier tier) {
        this.id = id;
        this.nome = nome;
        setCpf(cpf);
        this.dataNascimento = dataNascimento;
        this.endereco = endereco;
        this.tier = tier;
    }

    @EqualsAndHashCode.Exclude
    private Endereco endereco;

    @EqualsAndHashCode.Exclude
    private Tier tier;

    public void setCpf(String cpf) {
        String cleanCPF = cpf;
        if (cpf != null && cpf.length() == 14) {
            cleanCPF = cpf.replace(".", "").replace("-", "");
        }
        this.cpf = cleanCPF;
    }

}