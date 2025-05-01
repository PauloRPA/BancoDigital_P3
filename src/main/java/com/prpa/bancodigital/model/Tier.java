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

//    private void removePoliticas() {
//        if (politicaUso != null)
//            politicaUso.getTiers().remove(this);
//        for (PoliticaTaxa politicaTaxa : this.politicasTaxa) {
//            politicaTaxa.getTiers().remove(this);
//        }
//    }

    public Tier(Long id, String nome) {
        this.id = id;
        this.nome = nome.toUpperCase();
        this.politicasTaxa = new HashSet<>();
    }

    public void setNome(String nome) {
        this.nome = nome.toUpperCase();
    }

    public Optional<PoliticaUso> getPoliticaUso() {
        return Optional.ofNullable(politicaUso);
    }

}