package com.prpa.bancodigital.model;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Endereco {

    private Long id;
    private String cep;
    private String complemento;
    private Integer numero;
    private String rua;
    private String bairro;
    private String cidade;
    private String estado;

}