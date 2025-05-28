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
class ContaBancariaTest {

    @Mock
    private Cliente clienteMock;

    @Mock
    private Tier tierMock;

    private ContaCorrente contaCorrente;
    private ContaPoupanca contaPoupanca;

    @BeforeEach
    void setup() {
        when(clienteMock.getTier()).thenReturn(tierMock);
        when(tierMock.getPoliticasTaxa()).thenReturn(Set.of());
        contaCorrente = new ContaCorrente(1L, "1111-11", "111-1", clienteMock);
        contaPoupanca = new ContaPoupanca(2L, "2222-22", "222-2", clienteMock);
    }

    @Test
    @DisplayName("Deve depositar um valor na conta bancaria corretamente")
    void whenDepositValidNumberShouldSucceed() {
        final BigDecimal amount = BigDecimal.valueOf(100);
        contaCorrente.deposit(amount);
        contaPoupanca.deposit(amount);

        assertThat(contaCorrente.getSaldo()).isEqualTo(amount);
        assertThat(contaPoupanca.getSaldo()).isEqualTo(amount);
    }

    @Test
    @DisplayName("Deve sacar um valor na conta bancaria corretamente")
    void whenWithdrawValidNumberShouldSucceed() {
        final BigDecimal amount = BigDecimal.valueOf(100);
        contaCorrente.deposit(amount);
        contaPoupanca.deposit(amount);

        contaCorrente.withdraw(amount);
        contaPoupanca.withdraw(amount);

        assertThat(contaCorrente.getSaldo()).isEqualTo(BigDecimal.ZERO);
        assertThat(contaPoupanca.getSaldo()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve render taxa fixa corretamente")
    void whenYieldFixedFeeShouldSucceed() {
        BigDecimal fee = BigDecimal.valueOf(10);
        PoliticaTaxa politicaTeste = new PoliticaTaxa(1L, "Politica teste", fee, FIXO, RENDIMENTO);

        contaCorrente.applyPoliticaTaxa(politicaTeste);
        contaPoupanca.applyPoliticaTaxa(politicaTeste);

        assertThat(contaCorrente.getSaldo()).isEqualTo(fee);
        assertThat(contaPoupanca.getSaldo()).isEqualTo(fee);
    }

    @Test
    @DisplayName("Deve cobrar taxa fixa corretamente")
    void whenChargeFixedFeeShouldSucceed() {
        final BigDecimal initialBalance = BigDecimal.valueOf(1000);
        BigDecimal fee = BigDecimal.valueOf(10);
        PoliticaTaxa politicaTeste = new PoliticaTaxa(1L, "Politica teste", fee, FIXO, MANUTENCAO);

        contaCorrente.deposit(initialBalance);
        contaPoupanca.deposit(initialBalance);

        contaCorrente.applyPoliticaTaxa(politicaTeste);
        contaPoupanca.applyPoliticaTaxa(politicaTeste);

        assertThat(contaCorrente.getSaldo()).isEqualTo(initialBalance.subtract(fee));
        assertThat(contaPoupanca.getSaldo()).isEqualTo(initialBalance.subtract(fee));
    }

    @Test
    @DisplayName("Deve render taxa relativa ao saldo corretamente")
    void whenYieldPercentageFeeShouldSucceed() {
        final BigDecimal initialBalance = BigDecimal.valueOf(100);
        BigDecimal feePct = BigDecimal.valueOf(0.10);
        PoliticaTaxa politicaTeste = new PoliticaTaxa(1L, "Politica teste", feePct, PORCENTAGEM, RENDIMENTO);

        contaCorrente.deposit(initialBalance);
        contaPoupanca.deposit(initialBalance);

        contaCorrente.applyPoliticaTaxa(politicaTeste);
        contaPoupanca.applyPoliticaTaxa(politicaTeste);

        assertThat(contaCorrente.getSaldo()).isEqualTo(BigDecimal.valueOf(110.0));
        assertThat(contaPoupanca.getSaldo()).isEqualTo(BigDecimal.valueOf(110.0));
    }

    @Test
    @DisplayName("Deve cobrar taxa relativa ao saldo corretamente")
    void whenChargePercentageFeeShouldSucceed() {
        final BigDecimal initialBalance = BigDecimal.valueOf(100);
        BigDecimal feePct = BigDecimal.valueOf(0.10);
        PoliticaTaxa politicaTeste = new PoliticaTaxa(1L, "Politica teste", feePct, PORCENTAGEM, MANUTENCAO);

        contaCorrente.deposit(initialBalance);
        contaPoupanca.deposit(initialBalance);

        contaCorrente.applyPoliticaTaxa(politicaTeste);
        contaPoupanca.applyPoliticaTaxa(politicaTeste);

        assertThat(contaCorrente.getSaldo()).isEqualTo(BigDecimal.valueOf(90.0));
        assertThat(contaPoupanca.getSaldo()).isEqualTo(BigDecimal.valueOf(90.0));
    }

}