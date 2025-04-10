package com.prpa.bancodigital.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Fatura")
public class Fatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "abertura")
    private LocalDate abertura;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "fechamento")
    private LocalDate fechamento;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "paga")
    private Boolean paid;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cartao_fk", referencedColumnName = "id", nullable = false)
    private CartaoCredito cartao;

    public Fatura() {
    }

    public Fatura(Long id, CartaoCredito cartao) {
        this.id = id;
        this.abertura = LocalDate.now();
        this.fechamento = LocalDate.now().plusMonths(1);
        this.valor = BigDecimal.ZERO;
        this.paid = false;
        this.cartao = cartao;
    }

    public void newTransaction(Transacao transacao) {
        setValor(getValor().add(transacao.getAmount()));
    }

    public void pagar(BigDecimal amount) {
        setValor(getValor().subtract(amount));
        if (getValor().compareTo(BigDecimal.ZERO) == 0) {
            setPaid(true);
            setFechamento(LocalDate.now());
        }
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

    public Boolean isPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
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
