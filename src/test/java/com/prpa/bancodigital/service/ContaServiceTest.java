package com.prpa.bancodigital.service;

import com.prpa.bancodigital.exception.InvalidInputParameterException;
import com.prpa.bancodigital.exception.ResourceAlreadyExistsException;
import com.prpa.bancodigital.exception.ResourceNotFoundException;
import com.prpa.bancodigital.model.*;
import com.prpa.bancodigital.model.dtos.NewContaBancariaDTO;
import com.prpa.bancodigital.model.dtos.TransferenciaDTO;
import com.prpa.bancodigital.model.enums.TipoConta;
import com.prpa.bancodigital.model.enums.TipoTaxa;
import com.prpa.bancodigital.model.enums.UnidadeTaxa;
import com.prpa.bancodigital.repository.ContaBancariaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContaServiceTest {

    @Mock
    private Cliente clienteMock;

    @Mock
    private Tier tierMock;

    @Mock
    private ContaCorrente contaCorrenteMock;

    @Mock
    private ContaBancaria contaBancariaMock;

    @Mock
    private NewContaBancariaDTO newContaBancariaDTOMock;

    @Mock
    private ContaBancariaRepository contaBancariaRepository;

    @InjectMocks
    private ContaService contaService;

    // --------------------------------
    // New account test
    // --------------------------------

    @Test
    @DisplayName("Deve criar uma nova conta bancaria com sucesso")
    public void whenNewAccountIsValidShouldReturnNewContaBancaria() {
        when(newContaBancariaDTOMock.getTipo()).thenReturn(TipoConta.CONTA_CORRENTE.name());
        when(contaBancariaRepository.save(any())).thenReturn(contaBancariaMock);

        ContaBancaria newContaBancaria = contaService.newAccount(newContaBancariaDTOMock, clienteMock);
        assertThat(newContaBancaria).isEqualTo(contaBancariaMock);
        verify(contaBancariaRepository, times(1)).existsByNumero(any());
        verify(contaBancariaRepository, times(1)).existsByAgencia(any());
        verify(contaBancariaRepository, times(1)).findByCliente(eq(clienteMock));
    }

    @Test
    @DisplayName("Deve gerar novos números de conta e agencia caso hajam colisões")
    public void whenNumbersCollideShouldSolveCollisions() {
        when(newContaBancariaDTOMock.getTipo()).thenReturn(TipoConta.CONTA_CORRENTE.name());
        when(contaBancariaRepository.save(any())).thenReturn(contaBancariaMock);
        when(contaBancariaRepository.existsByNumero(any())).thenReturn(true, true, true, true, false);
        when(contaBancariaRepository.existsByAgencia(any())).thenReturn(true, true, true, true, false);

        ContaBancaria newContaBancaria = contaService.newAccount(newContaBancariaDTOMock, clienteMock);
        assertThat(newContaBancaria).isEqualTo(contaBancariaMock);
        verify(contaBancariaRepository, times(5)).existsByNumero(any());
        verify(contaBancariaRepository, times(5)).existsByAgencia(any());
        verify(contaBancariaRepository, times(1)).findByCliente(eq(clienteMock));
    }

    @Test
    @DisplayName("Deve lançar uma exceção caso o usuário ja possua uma conta deste tipo")
    public void whenUserAccountTypeAlreadyExistsShouldThrow() {
        when(newContaBancariaDTOMock.getTipo()).thenReturn(TipoConta.CONTA_CORRENTE.name());
        when(contaBancariaRepository.findByCliente(clienteMock)).thenReturn(List.of(contaCorrenteMock));

        assertThatThrownBy(() -> contaService.newAccount(newContaBancariaDTOMock, clienteMock)).isInstanceOf(ResourceAlreadyExistsException.class);

        verify(contaBancariaRepository, times(1)).existsByNumero(any());
        verify(contaBancariaRepository, times(1)).existsByAgencia(any());
    }

    // --------------------------------
    // Rendimento test
    // --------------------------------

    @Test
    @DisplayName("Deve render o de acordo com a taxa vigente")
    public void whenYieldShouldSuccess() {
        PoliticaTaxa politicaTaxa = new PoliticaTaxa(null, "teste", BigDecimal.valueOf(10), UnidadeTaxa.FIXO, TipoTaxa.RENDIMENTO);
        Transacao transacaoMock = mock(Transacao.class);
        when(contaCorrenteMock.getPoliticas()).thenReturn(List.of(politicaTaxa));
        when(contaCorrenteMock.applyPoliticaTaxa(any())).thenReturn(transacaoMock);
        when(contaBancariaRepository.findById(any())).thenReturn(Optional.of(contaCorrenteMock));

        List<Transacao> rendimento = contaService.rendimento(1L);
        assertThat(rendimento).singleElement().isEqualTo(transacaoMock);
    }

    @Test
    @DisplayName("Deve filtrar taxas que não são de rendimento corretamente")
    public void whenYieldShouldFilterFeesSuccessfully() {
        PoliticaTaxa politicaTaxaMock = mock(PoliticaTaxa.class);
        when(politicaTaxaMock.getTipoTaxa()).thenReturn(TipoTaxa.MANUTENCAO);
        when(contaCorrenteMock.getPoliticas()).thenReturn(List.of(politicaTaxaMock));
        when(contaBancariaRepository.findById(any())).thenReturn(Optional.of(contaCorrenteMock));

        assertThatThrownBy(() -> contaService.rendimento(1L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve cobrar manutenção o de acordo com a taxa vigente")
    public void whenChargeFeeShouldSuccess() {
        PoliticaTaxa politicaTaxa = new PoliticaTaxa(null, "teste", BigDecimal.valueOf(10), UnidadeTaxa.FIXO, TipoTaxa.MANUTENCAO);
        Transacao transacaoMock = mock(Transacao.class);
        when(contaCorrenteMock.getPoliticas()).thenReturn(List.of(politicaTaxa));
        when(contaCorrenteMock.applyPoliticaTaxa(any())).thenReturn(transacaoMock);
        when(contaBancariaRepository.findById(any())).thenReturn(Optional.of(contaCorrenteMock));

        List<Transacao> rendimento = contaService.manutencao(1L);
        assertThat(rendimento).singleElement().isEqualTo(transacaoMock);
    }

    @Test
    @DisplayName("Deve filtrar taxas que não são de manutenção corretamente")
    public void whenChargeFeeShouldFilterFeesSuccessfully() {
        PoliticaTaxa politicaTaxaMock = mock(PoliticaTaxa.class);
        when(politicaTaxaMock.getTipoTaxa()).thenReturn(TipoTaxa.RENDIMENTO);
        when(contaCorrenteMock.getPoliticas()).thenReturn(List.of(politicaTaxaMock));
        when(contaBancariaRepository.findById(any())).thenReturn(Optional.of(contaCorrenteMock));

        assertThatThrownBy(() -> contaService.manutencao(1L)).isInstanceOf(ResourceNotFoundException.class);
    }

    // --------------------------------
    // Transferencia test
    // --------------------------------

    @Test
    @DisplayName("Deve transferir os valores corretamente")
    public void whenTransferringMoneyShouldSucceed() {
        final long ID = 1L;
        String NUMERO = "numero2";
        String AGENCIA = "agencia2";
        BigDecimal QUANTIA = BigDecimal.valueOf(200);
        BigDecimal INITAL_BALANCE = BigDecimal.valueOf(1000);

        when(clienteMock.getTier()).thenReturn(tierMock);
        when(tierMock.getPoliticasTaxa()).thenReturn(Set.of());

        ContaCorrente origem = new ContaCorrente(1L, "numero1", "agencia1", clienteMock);
        origem.deposit(INITAL_BALANCE);
        ContaPoupanca alvo = new ContaPoupanca(2L, NUMERO, AGENCIA, clienteMock);

        when(contaBancariaRepository.findById(eq(ID))).thenReturn(Optional.of(origem));
        when(contaBancariaRepository.findByNumeroAndAgencia(eq(NUMERO), eq(AGENCIA)))
                .thenReturn(Optional.of(alvo));

        contaService.transferirById(ID, new TransferenciaDTO(NUMERO, AGENCIA, QUANTIA));
        assertThat(origem.getSaldo()).isEqualTo(INITAL_BALANCE.subtract(QUANTIA));
        assertThat(alvo.getSaldo()).isEqualTo(QUANTIA);
    }

    @Test
    @DisplayName("Não deve permitir a transferencia quando origem e alvo forem iguais")
    public void whenTransferringMoneyBetweenSameAccountShouldThrow() {
        final long ID = 1L;
        String NUMERO = "numero2";
        String AGENCIA = "agencia2";

        when(contaBancariaRepository.findById(eq(ID))).thenReturn(Optional.of(contaCorrenteMock));
        when(contaBancariaRepository.findByNumeroAndAgencia(eq(NUMERO), eq(AGENCIA)))
                .thenReturn(Optional.of(contaCorrenteMock));

        assertThatThrownBy(() -> contaService.transferirById(ID, new TransferenciaDTO(NUMERO, AGENCIA, BigDecimal.valueOf(200))))
                .isInstanceOf(InvalidInputParameterException.class);
    }

    // --------------------------------
    // PIX test
    // --------------------------------

    @Test
    @DisplayName("Deve transferir os valores corretamente")
    public void whenPIXMoneyShouldSucceed() {
        final long ID = 1L;
        String NUMERO = "numero2";
        String AGENCIA = "agencia2";
        BigDecimal QUANTIA = BigDecimal.valueOf(200);
        BigDecimal INITAL_BALANCE = BigDecimal.valueOf(1000);

        when(clienteMock.getTier()).thenReturn(tierMock);
        when(tierMock.getPoliticasTaxa()).thenReturn(Set.of());

        ContaCorrente origem = new ContaCorrente(1L, "numero1", "agencia1", clienteMock);
        origem.deposit(INITAL_BALANCE);
        ContaPoupanca alvo = new ContaPoupanca(2L, NUMERO, AGENCIA, clienteMock);

        when(contaBancariaRepository.findById(eq(ID))).thenReturn(Optional.of(origem));
        when(contaBancariaRepository.findByNumeroAndAgencia(eq(NUMERO), eq(AGENCIA)))
                .thenReturn(Optional.of(alvo));

        contaService.pixById(ID, new TransferenciaDTO(NUMERO, AGENCIA, QUANTIA));
        assertThat(origem.getSaldo()).isEqualTo(INITAL_BALANCE.subtract(QUANTIA));
        assertThat(alvo.getSaldo()).isEqualTo(QUANTIA);
    }

    @Test
    @DisplayName("Não deve permitir a transferencia quando origem e alvo forem iguais")
    public void whenPIXMoneyBetweenSameAccountShouldThrow() {
        final long ID = 1L;
        String NUMERO = "numero2";
        String AGENCIA = "agencia2";

        when(contaBancariaRepository.findById(eq(ID))).thenReturn(Optional.of(contaCorrenteMock));
        when(contaBancariaRepository.findByNumeroAndAgencia(eq(NUMERO), eq(AGENCIA)))
                .thenReturn(Optional.of(contaCorrenteMock));

        assertThatThrownBy(() -> contaService.pixById(ID, new TransferenciaDTO(NUMERO, AGENCIA, BigDecimal.valueOf(200))))
                .isInstanceOf(InvalidInputParameterException.class);
    }
}
