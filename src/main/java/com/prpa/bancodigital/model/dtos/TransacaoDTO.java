package com.prpa.bancodigital.model.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.prpa.bancodigital.model.Transacao;
import com.prpa.bancodigital.model.enums.TipoTransacao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class TransacaoDTO {

    private String name;
    private BigDecimal amount;
    private TipoTransacao type;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private ZonedDateTime createdTimestamp;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private ZonedDateTime doneTimestamp;

    private Boolean approved;
    private Boolean processed;
    private List<String> reprovalReasons;

    public static TransacaoDTO from(Transacao transaction) {
        return new TransacaoDTO(
                transaction.getName(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getCreatedTimestamp(),
                transaction.getDoneTimestamp().orElse(null),
                transaction.isProcessed(),
                transaction.isApproved(),
                transaction.getReprovalReasons());
    }

}
