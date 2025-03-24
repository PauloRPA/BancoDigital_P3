package com.prpa.bancodigital.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.Endereco;
import com.prpa.bancodigital.model.dtos.ClienteDTO;
import com.prpa.bancodigital.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import static com.prpa.bancodigital.config.ApplicationConfig.API_V1;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
public class ClienteControllerTest {

    private static final String CLIENTE_MAPPING = API_V1 + "/clientes";

    @MockitoBean
    private ClienteService clienteService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Endereco enderecoTeste1;
    private Endereco enderecoTeste2;

    private Cliente clienteTeste1;
    private Cliente clienteTeste2;

    @BeforeEach
    public void setup() {
        enderecoTeste1 = new Endereco(1L, "", 12, "Rua teste1", "Bairro teste1", "Cidade teste1", "Estado teste1");
        enderecoTeste2 = new Endereco(2L, "", 3, "Rua teste2", "Bairro teste2", "Cidade teste2", "Estado teste2");

        clienteTeste1 = new Cliente();
        clienteTeste1.setId(1L);
        clienteTeste1.setNome("TesteUm");
        clienteTeste1.setDataNascimento(LocalDate.of(1989, 6, 2));
        clienteTeste1.setCpf("14658970899");
        clienteTeste1.setEndereco(enderecoTeste1);

        clienteTeste2 = new Cliente();
        clienteTeste2.setId(2L);
        clienteTeste2.setNome("TesteDois");
        clienteTeste2.setDataNascimento(LocalDate.of(1983, 3, 13));
        clienteTeste2.setCpf("83441349865");
        clienteTeste2.setEndereco(enderecoTeste2);
    }

    // ------------------------------------------
    // Caminhos felizes :)
    // ------------------------------------------

    @Test
    @DisplayName("FindAll retorna todos clientes corretamente")
    public void whenGETFindAllTestShould200OK() throws Exception {
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
    @DisplayName("FindAll retorna os clientes corretamente de acordo com a paginacao")
    public void whenGETFindAllWithPaginationTestShould200OK() throws Exception {
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
    public void whenGETFindByIdTestShould200OK() throws Exception {
        final long ID = clienteTeste1.getId();
        when(clienteService.findById(ID)).thenReturn(clienteTeste1);

        URI requestURI = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).path("/{id}").build(ID);
        ResultActions resultActions = mockMvc.perform(get(requestURI).accept(APPLICATION_JSON))
                .andExpect(status().isOk());
        testJsonPathEqualsToCliente(clienteTeste1, resultActions);
    }

    @Test
    @DisplayName("Post novo cliente valido")
    public void whenPOSTNewValidClienteTestShould201CREATED() throws Exception {
        URI expectedLocation = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).path("/{id}").build(clienteTeste1.getId());

        ClienteDTO clienteTeste1DTO = ClienteDTO.from(clienteTeste1);
        when(clienteService.newCliente(eq(clienteTeste1DTO))).thenReturn(clienteTeste1);

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
    public void whenPUTValidClienteTestShould200OK() throws Exception {
        final long ID_CLIENTE1 = clienteTeste1.getId();
        ClienteDTO clienteTeste2DTO = ClienteDTO.from(clienteTeste2);

        clienteTeste2.setId(clienteTeste1.getId());
        when(clienteService.changeById(eq(ID_CLIENTE1), eq(clienteTeste2DTO))).thenReturn(clienteTeste2);

        URI requestURI = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).path("/{id}").build(ID_CLIENTE1);
        ResultActions resultActions = mockMvc.perform(put(requestURI)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(clienteTeste2DTO)))
                .andExpect(status().isOk());
        testJsonPathEqualsToCliente(clienteTeste2, resultActions);
    }

    @Test
    @DisplayName("Deletar cliente que existe")
    public void whenDELETEClienteIDTestShould200OK() throws Exception {
        URI requestURI = UriComponentsBuilder.fromPath(CLIENTE_MAPPING).path("/{id}").build(1L);
        mockMvc.perform(delete(requestURI))
                .andExpect(status().isNoContent());
    }

    // ------------------------------------------
    // Caminhos tristes :(
    // ------------------------------------------

    // ------------------------------------------
    // Utils
    // ------------------------------------------

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

}
