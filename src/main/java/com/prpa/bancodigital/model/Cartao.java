package com.prpa.bancodigital.model;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Cartao",
        uniqueConstraints = @UniqueConstraint(
                name = "numero_vencimento_ccv_constraint",
                columnNames = {"numero", "vencimento", "ccv"}))
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Cartao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "numero", unique = true, nullable = false)
    protected String numero;

    @Column(name = "vencimento", unique = true, nullable = false)
    protected LocalDate vencimento;

    @Column(name = "ccv", unique = true, nullable = false)
    protected String ccv;

    @JsonIgnore
    @Column(name = "senha")
    protected String senha;

    @JsonIgnore
    @Column(name = "salt")
    protected String salt;

    @Column(name = "limite", scale = 2)
    protected BigDecimal limite;

    @Column(name = "ativo")
    protected Boolean ativo;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "conta_fk", referencedColumnName = "id", nullable = false)
    protected ContaBancaria conta;

    public Cartao() { }

    public Cartao(Long id, String numero, LocalDate vencimento, String ccv, String senha, String salt, BigDecimal limite, Boolean ativo, ContaBancaria conta) {
        this.id = id;
        this.numero = numero;
        this.vencimento = vencimento;
        this.ccv = ccv;
        this.senha = senha;
        this.salt = salt;
        this.limite = limite;
        this.ativo = ativo;
        this.conta = conta;
    }

    public String getNome() {
        return getConta().getCliente().getNome();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public void setVencimento(LocalDate vencimento) {
        this.vencimento = vencimento;
    }

    public String getCcv() {
        return ccv;
    }

    public void setCcv(String ccv) {
        this.ccv = ccv;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public BigDecimal getLimite() {
        return limite;
    }

    public void setLimite(BigDecimal limite) {
        this.limite = limite;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public ContaBancaria getConta() {
        return conta;
    }

    public void setConta(ContaBancaria conta) {
        this.conta = conta;
    }
}
