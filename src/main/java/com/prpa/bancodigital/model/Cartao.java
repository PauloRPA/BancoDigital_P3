package com.prpa.bancodigital.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prpa.bancodigital.exception.UnauthorizedOperationException;
import com.prpa.bancodigital.model.dtos.PagamentoDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.function.Predicate.not;

@Getter
@Setter
@Entity
@Table(name = "Cartao",
        uniqueConstraints = @UniqueConstraint(
                name = "numero_vencimento_ccv_constraint",
                columnNames = {"numero", "vencimento", "ccv"}))
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Cartao {

    public static final int YEARS_TO_EXPIRE = 4;
    private static final Boolean DEFAULT_INITIAL_STATUS = false;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "numero", unique = true, nullable = false)
    protected String numero;

    @Column(name = "vencimento", nullable = false)
    protected LocalDate vencimento;

    @Column(name = "ccv", nullable = false)
    protected String ccv;

    @JsonIgnore
    @Column(name = "senha")
    protected String senha;

    @Column(name = "ativo")
    protected Boolean ativo;

//   TODO: relação conta
//    @JsonIgnore
//    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//    @JoinColumn(name = "conta_fk", referencedColumnName = "id", nullable = false)
    @Transient
    protected ContaBancaria conta;

    public Cartao() {
        this.ativo = DEFAULT_INITIAL_STATUS;
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

}
