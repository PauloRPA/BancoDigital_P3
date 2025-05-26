package com.prpa.bancodigital.model;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Cliente {

    private Long id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;

    @EqualsAndHashCode.Exclude
    private Endereco endereco;

    @EqualsAndHashCode.Exclude
    private Tier tier;

}