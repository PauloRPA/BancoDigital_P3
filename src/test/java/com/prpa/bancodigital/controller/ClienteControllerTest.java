package com.prpa.bancodigital.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prpa.bancodigital.exception.ApiException;
import com.prpa.bancodigital.exception.InvalidInputParameterException;
import com.prpa.bancodigital.exception.ResourceNotFoundException;
import com.prpa.bancodigital.exception.ValidationException;
import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.Endereco;
import com.prpa.bancodigital.model.Tier;
import com.prpa.bancodigital.model.dtos.ClienteDTO;
import com.prpa.bancodigital.model.external.cep.CepService;
import com.prpa.bancodigital.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static com.prpa.bancodigital.config.ApplicationConfig.API_V1;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ClienteController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ClienteControllerTest {

    private static final String CLIENTE_MAPPING = API_V1 + "/clientes";

    @MockitoBean
    private CepService cepService;

    @MockitoBean
    private ClienteService clienteService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Endereco enderecoTeste1;
    private Endereco enderecoTeste2;

    private Tier tier1;
    private Tier tier2;

    private Cliente clienteTeste1;
    private Cliente clienteTeste2;

    @BeforeEach
    void setup() {
        tier1 = new Tier(1L, "TIERUM");
        tier2 = new Tier(2L, "TIERDOIS");

        enderecoTeste1 = new Endereco(1L, "01025-020", "", 12, "Rua teste1", "Bairro teste1", "Cidade teste1", "Estado teste1");
        enderecoTeste2 = new Endereco(2L, "01015-100", "", 3, "Rua teste2", "Bairro teste2", "Cidade teste2", "Estado teste2");

        clienteTeste1 = new Cliente();
        clienteTeste1.setId(1L);
        clienteTeste1.setNome("TesteUm");
        clienteTeste1.setDataNascimento(LocalDate.of(1989, 6, 2));
        clienteTeste1.setCpf("14658970899");
        clienteTeste1.setEndereco(enderecoTeste1);
        clienteTeste1.setTier(tier1);

        clienteTeste2 = new Cliente();
        clienteTeste2.setId(2L);
        clienteTeste2.setNome("TesteDois");
        clienteTeste2.setDataNascimento(LocalDate.of(1983, 3, 13));
        clienteTeste2.setCpf("83441349865");
        clienteTeste2.setEndereco(enderecoTeste2);
        clienteTeste2.setTier(tier2);
    }

    // ------------------------------------------
    // Caminhos felizes :)
    // ------------------------------------------

    @Test
    @DisplayName("FindAll retorna todos clientes corretamente")
    void whenGETFindAllTestShould200OK() throws Exception {
        List<Cliente> whenFindAllClienteService = List.of(clienteTeste1, clienteTeste2);
        when(clienteService.findAll(any())).thenReturn(whenFindAllClienteService);

        URI requestURI = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).build().toUri();
        ResultActions resultActions = mockMvc.perform(get(requestURI).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$", hasSize(whenFindAllClienteService.size())));
        testJsonPathEqualsToCliente("$[0]", clienteTeste1, resultActions);
        testJsonPathEqualsToCliente("$[1]", clienteTeste2, resultActions);
    }

    @Test
    @DisplayName("FindAll retorna os clientes corretamente de acordo com a paginação")
    void whenGETFindAllWithPaginationTestShould200OK() throws Exception {
        final int DEFAULT_PAGE = ClienteController.DEFAULT_PAGE;
        final int DEFAULT_SIZE = ClienteController.DEFAULT_SIZE;
        final int MAX_PAGE_SIZE = ClienteController.MAX_PAGE_SIZE;

        List<Cliente> whenFindAllClienteService = IntStream.range(0, 120)
                .mapToObj(i -> i % 2 == 0 ? clienteTeste1 : clienteTeste2)
                .toList();

        when(clienteService.findAll(any())).thenReturn(whenFindAllClienteService);

        URI requestURI = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).build().toUri();
        mockMvc.perform(get(requestURI)
                        .accept(APPLICATION_JSON)
                        .param("size", String.valueOf(MAX_PAGE_SIZE + 1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        verify(clienteService).findAll(PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE));

        mockMvc.perform(get(requestURI)
                        .accept(APPLICATION_JSON)
                        .param("size", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        verify(clienteService).findAll(PageRequest.of(DEFAULT_PAGE, 30));

        mockMvc.perform(get(requestURI)
                        .accept(APPLICATION_JSON)
                        .param("page", "10")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        verify(clienteService).findAll(PageRequest.of(10, 100));
    }

    @Test
    @DisplayName("FindById o cliente corretamente pelo ID")
    void whenGETFindByIdTestShould200OK() throws Exception {
        final long ID = clienteTeste1.getId();
        when(clienteService.findById(ID)).thenReturn(clienteTeste1);

        URI requestURI = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).path("/{id}").build(ID);
        ResultActions resultActions = mockMvc.perform(get(requestURI).accept(APPLICATION_JSON))
                .andExpect(status().isOk());
        testJsonPathEqualsToCliente(clienteTeste1, resultActions);
    }

    @Test
    @DisplayName("Post novo cliente valido")
    void whenPOSTNewValidClienteTestShould201CREATED() throws Exception {
        URI expectedLocation = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).path("/{id}").build(clienteTeste1.getId());

        ClienteDTO clienteTeste1DTO = ClienteDTO.from(clienteTeste1);
        when(clienteService.newCliente(clienteTeste1DTO.toCliente())).thenReturn(clienteTeste1);

        URI requestURI = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).build().toUri();
        ResultActions resultActions = mockMvc.perform(post(requestURI)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clienteTeste1DTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(expectedLocation.toString())));
        testJsonPathEqualsToCliente(clienteTeste1, resultActions);
    }

    @Test
    @DisplayName("Put mudar cliente existente")
    void whenPUTValidClienteTestShould200OK() throws Exception {
        final long ID_CLIENTE1 = clienteTeste1.getId();
        ClienteDTO clienteTeste2DTO = ClienteDTO.from(clienteTeste2);

        clienteTeste2.setId(clienteTeste1.getId());
        when(clienteService.changeById(ID_CLIENTE1, clienteTeste2DTO.toCliente())).thenReturn(clienteTeste2);

        URI requestURI = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).path("/{id}").build(ID_CLIENTE1);
        ResultActions resultActions = mockMvc.perform(put(requestURI)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clienteTeste2DTO)))
                .andExpect(status().isOk());
        testJsonPathEqualsToCliente(clienteTeste2, resultActions);
    }

    @Test
    @DisplayName("Deletar cliente que existe")
    void whenDELETEClienteIDTestShould200OK() throws Exception {
        URI requestURI = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).path("/{id}").build(1L);
        mockMvc.perform(delete(requestURI))
                .andExpect(status().isNoContent());
    }

    // ------------------------------------------
    // Caminhos tristes :(
    // ------------------------------------------

    // GET
    // ------------------------------------------

    @Test
    @DisplayName("Busca um cliente por um ID inexistente")
    void whenGETClienteByInvalidIDTestShould404NOTFOUND() throws Exception {
        final long invalidId = 999999L;
        String expectedDetail = "Id não encontrado";

        ResourceNotFoundException notFoundException = new ResourceNotFoundException(expectedDetail);
        when(clienteService.findById(invalidId))
                .thenThrow(notFoundException);

        URI requestURI = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).path("/{id}").build(invalidId);
        ResultActions resultActions = mockMvc.perform(get(requestURI))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

        ProblemDetail expected = problemDetailFromException(requestURI, notFoundException, expectedDetail);
        testJsonPathEqualsToProblemDetail(expected, resultActions);
    }

    @Test
    @DisplayName("Busca um cliente por um ID negativo")
    void whenGETClienteByNegativeIDTestShould400BADREQUEST() throws Exception {
        final long invalidId = -999999L;
        String expectedDetail = "O parâmetro id deve ser maior ou igual a 0";

        URI requestURI = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).path("/{id}").build(invalidId);
        ResultActions resultActions = mockMvc.perform(get(requestURI))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

        InvalidInputParameterException invalidInputException = new InvalidInputParameterException(expectedDetail);
        ProblemDetail expected = problemDetailFromException(requestURI, invalidInputException, expectedDetail);
        testJsonPathEqualsToProblemDetail(expected, resultActions);
    }

    @Test
    @DisplayName("Busca um cliente por um ID com letras")
    void whenGETClienteByIDWithLettersTestShould400BADREQUEST() throws Exception {
        URI requestURI = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).path("/{id}").build("923sdlfkj");
        mockMvc.perform(get(requestURI))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    @DisplayName("Busca por todos clientes em uma pagina muito grande deve retornar vazio")
    void whenGETAllClientesEmptyPageTestShouldReturnEmpty200OK() throws Exception {
        when(clienteService.findAll(any())).thenReturn(List.of());

        URI requestURI = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).build().toUri();
        mockMvc.perform(get(requestURI)
                        .param("page", "1000"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(clienteService).findAll(PageRequest.of(1000, ClienteController.DEFAULT_SIZE));
    }

    @ParameterizedTest
    @CsvSource({
            "page, -1",
            "size, -4",
            "size, 400000",
    })
    @DisplayName("Busca por todos clientes em uma paginas inválidas deve retornar valores padrão")
    void whenGETAllClientesWithInvalidPagesTestShouldReturnPage200OK(String  param, String value) throws Exception {
        URI requestURI = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).build().toUri();
        mockMvc.perform(get(requestURI)
                        .param(param, value))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));

        verify(clienteService).findAll(PageRequest.of(ClienteController.DEFAULT_PAGE, ClienteController.DEFAULT_SIZE));
    }

    // ------------------------------------------
    // POST
    // ------------------------------------------

    @Test
    @DisplayName("Insere um cliente com nome invalido")
    void whenPOSTInvalidClienteNomeTestShould400BADREQUEST() throws Exception {
        String emptyName = "";
        String nameTooShort = "P";
        String nameTooLong = "Paulo".repeat(120);
        String nameInvalidChars = "Paulo 123";

        String[] invalidNames = {emptyName, nameTooShort, nameTooLong, nameInvalidChars};
        URI requestURI = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).build().toUri();
        ValidationException validationException = new ValidationException("");

        for (int i = 0; i < invalidNames.length; i++) {
            clienteTeste1.setNome(invalidNames[i]);
            ResultActions resultActions = mockMvc.perform(post(requestURI)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ClienteDTO.from(clienteTeste1))))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

            ProblemDetail expected = problemDetailFromException(requestURI, validationException, ValidationException.DETAIL);
            testJsonPathEqualsToProblemDetail(expected, resultActions);
        }
    }

    @Test
    @DisplayName("Insere um cliente com cpf invalido")
    void whenPOSTInvalidClienteCPFTestShould400BADREQUEST() throws Exception {
        String emptyCpf = "";
        String invalidDigit = "105.466.338-61";
        String invalidFormat = "105.466338-61";

        String[] invalidNames = {emptyCpf, invalidDigit, invalidFormat};
        URI requestURI = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).build().toUri();
        ValidationException validationException = new ValidationException("");

        for (int i = 0; i < invalidNames.length; i++) {
            clienteTeste1.setNome(invalidNames[i]);
            ResultActions resultActions = mockMvc.perform(post(requestURI)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ClienteDTO.from(clienteTeste1))))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

            ProblemDetail expected = problemDetailFromException(requestURI, validationException, ValidationException.DETAIL);
            testJsonPathEqualsToProblemDetail(expected, resultActions);
        }
    }

    // ------------------------------------------
    // PUT
    // ------------------------------------------

    @Test
    @DisplayName("Edita um cliente com nome invalido")
    void whenPUTInvalidClienteNomeTestShould400BADREQUEST() throws Exception {
        String emptyName = "";
        String nameTooShort = "P";
        String nameTooLong = "Paulo".repeat(120);
        String nameInvalidChars = "Paulo 123";

        String[] invalidNames = {emptyName, nameTooShort, nameTooLong, nameInvalidChars};
        URI requestURI = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).path("/{id}").build(1L);
        ValidationException validationException = new ValidationException("");

        for (int i = 0; i < invalidNames.length; i++) {
            clienteTeste1.setNome(invalidNames[i]);
            ResultActions resultActions = mockMvc.perform(put(requestURI)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ClienteDTO.from(clienteTeste1))))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

            ProblemDetail expected = problemDetailFromException(requestURI, validationException, ValidationException.DETAIL);
            testJsonPathEqualsToProblemDetail(expected, resultActions);
        }
    }

    @Test
    @DisplayName("Edita um cliente com cpf invalido")
    void whenPUTInvalidClienteCPFTestShould400BADREQUEST() throws Exception {
        String emptyCpf = "";
        String invalidDigit = "105.466.338-61";
        String invalidFormat = "105.466338-61";

        String[] invalidNames = {emptyCpf, invalidDigit, invalidFormat};
        URI requestURI = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).path("/{id}").build(1L);
        ValidationException validationException = new ValidationException("");

        for (int i = 0; i < invalidNames.length; i++) {
            clienteTeste1.setNome(invalidNames[i]);
            ResultActions resultActions = mockMvc.perform(put(requestURI)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ClienteDTO.from(clienteTeste1))))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

            ProblemDetail expected = problemDetailFromException(requestURI, validationException, ValidationException.DETAIL);
            testJsonPathEqualsToProblemDetail(expected, resultActions);
        }
    }

    @Test
    @DisplayName("Edita um cliente por um ID inexistente")
    void whenPUTClienteByInvalidIDTestShould404NOTFOUND() throws Exception {
        final long ID = 1L;
        String expectedDetail = "Id não encontrado";

        ResourceNotFoundException notFoundException = new ResourceNotFoundException(expectedDetail);
        when(clienteService.changeById(eq(ID), any())).thenThrow(notFoundException);

        URI requestURI = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).path("/{id}").build(ID);
        ResultActions resultActions = mockMvc.perform(put(requestURI)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ClienteDTO.from(clienteTeste1))))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

        ProblemDetail expected = problemDetailFromException(requestURI, notFoundException, expectedDetail);
        testJsonPathEqualsToProblemDetail(expected, resultActions);
    }

    // ------------------------------------------
    // DELETE
    // ------------------------------------------

    @Test
    @DisplayName("Remove um cliente por um ID inexistente")
    void whenDELETEClienteByInvalidIDTestShould404NOTFOUND() throws Exception {
        final long ID = 1L;
        String expectedDetail = "Id não encontrado";

        ResourceNotFoundException notFoundException = new ResourceNotFoundException(expectedDetail);
        doThrow(notFoundException).when(clienteService).deleteById(ID);

        URI requestURI = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).path("/{id}").build(ID);
        ResultActions resultActions = mockMvc.perform(delete(requestURI))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

        ProblemDetail expected = problemDetailFromException(requestURI, notFoundException, expectedDetail);
        testJsonPathEqualsToProblemDetail(expected, resultActions);
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

    private void testJsonPathEqualsToCliente(Cliente target, ResultActions resultActions) throws Exception {
        testJsonPathEqualsToCliente("$", target, resultActions);
    }

    private void testJsonPathEqualsToCliente(String jsonTarget, Cliente target, ResultActions onGoingTest) throws Exception {
        onGoingTest.andExpect(jsonPath(jsonTarget).exists())
                .andExpect(jsonPath(jsonTarget + ".id", equalTo(target.getId().intValue())))
                .andExpect(jsonPath(jsonTarget + ".nome", equalTo(target.getNome())))
                .andExpect(jsonPath(jsonTarget + ".dataNascimento", equalTo(target.getDataNascimento().toString())))
                .andExpect(jsonPath(jsonTarget + ".cpf", equalTo(target.getCpf())))
                .andExpect(jsonPath(jsonTarget + ".endereco.id", equalTo(target.getEndereco().getId().intValue())))
                .andExpect(jsonPath(jsonTarget + ".endereco.complemento", equalTo(target.getEndereco().getComplemento())))
                .andExpect(jsonPath(jsonTarget + ".endereco.numero", equalTo(target.getEndereco().getNumero())))
                .andExpect(jsonPath(jsonTarget + ".endereco.rua", equalTo(target.getEndereco().getRua())))
                .andExpect(jsonPath(jsonTarget + ".endereco.bairro", equalTo(target.getEndereco().getBairro())))
                .andExpect(jsonPath(jsonTarget + ".endereco.cidade", equalTo(target.getEndereco().getCidade())))
                .andExpect(jsonPath(jsonTarget + ".endereco.estado", equalTo(target.getEndereco().getEstado())));
    }

    private ProblemDetail problemDetailFromException(URI requestURI, ApiException exception, String expectedDetail) {
        ProblemDetail expectedProblemDetail = ProblemDetail.forStatusAndDetail(exception.getStatusCode(), expectedDetail);
        expectedProblemDetail.setType(URI.create(exception.getType()));
        expectedProblemDetail.setInstance(requestURI);
        return expectedProblemDetail;
    }

}
