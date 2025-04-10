package com.prpa.bancodigital.controller;

import com.prpa.bancodigital.model.*;
import com.prpa.bancodigital.model.dtos.CartaoDTO;
import com.prpa.bancodigital.model.enums.TipoCartao;
import com.prpa.bancodigital.service.CartaoService;
import com.prpa.bancodigital.service.ContaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

import static com.prpa.bancodigital.config.ApplicationConfig.API_V1;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartaoController.class)
public class CartaoControllerTest {

    private static final String CARTAO_MAPPING = API_V1 + "/cartoes";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContaService contaService;

    @MockitoBean
    private CartaoService cartaoService;

    @Test
    @DisplayName("Deve aceitar um novo cartão válido corretamente")
    public void whenPostValidCartaoShouldReturnSuccessfully() throws Exception {
        final long EXPECTED_ID = 1L;
        URI expectedLocation = UriComponentsBuilder.fromPath(CARTAO_MAPPING).path("/{id}").build(EXPECTED_ID);

        CartaoCredito cartaoCredito = new CartaoCredito();
        mockContaParaCartao(cartaoCredito);
        cartaoCredito.setId(EXPECTED_ID);
        cartaoCredito.setNumero("1293843209482");
        cartaoCredito.setVencimento(LocalDate.of(2999, 6, 12));
        cartaoCredito.setCcv("213");
        cartaoCredito.setLimiteCredito(BigDecimal.valueOf(10));
        cartaoCredito.setAtivo(true);

        CartaoDTO cartaoDTO = new CartaoDTO(TipoCartao.CARTAO_CREDITO.name(), "123456", "1234", "2345678", "2345678");

        when(contaService.findByNumeroAndAgencia(any(), any()))
                .thenReturn(Optional.ofNullable(cartaoCredito.getConta()));
        when(cartaoService.newCartao(any(), any()))
                .thenReturn(cartaoCredito);

        URI requestURI = UriComponentsBuilder.fromPath(CARTAO_MAPPING).build().toUri();
        ResultActions resultActions = mockMvc.perform(post(requestURI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartaoDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(expectedLocation.toString())));
        testJsonPathEqualsToCartao(cartaoCredito, resultActions);
    }

    // ------------------------------------
    // SingleField validation test
    // ------------------------------------

    @Test
    @DisplayName("Deve retornar um BAD_REQUEST ao desserializar o campo único nomeado boolean inválido")
    public void whenUserFillSingleFieldInputForBooleanIncorrectlyShouldDeserializeSuccessfully() throws Exception {
        URI requestURI = UriComponentsBuilder.fromPath(CARTAO_MAPPING)
                .path("/{id}/status").buildAndExpand(1L).toUri();

        String[] invalidRequestBody = {
                "{ \"status\":  }",
                "{ \"status\": asdf }",
                "{ \"status\": 123.0 }",
                "{}",
                ""
        };

        for (String invalidContent : invalidRequestBody) {
            mockMvc.perform(put(requestURI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidContent))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    @DisplayName("Deve desserializar o campo único nomeado boolean válido corretamente")
    public void whenUserFillSingleFieldInputForBooleanCorrectlyShouldDeserializeSuccessfully() throws Exception {
        CartaoCredito cartaoCredito = new CartaoCredito();
        mockContaParaCartao(cartaoCredito);

        when(cartaoService.setAtivo(anyLong(), anyBoolean()))
                .thenReturn(cartaoCredito);

        URI requestURI = UriComponentsBuilder.fromPath(CARTAO_MAPPING)
                .path("/{id}/status").buildAndExpand(1L).toUri();

        String[] trueValidRequestBody = {
                "{ \"status\": false }",
                "{ \"status\": \"false\" }",
                "{ \"status\": true }",
                "{ \"status\": \"true\" }",
        };

        for (String requestBody : trueValidRequestBody) {
            mockMvc.perform(put(requestURI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk());
        }

        verify(cartaoService, times(2)).setAtivo(eq(1L), eq(true));
        verify(cartaoService, times(2)).setAtivo(eq(1L), eq(false));
    }

    @Test
    @DisplayName("Deve retornar um BAD_REQUEST ao desserializar o campo único double nomeado inválido")
    public void whenUserFillSingleFieldInputForDoubleIncorrectlyShouldDeserializeSuccessfully() throws Exception {
        URI requestURI = UriComponentsBuilder.fromPath(CARTAO_MAPPING)
                .path("/{id}/limite").buildAndExpand(1L).toUri();

        String[] invalidRequestBody = {
                "{ \"limite\":  }",
                "{ \"limite\": asdf }",
                "{ \"limite\": 123,0 }",
                "{ \"limite\": 123,0 }",
                "{}",
                ""
        };

        for (String invalidContent : invalidRequestBody) {
            mockMvc.perform(put(requestURI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidContent))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    @DisplayName("Deve desserializar o campo único nomeado double válido corretamente")
    public void whenUserFillSingleFieldInputForDoubleCorrectlyShouldDeserializeSuccessfully() throws Exception {
        final double limite = 123.0;
        CartaoCredito cartaoCredito = new CartaoCredito();
        mockContaParaCartao(cartaoCredito);

        when(cartaoService.setLimit(anyLong(), anyDouble()))
                .thenReturn(cartaoCredito);

        URI requestURI = UriComponentsBuilder.fromPath(CARTAO_MAPPING)
                .path("/{id}/limite").buildAndExpand(1L).toUri();

        mockMvc.perform(put(requestURI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"limite\": " + limite + " }"))
                .andExpect(status().isOk());
        verify(cartaoService).setLimit(eq(1L), eq(limite));
    }


    private static void mockContaParaCartao(CartaoCredito cartaoCredito) {
        ContaCorrente contaCorrenteMock = mock(ContaCorrente.class);
        Tier tierMock = mock(Tier.class);
        Cliente clienteMock = mock(Cliente.class);
        when(clienteMock.getTier()).thenReturn(tierMock);
        when(clienteMock.getNome()).thenReturn("Meu nome");
        when(contaCorrenteMock.getCliente()).thenReturn(clienteMock);
        cartaoCredito.setConta(contaCorrenteMock);
    }

    private void testJsonPathEqualsToCartao(Cartao target, ResultActions resultActions) throws Exception {
        testJsonPathEqualsToCartao("$", target, resultActions);
    }

    private void testJsonPathEqualsToCartao(String jsonTarget, Cartao target, ResultActions onGoingTest) throws Exception {
        onGoingTest.andExpect(jsonPath(jsonTarget).exists())
                .andExpect(jsonPath(jsonTarget + ".id", equalTo(target.getId().intValue())))
                .andExpect(jsonPath(jsonTarget + ".numero", equalTo(target.getNumero())))
                .andExpect(jsonPath(jsonTarget + ".vencimento", equalTo(target.getVencimento().toString())))
                .andExpect(jsonPath(jsonTarget + ".ccv", equalTo(target.getCcv())))
                .andExpect(jsonPath(jsonTarget + ".ativo", equalTo(target.getAtivo())));
    }

}
