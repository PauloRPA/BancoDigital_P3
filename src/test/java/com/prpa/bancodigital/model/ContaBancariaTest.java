package com.prpa.bancodigital.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Set;

import static com.prpa.bancodigital.model.enums.TipoTaxa.MANUTENCAO;
import static com.prpa.bancodigital.model.enums.TipoTaxa.RENDIMENTO;
import static com.prpa.bancodigital.model.enums.UnidadeTaxa.FIXO;
import static com.prpa.bancodigital.model.enums.UnidadeTaxa.PORCENTAGEM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ContaBancariaTest {

    @Mock
    private Cliente clienteMock;

    @Mock
    private Tier tierMock;

    private ContaCorrente contaCorrente;
    private ContaPoupanca contaPoupanca;

    @BeforeEach
    public void setup() {
        when(clienteMock.getTier()).thenReturn(tierMock);
        when(tierMock.getPoliticasTaxa()).thenReturn(Set.of());
        contaCorrente = new ContaCorrente(1L, "1111-11", "111-1", clienteMock);
        contaPoupanca = new ContaPoupanca(2L, "2222-22", "222-2", clienteMock);
    }

    @Test
    @DisplayName("Deve depositar um valor na conta bancaria corretamente")
    public void whenDepositValidNumberShouldSucceed() {
        final BigDecimal AMOUNT = BigDecimal.valueOf(100);
        contaCorrente.deposit(AMOUNT);
        contaPoupanca.deposit(AMOUNT);

        assertThat(contaCorrente.getSaldo()).isEqualTo(AMOUNT);
        assertThat(contaPoupanca.getSaldo()).isEqualTo(AMOUNT);
    }

    @Test
    @DisplayName("Deve sacar um valor na conta bancaria corretamente")
    public void whenWithdrawValidNumberShouldSucceed() {
        final BigDecimal AMOUNT = BigDecimal.valueOf(100);
        contaCorrente.deposit(AMOUNT);
        contaPoupanca.deposit(AMOUNT);

        contaCorrente.withdraw(AMOUNT);
        contaPoupanca.withdraw(AMOUNT);

        assertThat(contaCorrente.getSaldo()).isEqualTo(BigDecimal.ZERO);
        assertThat(contaPoupanca.getSaldo()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve render taxa fixa corretamente")
    public void whenYieldFixedFeeShouldSucceed() {
        BigDecimal FEE = BigDecimal.valueOf(10);
        PoliticaTaxa politicaTeste = new PoliticaTaxa(1L, "Politica teste", FEE, FIXO, RENDIMENTO);

        contaCorrente.applyPoliticaTaxa(politicaTeste);
        contaPoupanca.applyPoliticaTaxa(politicaTeste);

        assertThat(contaCorrente.getSaldo()).isEqualTo(FEE);
        assertThat(contaPoupanca.getSaldo()).isEqualTo(FEE);
    }

    @Test
    @DisplayName("Deve cobrar taxa fixa corretamente")
    public void whenChargeFixedFeeShouldSucceed() {
        final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(1000);
        BigDecimal FEE = BigDecimal.valueOf(10);
        PoliticaTaxa politicaTeste = new PoliticaTaxa(1L, "Politica teste", FEE, FIXO, MANUTENCAO);

        contaCorrente.deposit(INITIAL_BALANCE);
        contaPoupanca.deposit(INITIAL_BALANCE);

        contaCorrente.applyPoliticaTaxa(politicaTeste);
        contaPoupanca.applyPoliticaTaxa(politicaTeste);

        assertThat(contaCorrente.getSaldo()).isEqualTo(INITIAL_BALANCE.subtract(FEE));
        assertThat(contaPoupanca.getSaldo()).isEqualTo(INITIAL_BALANCE.subtract(FEE));
    }

    @Test
    @DisplayName("Deve render taxa relativa ao saldo corretamente")
    public void whenYieldPercentageFeeShouldSucceed() {
        final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(100);
        BigDecimal FEE_PCT = BigDecimal.valueOf(0.10);
        PoliticaTaxa politicaTeste = new PoliticaTaxa(1L, "Politica teste", FEE_PCT, PORCENTAGEM, RENDIMENTO);

        contaCorrente.deposit(INITIAL_BALANCE);
        contaPoupanca.deposit(INITIAL_BALANCE);

        contaCorrente.applyPoliticaTaxa(politicaTeste);
        contaPoupanca.applyPoliticaTaxa(politicaTeste);

        assertThat(contaCorrente.getSaldo()).isEqualTo(BigDecimal.valueOf(110.0));
        assertThat(contaPoupanca.getSaldo()).isEqualTo(BigDecimal.valueOf(110.0));
    }

    @Test
    @DisplayName("Deve cobrar taxa relativa ao saldo corretamente")
    public void whenChargePercentageFeeShouldSucceed() {
        final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(100);
        BigDecimal FEE_PCT = BigDecimal.valueOf(0.10);
        PoliticaTaxa politicaTeste = new PoliticaTaxa(1L, "Politica teste", FEE_PCT, PORCENTAGEM, MANUTENCAO);

        contaCorrente.deposit(INITIAL_BALANCE);
        contaPoupanca.deposit(INITIAL_BALANCE);

        contaCorrente.applyPoliticaTaxa(politicaTeste);
        contaPoupanca.applyPoliticaTaxa(politicaTeste);

        assertThat(contaCorrente.getSaldo()).isEqualTo(BigDecimal.valueOf(90.0));
        assertThat(contaPoupanca.getSaldo()).isEqualTo(BigDecimal.valueOf(90.0));
    }

}