package com.prpa.bancodigital.model;

import com.prpa.bancodigital.model.enums.TipoCartao;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CartaoDebito extends Cartao {

    public CartaoDebito() {
        super();
        this.tipo = TipoCartao.CARTAO_DEBITO;
    }

    public CartaoDebito(Long id, BigDecimal limiteDiario) {
        this();
        this.id = id;
        this.limiteDiario = limiteDiario;
    }

    public CartaoDebito(Long id, String numero, LocalDate vencimento, String ccv, String senha, ContaBancaria conta) {
        super(id, numero, vencimento, ccv, senha, conta);
        this.limiteDiario = getPoliticaUso().getLimiteDiario();
        this.tipo = TipoCartao.CARTAO_DEBITO;
    }

}