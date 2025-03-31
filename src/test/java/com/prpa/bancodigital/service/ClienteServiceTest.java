package com.prpa.bancodigital.service;

import com.prpa.bancodigital.exception.ResourceAlreadyExistsException;
import com.prpa.bancodigital.model.Cliente;
import com.prpa.bancodigital.model.Endereco;
import com.prpa.bancodigital.model.Tier;
import com.prpa.bancodigital.repository.ClienteRepository;
import com.prpa.bancodigital.repository.TierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private TierRepository tierRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Endereco enderecoTeste1;
    private Endereco enderecoTeste2;

    private Tier tier1;
    private Tier tier2;

    private Cliente clienteTeste1;
    private Cliente clienteTeste2;

    @BeforeEach
    public void setup() {
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

    @Test
    @DisplayName("Ao tentar salvar um cliente com nome que ja existe deve lançar uma exceção")
    public void whenSaveNewClienteWithNomeThatAlreadyExistsShouldThrow() {
        when(clienteRepository.existsByNome(any())).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class, () -> {
            clienteService.newCliente(clienteTeste1);
        });
    }

    @Test
    @DisplayName("Ao tentar salvar um cliente com cpf que ja existe deve lançar uma exceção")
    public void whenSaveNewClienteWithCPFThatAlreadyExistsShouldThrow() {
        when(clienteRepository.existsByCpf(any())).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class, () -> {
            clienteService.newCliente(clienteTeste1);
        });
    }


    @Test
    @DisplayName("Ao tentar editar um cliente com nome que ja existe deve lançar uma exceção")
    public void whenChangeClienteWithNomeThatAlreadyExistsShouldThrow() {
        when(clienteRepository.existsById(any())).thenReturn(true);
        when(clienteRepository.findById(any())).thenReturn(Optional.ofNullable(clienteTeste1));
        when(clienteRepository.existsByNome(any())).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class, () -> {
            clienteService.changeById(clienteTeste1.getId(), clienteTeste2);
        });
    }

    @Test
    @DisplayName("Ao tentar editar um cliente com cpf que ja existe deve lançar uma exceção")
    public void whenChangeClienteWithCPFThatAlreadyExistsShouldThrow() {
        when(clienteRepository.existsById(any())).thenReturn(true);
        when(clienteRepository.findById(any())).thenReturn(Optional.ofNullable(clienteTeste1));
        when(clienteRepository.existsByCpf(any())).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class, () -> {
            clienteService.changeById(clienteTeste1.getId(), clienteTeste2);
        });
    }


    @Test
    @DisplayName("Ao tentar editar um cliente com nome do próprio cliente sendo editado")
    public void whenChangeClienteWithItsOwnNameShouldThrow() {
        when(tierRepository.findByNomeIgnoreCase(any())).thenReturn(Optional.of(tier1));
        when(clienteRepository.existsById(any())).thenReturn(true);
        when(clienteRepository.findById(any())).thenReturn(Optional.ofNullable(clienteTeste1));
        assertDoesNotThrow(() -> {
            clienteService.changeById(clienteTeste1.getId(), clienteTeste1);
        });
    }

    @Test
    @DisplayName("Ao tentar editar um cliente com cpf do próprio cliente sendo editado")
    public void whenChangeClienteWithItsOwnCPFShouldThrow() {
        when(tierRepository.findByNomeIgnoreCase(any())).thenReturn(Optional.of(tier1));
        when(clienteRepository.existsById(any())).thenReturn(true);
        when(clienteRepository.findById(any())).thenReturn(Optional.ofNullable(clienteTeste1));
        assertDoesNotThrow(() -> {
            clienteService.changeById(clienteTeste1.getId(), clienteTeste1);
        });
    }
}