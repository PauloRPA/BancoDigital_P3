package com.prpa.bancodigital.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
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

    @Column(name = "taxa_utilizacao_cobrada")
    private Boolean taxaUtilizacao;

    @Column(name = "paga")
    private Boolean paid;

    //TODO: relação cartao
    //@ManyToOne
    //@JoinColumn(name = "cartao_fk", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    @Transient
    private CartaoCredito cartao;

    public Fatura() {
    }

    public Fatura(Long id, CartaoCredito cartao) {
        this.id = id;
        this.abertura = LocalDate.now();
        this.fechamento = LocalDate.now().plusMonths(1);
        this.valor = BigDecimal.ZERO;
        this.paid = false;
        this.taxaUtilizacao = false;
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

    public Boolean isPaid() {
        return paid;
    }

}