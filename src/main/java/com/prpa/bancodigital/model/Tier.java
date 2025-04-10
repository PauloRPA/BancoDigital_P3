package com.prpa.bancodigital.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "Tier")
public class Tier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", unique = true, updatable = false)
    private String nome;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "politica_uso_fk")
    private PoliticaUso politicaUso;

    @JsonIgnore
    @ManyToMany(mappedBy = "tiers")
    private Set<PoliticaTaxa> politicasTaxa;

    @PreRemove
    private void removePoliticas() {
        if (politicaUso != null)
            politicaUso.getTiers().remove(this);
        for (PoliticaTaxa politicaTaxa : this.politicasTaxa) {
            politicaTaxa.getTiers().remove(this);
        }
    }

    public Tier() {
    }

    public Tier(Long id, String nome) {
        this.id = id;
        this.nome = nome.toUpperCase();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome.toUpperCase();
    }

    public Set<PoliticaTaxa> getPoliticasTaxa() {
        return politicasTaxa;
    }

    public void setPoliticasTaxa(Set<PoliticaTaxa> politicasTaxa) {
        this.politicasTaxa = politicasTaxa;
    }

    public Optional<PoliticaUso> getPoliticaUso() {
        return Optional.ofNullable(politicaUso);
    }

    public void setPoliticaUso(PoliticaUso politicaUso) {
        this.politicaUso = politicaUso;
    }
}