package com.prpa.bancodigital.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class CartaoTest {

    @Mock
    private ContaBancaria conta;

    private CartaoCredito credito;
    private CartaoDebito debito;

    @BeforeEach
    public void setup() {
        credito = new CartaoCredito();
        debito = new CartaoDebito();
    }

    @Test
    @DisplayName("O cartão deve encontrar o digito de Luhn corretamente")
    public void whenFindLuhnDigitShouldNumberCorrectly() {
        credito.setNumero("17893729974");
        int[] payload = credito.getNumero()
                .substring(0, credito.getNumero().length() - 1)
                .chars().map(ch -> ch - '0').toArray();
        assertThat(Cartao.findLuhnDigit(payload)).isEqualTo(4);
        assertThat(Cartao.isCardNumberValid(credito.getNumero())).isTrue();
    }

    @Test
    @DisplayName("O cartão deve validar o digito de Luhn corretamente")
    public void whenValidateLuhnDigitShouldValidateCorrectly() {
        credito.setNumero("5330517927376254");
        int[] payload = credito.getNumero()
                .substring(0, credito.getNumero().length() - 1)
                .chars().map(ch -> ch - '0').toArray();
        assertThat(Cartao.findLuhnDigit(payload)).isEqualTo(4);
        assertThat(Cartao.isCardNumberValid(credito.getNumero())).isTrue();
    }

    @RepeatedTest(100)
    @DisplayName("O cartão gerar um numero corretamente")
    public void whenCreateCartaoShouldGenerateNumbersCorrectly() {
        credito.setNumero(Cartao.generateCardNumber());
        assertThat(Cartao.isCardNumberValid(credito.getNumero())).isTrue();

        final int sectionSize = 4;
        String sections = IntStream.iterate(0, i -> i + sectionSize)
                .limit(credito.getNumero().length() / sectionSize)
                .mapToObj(i -> credito.getNumero().substring(i, i + sectionSize))
                .collect(Collectors.joining("-"));
        log.info(sections);
    }

    @Test
    @DisplayName("O cartão deve salvar e verificar a senha salva e valida corretamente")
    public void whenSettingPasswordShouldIdentifyCorrectPasswordCorrectly() {
        String password = "senha super secreta";

        credito.setSenha(password);
        log.info(credito.getSenha());
        assertThat(credito.isPasswordCorrect(password)).isTrue();
    }

    @RepeatedTest(100)
    @DisplayName("O cartão deve salvar e verificar a senha salva e invalida corretamente")
    public void whenSettingPasswordShouldIdentifyIncorrectPasswordCorrectly() {
        final int PASSWORD_SIZE = 50;
        String dictionary = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        Random random = new Random();
        String password = IntStream.generate(() -> random.nextInt(0, dictionary.length()))
                .limit(PASSWORD_SIZE)
                .mapToObj(dictionary::charAt)
                .map(String::valueOf)
                .collect(Collectors.joining());

        final int wrongCharIndex = random.nextInt(0, password.length());
        String wrongPassword = password.substring(0, wrongCharIndex) +
                password.substring(wrongCharIndex + 1);

        credito.setSenha(password);
        assertThat(credito.isPasswordCorrect(password)).isTrue();
        assertThat(credito.isPasswordCorrect(wrongPassword)).isFalse();

        log.info(password);
        log.info(wrongPassword);
    }

}