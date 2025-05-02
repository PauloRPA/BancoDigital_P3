package com.prpa.bancodigital.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tier {

    private Long id;

    private String nome;

    @JsonIgnore
    private PoliticaUso politicaUso;

    @JsonIgnore
    private Set<PoliticaTaxa> politicasTaxa;

    public Tier(Long id, String nome) {
        this.id = id;
        this.nome = nome.toUpperCase();
        this.politicasTaxa = new HashSet<>();
    }

    public void addPoliticaTaxa(PoliticaTaxa politicaTaxa) {
        if (this.politicasTaxa == null)
            this.politicasTaxa = new HashSet<>();
        this.politicasTaxa.add(politicaTaxa);
    }

    public void setNome(String nome) {
        this.nome = nome.toUpperCase();
    }

    public Optional<PoliticaUso> getPoliticaUso() {
        return Optional.ofNullable(politicaUso);
    }

}