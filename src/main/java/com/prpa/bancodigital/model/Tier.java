package com.prpa.bancodigital.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Tier")
public class Tier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", unique = true, updatable = false)
    private String nome;

    public Tier() { }

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
}
