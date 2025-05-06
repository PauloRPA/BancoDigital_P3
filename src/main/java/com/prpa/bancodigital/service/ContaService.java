package com.prpa.bancodigital.service;

import com.prpa.bancodigital.exception.InvalidInputParameterException;
import com.prpa.bancodigital.exception.ResourceAlreadyExistsException;
import com.prpa.bancodigital.exception.ResourceNotFoundException;
import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.ContaBancaria;
import com.prpa.bancodigital.model.PoliticaTaxa;
import com.prpa.bancodigital.model.Transacao;
import com.prpa.bancodigital.model.dtos.NewContaBancariaDTO;
import com.prpa.bancodigital.model.dtos.TransferenciaDTO;
import com.prpa.bancodigital.model.enums.TipoConta;
import com.prpa.bancodigital.model.enums.TipoTaxa;
import com.prpa.bancodigital.repository.ContaBancariaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.prpa.bancodigital.model.enums.UnidadeTaxa.FIXO;

@Slf4j
@Service
public class ContaService {

    private final ContaBancariaRepository contaBancariaRepository;

    public ContaService(ContaBancariaRepository contaBancariaRepository) {
        this.contaBancariaRepository = contaBancariaRepository;
    }

    private static PoliticaTaxa yearlyFeeToMonthly(PoliticaTaxa taxa) {
        BigDecimal quantia = taxa.getQuantia().divide(BigDecimal.valueOf(12), 12, RoundingMode.FLOOR);
        quantia = quantia.divide(BigDecimal.valueOf(100), 12, RoundingMode.FLOOR);
        return new PoliticaTaxa(null, taxa.getNome(), quantia, taxa.getUnidade(), taxa.getTipoTaxa());
    }

    public ContaBancaria findById(long id) {
        return contaBancariaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrada nenhuma conta com este id"));
    }

    public Optional<ContaBancaria> findByNumeroAndAgencia(String numero, String agencia) {
        return contaBancariaRepository.findByNumeroAndAgencia(numero, agencia);
    }

    public ContaBancaria newAccount(NewContaBancariaDTO newAccount, Cliente cliente) {
        String accountNumber = generateNumberSequence(6);
        while (contaBancariaRepository.existsByNumero(accountNumber))
            accountNumber = generateNumberSequence(6);

        String accountAgency = generateNumberSequence(4);
        while (contaBancariaRepository.existsByAgencia(accountAgency))
            accountAgency = generateNumberSequence(4);

        ContaBancaria conta;
        TipoConta tipoConta = TipoConta.fromName(newAccount.getTipo())
                .orElseThrow(() -> new InvalidInputParameterException("O tipo de conta é inválida"));
        try {
            conta = (ContaBancaria) tipoConta
                    .getContaBancariaClass()
                    .getDeclaredConstructor(new Class[0])
                    .newInstance(new Object[0]);
            conta.setNumero(accountNumber);
            conta.setAgencia(accountAgency);
            conta.setCliente(cliente);
            conta.setTipo(tipoConta);
        } catch (Exception e) {
            throw new InvalidInputParameterException("Não foi possível criar uma nova conta");
        }

        for (ContaBancaria contaBancaria : contaBancariaRepository.findByCliente(cliente)) {
            if (tipoConta.getContaBancariaClass().equals(contaBancaria.getClass())) {
                throw new ResourceAlreadyExistsException("Este Cliente ja possui uma conta deste tipo");
            }
        }

        return contaBancariaRepository.save(conta);
    }

    private String generateNumberSequence(int size) {
        Random random = new Random();
        return IntStream.generate(() -> random.nextInt(0, 10))
                .limit(size)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }

    public Transacao transferirById(long id, TransferenciaDTO alvo) {
        ContaBancaria fromConta = findById(id);
        ContaBancaria contaAlvo = findByNumeroAndAgencia(alvo.getNumero(), alvo.getAgencia())
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrada nenhuma conta com o numero e agencia informados"));
        if (fromConta.equals(contaAlvo))
            throw new InvalidInputParameterException("Não é possível transferir caso a conta destino seja igual a conta origem");
        Transacao transaction = fromConta.transferir(contaAlvo, alvo.getQuantia());
        contaBancariaRepository.save(contaAlvo);
        contaBancariaRepository.save(fromConta);
        return transaction;
    }

    public Transacao pixById(long id, TransferenciaDTO alvo) {
        ContaBancaria fromConta = findById(id);
        ContaBancaria contaAlvo = findByNumeroAndAgencia(alvo.getNumero(), alvo.getAgencia())
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrada nenhuma conta com o numero e agencia informados"));
        if (fromConta.equals(contaAlvo))
            throw new InvalidInputParameterException("Não é possível transferir caso a conta destino seja igual a conta origem");
        Transacao transaction = fromConta.pix(contaAlvo, alvo.getQuantia());
        contaBancariaRepository.save(contaAlvo);
        contaBancariaRepository.save(fromConta);
        return transaction;
    }

    public Transacao depositById(long id, BigDecimal value) {
        ContaBancaria found = findById(id);
        Transacao transaction = found.deposit(value);
        contaBancariaRepository.save(found);
        return transaction;
    }

    public Transacao withdrawById(long id, BigDecimal value) {
        ContaBancaria found = findById(id);
        Transacao transaction = found.withdraw(value);
        contaBancariaRepository.save(found);
        return transaction;
    }

    public List<Transacao> manutencao(long id) {
        ContaBancaria found = findById(id);
        List<Transacao> transactions = found.getPoliticas().stream()
                .filter(politicaTaxa -> politicaTaxa.getTipoTaxa().equals(TipoTaxa.MANUTENCAO))
                .map(taxa -> taxa.getUnidade().equals(FIXO) ? taxa : yearlyFeeToMonthly(taxa))
                .map(found::applyPoliticaTaxa)
                .toList();
        if (transactions.isEmpty())
            throw new ResourceNotFoundException("Não há politicas de manutenção para essa conta");
        contaBancariaRepository.save(found);
        return transactions;
    }

    public List<Transacao> rendimento(long id) {
        ContaBancaria found = findById(id);
        List<Transacao> transactions = found.getPoliticas().stream()
                .filter(politicaTaxa -> politicaTaxa.getTipoTaxa().equals(TipoTaxa.RENDIMENTO))
                .map(ContaService::yearlyFeeToMonthly)
                .map(found::applyPoliticaTaxa)
                .toList();
        if (transactions.isEmpty())
            throw new ResourceNotFoundException("Não há politicas de rendimento para essa conta");
        contaBancariaRepository.save(found);
        return transactions;
    }

}