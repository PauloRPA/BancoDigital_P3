package com.prpa.bancodigital.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.prpa.bancodigital.exception.UnauthorizedOperationException;
import com.prpa.bancodigital.model.dtos.PagamentoDTO;
import com.prpa.bancodigital.model.enums.TipoCartao;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.prpa.bancodigital.model.PoliticaUso.ILIMITADO;
import static com.prpa.bancodigital.model.PoliticaUso.SEM_POLITICA;
import static java.util.function.Predicate.not;

@Getter
@Setter
public abstract class Cartao {

    public static final int YEARS_TO_EXPIRE = 4;
    private static final Boolean DEFAULT_INITIAL_STATUS = false;
    public static final String SEM_LIMITE = "Sem limite";

    protected Long id;
    protected String numero;
    protected LocalDate vencimento;
    protected String ccv;
    protected Boolean ativo;
    protected TipoCartao tipo;
    protected BigDecimal limiteDiario;
    protected BigDecimal limiteCredito;

    @JsonIgnore
    protected String senha;

    @JsonIgnore
    protected ContaBancaria conta;

    public Cartao() {
        this.ativo = DEFAULT_INITIAL_STATUS;
        this.limiteDiario = ILIMITADO;
        this.limiteCredito = ILIMITADO;
    }

    public Cartao(Long id, String numero, LocalDate vencimento, String ccv, String senha, ContaBancaria conta) {
        this();
        this.id = id;
        this.vencimento = vencimento;
        this.ccv = ccv;
        this.numero = numero;
        this.conta = conta;
        setSenha(senha);
    }

    public static String generateCardNumber() {
        Random random = new Random();
        final int[] numbers = IntStream.generate(() -> random.nextInt(9)).limit(15).toArray();
        final int luhnDigit = findLuhnDigit(numbers);
        return Arrays.stream(numbers).mapToObj(String::valueOf).collect(Collectors.joining()) + luhnDigit;
    }

    public static String generateCCV() {
        Random random = new Random();
        return IntStream.generate(() -> random.nextInt(9))
                .limit(3)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }

    public static LocalDate generateVencimento() {
        return LocalDate.now().plusYears(YEARS_TO_EXPIRE);
    }

    public static int findLuhnDigit(String number) {
        return findLuhnDigit(number
                .substring(0, number.length() - 1)
                .chars().map(ch -> ch - '0').toArray());
    }

    public static int findLuhnDigit(int[] payload) {
        StringBuilder digitsToSum = new StringBuilder();
        final int firstItemModule = (payload.length - 1) % 2;
        int sum = 0;
        for (int i = payload.length - 1; i >= 0; i--) {
            if (i % 2 == firstItemModule) {
                digitsToSum.append(payload[i] * 2);
                continue;
            }
            sum += payload[i];
        }
        sum += digitsToSum.chars().map(ch -> ch - '0').sum();
        return (10 - (sum % 10)) % 10;
    }

    public static boolean isCardNumberValid(String number) {
        if (!isInt(number))
            throw new IllegalArgumentException("Apenas devem ser inseridos números para validação do numero do cartão");

        final int verificationDigit = number.substring(number.length() - 1).charAt(0) - '0';
        return findLuhnDigit(number) == verificationDigit;
    }

    private static boolean isInt(String number) {
        return number.chars()
                .mapToObj(ch -> (char) ch)
                .noneMatch(not(Character::isDigit));
    }

    public boolean isPasswordCorrect(String senha) {
        return senha.equalsIgnoreCase(getSenha());
    }

    public Transacao pay(PagamentoDTO pagamentoDTO) {
        if (!isPasswordCorrect(pagamentoDTO.getSenha()))
            throw new UnauthorizedOperationException("Senha incorreta");
        if (!getAtivo())
            throw new UnauthorizedOperationException("Este cartão esta inativo");

        return getConta().pay(pagamentoDTO);
    }

    public Transacao pay(Transacao transacao) {
        return getConta().pay(transacao);
    }

    public String getNome() {
        return getConta().getCliente().getNome();
    }

    public void setConta(ContaBancaria conta) {
        this.conta = conta;
        final boolean isCurrentCreditLimitGreaterThanMaxLimit = getPoliticaUso().getLimiteCredito().compareTo(getLimiteCredito()) < 0;
        final boolean isMaxCreditLimitUnlimited = getPoliticaUso().getLimiteCredito().equals(ILIMITADO);
        if (!isMaxCreditLimitUnlimited && isCurrentCreditLimitGreaterThanMaxLimit) {
            setLimiteCredito(getPoliticaUso().getLimiteCredito());
        }
        final boolean isCurrentUseLimitGreaterThanMaxLimit = getPoliticaUso().getLimiteDiario().compareTo(getLimiteDiario()) < 0;
        final boolean isMaxUseLimitUnlimited = getPoliticaUso().getLimiteDiario().equals(ILIMITADO);
        if (!isMaxUseLimitUnlimited && isCurrentUseLimitGreaterThanMaxLimit) {
            setLimiteDiario(getPoliticaUso().getLimiteDiario());
        }
    }

    @JsonIgnore
    public PoliticaUso getPoliticaUso() {
        return Optional.ofNullable(getConta())
                .map(ContaBancaria::getCliente)
                .map(Cliente::getTier)
                .flatMap(Tier::getPoliticaUso)
                .orElse(SEM_POLITICA);
    }

    @JsonProperty("limiteCredito")
    private String jsonGetLimiteCredito() {
        return this.limiteCredito.equals(ILIMITADO) ? SEM_LIMITE : this.limiteCredito.toString();
    }

    @JsonProperty("limiteDiario")
    public String jsonGetLimiteDiario() {
        return this.limiteDiario.equals(ILIMITADO) ? SEM_LIMITE : this.limiteDiario.toString();
    }

    @JsonIgnore
    public BigDecimal getLimiteDiario() {
        return this.limiteDiario;
    }

    @JsonIgnore
    public BigDecimal getLimiteCredito() {
        return limiteCredito;
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

    public String getLimiteMaximoCredito() {
        BigDecimal limiteCreditoMax = getPoliticaUso().getLimiteCredito();
        return limiteCreditoMax.equals(ILIMITADO) ? SEM_LIMITE : limiteCreditoMax.toString();
    }

    public String getLimiteDiarioMaximo() {
        BigDecimal limiteDiarioMax = getPoliticaUso().getLimiteDiario();
        return limiteDiarioMax.equals(PoliticaUso.ILIMITADO) ? SEM_LIMITE : limiteDiarioMax.toString();
    }

}