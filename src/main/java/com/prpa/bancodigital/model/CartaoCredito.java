package com.prpa.bancodigital.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Cartao_credito")
public class CartaoCredito extends Cartao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "limite_credito", scale = 2)
    protected BigDecimal limiteCredito;

    @OneToMany(mappedBy = "cartao")
    @Column(name = "fatura", scale = 2)
    protected List<Fatura> faturas;

    public CartaoCredito() {
        super();
        this.faturas = new ArrayList<>();
    }

    public CartaoCredito(Long id, BigDecimal limiteCredito, Fatura faturas) {
        super();
        this.id = id;
        this.limiteCredito = limiteCredito;
        this.faturas = new ArrayList<>();
    }

    public CartaoCredito(Long id, String numero, LocalDate vencimento, String ccv, String senha, String salt, BigDecimal limite, Boolean ativo, ContaBancaria conta, Long id1, BigDecimal limiteCredito, Fatura faturas) {
        super(id, numero, vencimento, ccv, senha, salt, limite, ativo, conta);
        this.id = id1;
        this.limiteCredito = limiteCredito;
        this.faturas = new ArrayList<>();
    }

    public BigDecimal getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(BigDecimal limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    public Fatura getFaturaAtual() {
        if (faturas.isEmpty()) return null;
        return faturas.get(faturas.size() - 1);
    }

    public void setFaturas(List<Fatura> faturas) {
        this.faturas = faturas;
    }
}