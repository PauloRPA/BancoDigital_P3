package com.prpa.bancodigital.model;

import com.prpa.bancodigital.model.enums.TipoTransacao;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class Transacao {

    private String name;
    private final BigDecimal amount;
    private final TipoTransacao type;

    private final ZonedDateTime createdTimestamp;
    private ZonedDateTime doneTimestamp;
    private Boolean approved;
    private Boolean processed;
    private final List<String> reprovalReasons;

    public Transacao(String name, BigDecimal amount, TipoTransacao type) {
        this.name = name;
        this.amount = amount;
        this.type = type;
        this.createdTimestamp = ZonedDateTime.now();
        this.reprovalReasons = new ArrayList<>();
        this.processed = false;
        this.approved = false;
    }

    public void reprove(List<String> reasons) {
        reasons.forEach(this::reprove);
    }

    public void reprove(String reason) {
        this.reprovalReasons.add(reason);
        reprove();
    }

    public void reprove() {
        if (isProcessed()) return;
        this.processed = true;
        this.approved = false;
    }

    public void approve() {
        if (isProcessed()) return;
        this.processed = true;
        this.approved = true;
        this.doneTimestamp = ZonedDateTime.now();
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

    public TipoTransacao getType() {
        return type;
    }

    public ZonedDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public Boolean isApproved() {
        return approved;
    }

    public Boolean isProcessed() {
        return processed;
    }

    public Optional<ZonedDateTime> getDoneTimestamp() {
        return Optional.ofNullable(doneTimestamp);
    }

    public List<String> getReprovalReasons() {
        return Collections.unmodifiableList(reprovalReasons);
    }

}
