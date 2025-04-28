package com.prpa.bancodigital.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.prpa.bancodigital.exception.UnauthorizedOperationException;
import com.prpa.bancodigital.model.enums.TipoCartao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.prpa.bancodigital.model.PoliticaUso.ILIMITADO;
import static com.prpa.bancodigital.model.PoliticaUso.SEM_POLITICA;

@Getter
@Setter
@Entity
@Table(name = "Cartao_debito")
public class CartaoDebito extends Cartao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "limite_diario", scale = 2)
    protected BigDecimal limiteDiario;
    private TipoCartao tipo;

    public CartaoDebito() {
        super();
        this.tipo = TipoCartao.CARTAO_DEBITO;
    }

    public CartaoDebito(Long id, BigDecimal limiteDiario) {
        this();
        this.id = id;
        this.limiteDiario = limiteDiario;
    }

    public CartaoDebito(Long id, String numero, LocalDate vencimento, String ccv, String senha, BigDecimal limite, Boolean ativo, ContaBancaria conta, BigDecimal limiteDiario) {
        super(id, numero, vencimento, ccv, senha, conta);
        this.limiteDiario = getPoliticaUso().getLimiteDiario();
        this.tipo = TipoCartao.CARTAO_DEBITO;
    }

    public String getLimiteDiarioMaximo() {
        BigDecimal limiteDiarioMax = getPoliticaUso().getLimiteDiario();
        return limiteDiarioMax.equals(PoliticaUso.ILIMITADO) ? "Sem limite" : limiteDiarioMax.toString();
    }

    @JsonIgnore
    public PoliticaUso getPoliticaUso() {
        return getConta().getCliente().getTier().getPoliticaUso().orElse(SEM_POLITICA);
    }

    @JsonProperty("limiteDiario")
    public String jsonGetLimiteDiario() {
        return this.limiteDiario.equals(ILIMITADO) ? "Sem limite" : this.limiteDiario.toString();
    }

    @JsonIgnore
    public BigDecimal getLimiteDiario() {
        return this.limiteDiario;
    }

    public void setLimiteDiario(BigDecimal limiteDiario) {
        BigDecimal limiteDiarioMax = getPoliticaUso().getLimiteDiario();
        if (limiteDiarioMax.equals(ILIMITADO)) {
            this.limiteDiario = limiteDiario;
            return;
        }

        if (limiteDiarioMax.compareTo(limiteDiario) < 0)
            throw new UnauthorizedOperationException("Não é possível definir um limite diário acima do máximo permitido");

        this.limiteDiario = limiteDiario;
    }

    @Override
    public void setConta(ContaBancaria conta) {
        this.conta = conta;
        setLimiteDiario(getPoliticaUso().getLimiteDiario());
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

}