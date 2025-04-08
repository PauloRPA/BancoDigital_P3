package com.prpa.bancodigital.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prpa.bancodigital.exception.ApiException;
import com.prpa.bancodigital.exception.ValidationException;
import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.ContaBancaria;
import com.prpa.bancodigital.model.ContaCorrente;
import com.prpa.bancodigital.model.ContaPoupanca;
import com.prpa.bancodigital.model.dtos.NewContaBancariaDTO;
import com.prpa.bancodigital.model.enums.TipoConta;
import com.prpa.bancodigital.service.ClienteService;
import com.prpa.bancodigital.service.ContaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;
import java.util.Objects;

import static com.prpa.bancodigital.config.ApplicationConfig.API_V1;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContaController.class)
public class ContaControllerTest {

    private static final String CONTA_MAPPING = API_V1 + "/contas";

    @MockitoBean
    private ContaService contaService;

    @MockitoBean
    private ClienteService clienteService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private Cliente clienteMock;

    @Mock
    private ContaCorrente contaCorrente;

    @Mock
    private ContaPoupanca contaPoupanca;

    @BeforeEach
    public void setup() {
    }

    // ------------------------------------------
    // GET
    // ------------------------------------------

    @Test
    @DisplayName("Busca saldo por ID")
    public void whenGETFindSaldoByIdTestShould200OK() throws Exception {
        final int EXPECTED_VALUE = 200;
        final long ID = 1L;

        when(contaCorrente.getSaldo())
                .thenReturn(BigDecimal.valueOf(EXPECTED_VALUE));

        when(contaService.findById(eq(ID)))
                .thenReturn(contaCorrente);

        URI requestURI = UriComponentsBuilder.fromPath(CONTA_MAPPING)
                .path("/{id}").buildAndExpand(ID).toUri();
        mockMvc.perform(get(requestURI)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo", equalTo(EXPECTED_VALUE)));
    }

    // ------------------------------------------
    // POST
    // ------------------------------------------

    @Test
    @DisplayName("Post nova conta valida")
    public void whenPOSTValidContaTestShould201CREATED() throws Exception {
        URI expectedLocation = UriComponentsBuilder.fromPath(CONTA_MAPPING).path("/{id}").build(contaCorrente.getId());

        when(contaCorrente.getId()).thenReturn(0L);
        when(contaCorrente.getNumero()).thenReturn("1111-11");
        when(contaCorrente.getAgencia()).thenReturn("111-1");
        when(contaCorrente.getSaldo()).thenReturn(BigDecimal.valueOf(200));
        when(contaCorrente.getTipo()).thenReturn(TipoConta.CONTA_CORRENTE);

        when(contaService.newAccount(any(), any()))
                .thenReturn(contaCorrente);

        NewContaBancariaDTO newAccount = new NewContaBancariaDTO(clienteMock, TipoConta.CONTA_CORRENTE.name());
        URI requestURI = UriComponentsBuilder.fromPath(CONTA_MAPPING).build().toUri();
        ResultActions resultActions = mockMvc.perform(post(requestURI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAccount)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(expectedLocation.toString())));

        testJsonPathEqualsToConta(contaCorrente, resultActions);
    }

    // ------------------------------------------
    // Caminhos tristes :(
    // ------------------------------------------

    @Test
    @DisplayName("Post nova conta com tipo invalido")
    public void whenPOSTInvalidTipoContaTestShould400BADREQUEST() throws Exception {
        String TIPO_VAZIO = "";
        String TIPO_INEXISTENTE = "CONTA_CORENTE";
        Map<String, String> invalidTypeAndErrorMessage = Map.of(
                TIPO_VAZIO, "O tipo de conta a ser criada é obrigatório. Use: CONTA_POUPANCA, CONTA_CORRENTE",
                TIPO_INEXISTENTE, "Este campo deve conter um dos seguintes valores: CONTA_POUPANCA, CONTA_CORRENTE"
        );

        URI requestURI = UriComponentsBuilder.fromPath(CONTA_MAPPING).build().toUri();
        for (Map.Entry<String, String> entry : invalidTypeAndErrorMessage.entrySet()) {
            String invalidType = entry.getKey(),
                    errorMessage = entry.getValue();

            NewContaBancariaDTO newAccount = new NewContaBancariaDTO(clienteMock, invalidType.isBlank() ? null : invalidType);
            ResultActions resultActions = mockMvc.perform(post(requestURI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newAccount)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.messages.tipo", equalTo(errorMessage)));
            ValidationException validationException = new ValidationException(errorMessage);
            ProblemDetail expectedProblemDetail = problemDetailFromException(requestURI, validationException, ValidationException.DETAIL);
            testJsonPathEqualsToProblemDetail(expectedProblemDetail, resultActions);
        }

        verify(contaService, never()).newAccount(any(), any());
    }

    // ------------------------------------------
    // Utils
    // ------------------------------------------

    private void testJsonPathEqualsToProblemDetail(ProblemDetail expected, ResultActions onGoingTest) throws Exception {
        URI expectedInstance = Objects.requireNonNull(expected.getInstance());
        onGoingTest.andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.status", equalTo(expected.getStatus())))
                .andExpect(jsonPath("$.detail", equalTo(expected.getDetail())))
                .andExpect(jsonPath("$.type", equalTo(expected.getType().toString())))
                .andExpect(jsonPath("$.instance", equalTo(expectedInstance.toString())));
    }

    private void testJsonPathEqualsToConta(ContaBancaria target, ResultActions resultActions) throws Exception {
        testJsonPathEqualsToConta("$", target, resultActions);
    }

    private void testJsonPathEqualsToConta(String jsonTarget, ContaBancaria target, ResultActions onGoingTest) throws Exception {
        onGoingTest.andExpect(jsonPath(jsonTarget).exists())
                .andExpect(jsonPath(jsonTarget + ".id", equalTo(target.getId().intValue())))
                .andExpect(jsonPath(jsonTarget + ".numero", equalTo(target.getNumero())))
                .andExpect(jsonPath(jsonTarget + ".agencia", equalTo(target.getAgencia())))
                .andExpect(jsonPath(jsonTarget + ".saldo", equalTo(target.getSaldo().intValue())))
                .andExpect(jsonPath(jsonTarget + ".tipo", equalTo(target.getTipo().name())));
    }

    private ProblemDetail problemDetailFromException(URI requestURI, ApiException exception, String expectedDetail) {
        ProblemDetail expectedProblemDetail = ProblemDetail.forStatusAndDetail(exception.getStatusCode(), expectedDetail);
        expectedProblemDetail.setType(URI.create(exception.getType()));
        expectedProblemDetail.setInstance(requestURI);
        return expectedProblemDetail;
    }

}
