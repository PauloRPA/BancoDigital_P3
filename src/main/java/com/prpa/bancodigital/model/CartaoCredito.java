package com.prpa.bancodigital.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.prpa.bancodigital.exception.UnauthorizedOperationException;
import com.prpa.bancodigital.model.dtos.PagamentoDTO;
import com.prpa.bancodigital.model.enums.TipoCartao;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.prpa.bancodigital.config.ApplicationConfig.BANK_NAME;
import static com.prpa.bancodigital.model.PoliticaUso.ILIMITADO;
import static com.prpa.bancodigital.model.PoliticaUso.SEM_POLITICA;
import static com.prpa.bancodigital.model.enums.TipoTransacao.COMPRA;
import static com.prpa.bancodigital.model.enums.TipoTransacao.FATURA;

@Entity
@Table(name = "Cartao_credito")
public class CartaoCredito extends Cartao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "limite_credito", scale = 2)
    protected BigDecimal limiteCredito;

    @JsonIgnore
    @OneToMany(mappedBy = "cartao", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Column(name = "fatura", scale = 2)
    protected List<Fatura> faturas;

    @Column(name = "tipo")
    protected TipoCartao tipo;


    public CartaoCredito() {
        super();
        this.faturas = new ArrayList<>();
        this.faturas.add(new Fatura(null, this));
        this.tipo = TipoCartao.CARTAO_CREDITO;
    }

    public CartaoCredito(Long id) {
        this();
        this.id = id;
    }

    public CartaoCredito(Long id, String numero, LocalDate vencimento, String ccv, String senha, ContaBancaria conta) {
        super(id, numero, vencimento, ccv, senha, conta);
        this.faturas = new ArrayList<>();
        this.faturas.add(new Fatura(null, this));
        this.tipo = TipoCartao.CARTAO_CREDITO;
    }

    public Optional<Transacao> pagarFatura(Double valor) {
        Optional<Fatura> faturaAtual = getFaturaAtual();
        if (faturaAtual.isEmpty())
            return Optional.empty();
        if (faturaAtual.get().getValor().equals(BigDecimal.ZERO))
            throw new UnauthorizedOperationException("Não há valor a ser pago");

        Fatura fatura = faturaAtual.get();
        if (valor.compareTo(fatura.getValor().doubleValue()) > 0) {
            throw new UnauthorizedOperationException("Não é possível pagar um valor maior que o da fatura");
        }

        String name = "[" + BANK_NAME + "] Pagamento da fatura em aberto";
        Transacao transacao = new Transacao(name, BigDecimal.valueOf(valor), FATURA);
        super.pay(transacao);

        if (transacao.isApproved()) {
            fatura.pagar(transacao.getAmount());
        }

        if (fatura.isPaid()) {
            this.faturas.add(new Fatura(null, this));
        }

        return Optional.of(transacao);
    }

    @Override
    public Transacao pay(PagamentoDTO pagamentoDTO) {
        Fatura faturaAtual = getFaturaAtual().orElseThrow(() -> new UnauthorizedOperationException("Não há faturas em aberto"));
        if (pagamentoDTO.getValor().add(faturaAtual.getValor()).compareTo(getLimiteCredito()) > 0)
            throw new UnauthorizedOperationException("Não é possível efetuar o pagamento pois ele excederá seu limite de credito");
        if (!isPasswordCorrect(pagamentoDTO.getSenha()))
            throw new UnauthorizedOperationException("Senha incorreta");
        if (!getAtivo())
            throw new UnauthorizedOperationException("Este cartão esta inativo");

        String name = "Compra " + pagamentoDTO.getInstituicao() + " - " + pagamentoDTO.getDescricao();
        Transacao transacao = new Transacao(name, pagamentoDTO.getValor(), COMPRA);
        transacao.approve();
        if (transacao.isApproved()) {
            getFaturaAtual().ifPresent(fatura -> fatura.newTransaction(transacao));
        }
        return transacao;
    }

    @JsonIgnore
    public Optional<Fatura> getFaturaAtual() {
        if (faturas.isEmpty()) return Optional.empty();
        return Optional.ofNullable(faturas.get(faturas.size() - 1));
    }

    @JsonIgnore
    public PoliticaUso getPoliticaUso() {
        return getConta().getCliente().getTier().getPoliticaUso().orElse(SEM_POLITICA);
    }

    public String getLimiteMaximoCredito() {
        BigDecimal limiteCreditoMax = getPoliticaUso().getLimiteCredito();
        return limiteCreditoMax.equals(ILIMITADO) ? "Sem limite" : limiteCreditoMax.toString();
    }

    public void setLimiteCredito(BigDecimal limiteCredito) {
        BigDecimal limiteCreditoMax = getPoliticaUso().getLimiteCredito();
        if (limiteCreditoMax.equals(ILIMITADO)) {
            this.limiteCredito = limiteCredito;
            return;
        }

        if (limiteCreditoMax.compareTo(limiteCredito) < 0)
            throw new UnauthorizedOperationException("Não é possível definir um limite de credito acima do máximo permitido");

        this.limiteCredito = limiteCredito;
    }


    @JsonProperty("limiteCredito")
    private String jsonGetLimiteCredito() {
        return this.limiteCredito.equals(ILIMITADO) ? "Sem limite" : this.limiteCredito.toString();
    }

    @JsonIgnore
    public BigDecimal getLimiteCredito() {
        return limiteCredito;
    }

    public List<Fatura> getFaturas() {
        return faturas;
    }

    public void setFaturas(List<Fatura> faturas) {
        this.faturas = faturas;
    }

    public TipoCartao getTipo() {
        return tipo;
    }

    public void setTipo(TipoCartao tipo) {
        this.tipo = tipo;
    }

    @Override
    public void setConta(ContaBancaria conta) {
        this.conta = conta;
        setLimiteCredito(getPoliticaUso().getLimiteCredito());
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