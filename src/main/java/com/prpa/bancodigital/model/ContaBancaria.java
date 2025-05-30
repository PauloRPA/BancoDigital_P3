package com.prpa.bancodigital.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prpa.bancodigital.exception.UnauthorizedOperationException;
import com.prpa.bancodigital.model.dtos.PagamentoDTO;
import com.prpa.bancodigital.model.enums.TipoConta;
import com.prpa.bancodigital.model.enums.TipoTaxa;
import com.prpa.bancodigital.model.enums.TipoTransacao;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.prpa.bancodigital.model.enums.TipoTransacao.*;
import static com.prpa.bancodigital.model.enums.UnidadeTaxa.FIXO;
import static com.prpa.bancodigital.model.enums.UnidadeTaxa.PORCENTAGEM;

@Getter
@Setter
public abstract class ContaBancaria {

    private Long id;
    protected String numero;
    protected String agencia;
    protected BigDecimal saldo;
    protected TipoConta tipo;

    protected Cliente cliente;

    protected List<Cartao> cartoes;

    protected List<PoliticaTaxa> politicas;

    protected ContaBancaria() {
        this.saldo = BigDecimal.ZERO;
        this.politicas = new ArrayList<>();
    }

    protected ContaBancaria(Long id, String numero, String agencia, Cliente cliente) {
        this();
        this.id = id;
        this.numero = numero;
        this.agencia = agencia;
        this.cliente = cliente;
    }

    @JsonIgnore
    public abstract List<PoliticaTaxa> getPoliticas();

    public void addPolitica(PoliticaTaxa politicaTaxa) {
        if (this.politicas == null)
            this.politicas = new ArrayList<>();
        this.politicas.add(politicaTaxa);
    }

    public void addCartao(Cartao cartao) {
        this.cartoes.add(cartao);
    }

    public Transacao applyPoliticaTaxa(PoliticaTaxa taxa) {
        BigDecimal fee = calculateFeeValue(taxa);
        TipoTransacao transactionType = taxa.getTipoTaxa().equals(TipoTaxa.RENDIMENTO) ? RENDIMENTO : TAXA;
        return processTransaction(new Transacao(taxa.getNome(), fee, transactionType));
    }

    public Transacao pay(PagamentoDTO payment) {
        String name = "Pagamento: %s - %s"
                .formatted(payment.getInstituicao(), payment.getDescricao());
        return processTransaction(new Transacao(name, payment.getValor(), COMPRA));
    }

    public Transacao pay(Transacao transacao) {
        return processTransaction(transacao);
    }

    public Transacao deposit(BigDecimal amount) {
        String name = "Deposito de %s efetuado na conta de numero: %s e agencia: %s"
                .formatted(amount, getNumero(), getAgencia());
        return processTransaction(new Transacao(name, amount, DEPOSITO));
    }

    public Transacao withdraw(BigDecimal value) {
        String name = "Saque no valor de %s para conta numero: %s e agencia: %s"
                .formatted(value, getNumero(), getAgencia());
        return processTransaction(new Transacao(name, value, SAQUE));
    }

    private BigDecimal calculateFeeValue(PoliticaTaxa taxa) {
        BigDecimal quantia = taxa.getUnidade().equals(PORCENTAGEM) ? saldo.multiply(taxa.getQuantia()) : null;
        return taxa.getUnidade().equals(FIXO) ? taxa.getQuantia() : quantia;
    }

    private Transacao processTransaction(Transacao transacao) {
        if (transacao.getType().isCharge()) {
            return processCharge(transacao);
        }
        return processYield(transacao);
    }

    private Transacao processYield(Transacao transacao) {
        this.saldo = this.saldo.add(transacao.getAmount());
        transacao.approve();
        return transacao;
    }

    private Transacao processCharge(Transacao transacao) {
        if (transacao.getType().equals(TAXA)) {
            this.saldo = this.saldo.subtract(transacao.getAmount());
            transacao.approve();
            return transacao;
        }

        if (this.saldo.compareTo(transacao.getAmount()) >= 0) {
            this.saldo = this.saldo.subtract(transacao.getAmount());
            transacao.approve();
            return transacao;
        }

        transacao.reprove("Saldo insuficiente");
        throw new UnauthorizedOperationException("Saldo insuficiente");
    }

    public Transacao transferir(ContaBancaria alvo, BigDecimal quantia) {
        String originName = "Transferencia no valor de %s enviada para conta numero: %s e agencia: %s"
                .formatted(quantia, alvo.getNumero(), alvo.getAgencia());
        Transacao charge = processTransaction(new Transacao(originName, quantia, TRANSFERENCIA_ORIGEM));

        if (charge.isApproved()) {
            String targetName = "Transferencia no valor de %s recebida".formatted(quantia);
            alvo.processTransaction(new Transacao(targetName, quantia, TRANSFERENCIA_DESTINO));
        }
        return charge;
    }

    public Transacao pix(ContaBancaria alvo, BigDecimal quantia) {
        String originName = "Pix no valor de %s para conta numero: %s e agencia: %s"
                .formatted(quantia, alvo.getNumero(), alvo.getAgencia());
        Transacao charge = processTransaction(new Transacao(originName, quantia, PIX_ORIGEM));

        if (charge.isApproved()) {
            String targetName = "PIX no valor de %s recebido".formatted(quantia);
            alvo.processTransaction(new Transacao(targetName, quantia, PIX_DESTINO));
        }
        return charge;
    }

}