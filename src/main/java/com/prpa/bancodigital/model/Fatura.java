package com.prpa.bancodigital.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Fatura {

    private Long id;
    private LocalDate abertura;
    private LocalDate fechamento;
    private BigDecimal valor;
    private Boolean taxaUtilizacao;
    private Boolean pago;

    @JsonIgnore
    private CartaoCredito cartao;

    public Fatura(Long id, CartaoCredito cartao) {
        this.id = id;
        this.abertura = LocalDate.now();
        this.fechamento = LocalDate.now().plusMonths(1);
        this.valor = BigDecimal.ZERO;
        this.pago = false;
        this.taxaUtilizacao = false;
        this.cartao = cartao;
    }

    public void newTransaction(Transacao transacao) {
        setValor(getValor().add(transacao.getAmount()));
    }

    public void pagar(BigDecimal amount) {
        setValor(getValor().subtract(amount));
        if (getValor().compareTo(BigDecimal.ZERO) == 0) {
            setPago(true);
            setFechamento(LocalDate.now());
        }
    }

    public Boolean isPaid() {
        return pago;
    }

}