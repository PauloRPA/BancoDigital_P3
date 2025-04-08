package com.prpa.bancodigital.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Fatura")
public class Fatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "abertura")
    private LocalDate abertura;

    @Column(name = "fechamento")
    private LocalDate fechamento;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "paga")
    private Boolean paga;

    @ManyToOne
    @JoinColumn(name = "cartao_fk", referencedColumnName = "id", nullable = false)
    private CartaoCredito cartao;

    public Fatura() {
    }

    public Fatura(Long id, LocalDate abertura, LocalDate fechamento, BigDecimal valor, Boolean paga, CartaoCredito cartao) {
        this.id = id;
        this.abertura = abertura;
        this.fechamento = fechamento;
        this.valor = valor;
        this.paga = paga;
        this.cartao = cartao;
    }

    public LocalDate getAbertura() {
        return abertura;
    }

    public void setAbertura(LocalDate abertura) {
        this.abertura = abertura;
    }

    public LocalDate getFechamento() {
        return fechamento;
    }

    public void setFechamento(LocalDate fechamento) {
        this.fechamento = fechamento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean getPaga() {
        return paga;
    }

    public void setPaga(Boolean paga) {
        this.paga = paga;
    }

    public CartaoCredito getCartao() {
        return cartao;
    }

    public void setCartao(CartaoCredito cartao) {
        this.cartao = cartao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
