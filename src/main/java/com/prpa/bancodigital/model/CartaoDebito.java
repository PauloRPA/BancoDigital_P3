package com.prpa.bancodigital.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Cartao_debito")
public class CartaoDebito extends Cartao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "limite_diario", scale = 2)
    protected BigDecimal limiteDiario;

    public CartaoDebito() {
        super();
    }

    public CartaoDebito(Long id, BigDecimal limiteDiario) {
        super();
        this.id = id;
        this.limiteDiario = limiteDiario;
    }

    public CartaoDebito(Long id, String numero, LocalDate vencimento, String ccv, String senha, String salt, BigDecimal limite, Boolean ativo, ContaBancaria conta, Long id1, BigDecimal limiteDiario) {
        super(id, numero, vencimento, ccv, senha, salt, limite, ativo, conta);
        this.id = id1;
        this.limiteDiario = limiteDiario;
    }

    public BigDecimal getLimiteDiario() {
        return limiteDiario;
    }

    public void setLimiteDiario(BigDecimal limiteDiario) {
        this.limiteDiario = limiteDiario;
    }
}
