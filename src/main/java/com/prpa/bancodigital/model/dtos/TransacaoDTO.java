package com.prpa.bancodigital.model.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.prpa.bancodigital.model.Transacao;
import com.prpa.bancodigital.model.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public class TransacaoDTO {

    private String name;
    private BigDecimal amount;
    private TipoTransacao type;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private ZonedDateTime createdTimestamp;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private ZonedDateTime doneTimestamp;

    private Boolean approved;
    private List<String> reprovalReasons;

    public TransacaoDTO(String name, BigDecimal amount, TipoTransacao type, ZonedDateTime createdTimestamp, ZonedDateTime doneTimestamp, Boolean approved, List<String> reprovalReasons) {
        this.name = name;
        this.amount = amount;
        this.type = type;
        this.createdTimestamp = createdTimestamp;
        this.doneTimestamp = doneTimestamp;
        this.approved = approved;
        this.reprovalReasons = reprovalReasons;
    }

    public static TransacaoDTO from(Transacao transaction) {
        return new TransacaoDTO(transaction.getName(), transaction.getAmount(), transaction.getType(), transaction.getCreatedTimestamp(), transaction.getDoneTimestamp().orElse(null), transaction.isApproved(), transaction.getReprovalReasons());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TipoTransacao getType() {
        return type;
    }

    public void setType(TipoTransacao type) {
        this.type = type;
    }

    public ZonedDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(ZonedDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public ZonedDateTime getDoneTimestamp() {
        return doneTimestamp;
    }

    public void setDoneTimestamp(ZonedDateTime doneTimestamp) {
        this.doneTimestamp = doneTimestamp;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public List<String> getReprovalReasons() {
        return reprovalReasons;
    }

    public void setReprovalReasons(List<String> reprovalReasons) {
        this.reprovalReasons = reprovalReasons;
    }
}
