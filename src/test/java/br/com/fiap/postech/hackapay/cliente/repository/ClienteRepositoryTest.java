package br.com.fiap.postech.hackapay.cliente.repository;

import br.com.fiap.postech.hackapay.cliente.entities.Cliente;
import br.com.fiap.postech.hackapay.cliente.helper.ClienteHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClienteRepositoryTest {
    @Mock
    private ClienteRepository clienteRepository;

    AutoCloseable openMocks;
    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void devePermitirCadastrarCliente() {
        // Arrange
        var cliente = ClienteHelper.getCliente(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        // Act
        var savedCliente = clienteRepository.save(cliente);
        // Assert
        assertThat(savedCliente).isNotNull().isEqualTo(cliente);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void devePermitirBuscarCliente() {
        // Arrange
        var cliente = ClienteHelper.getCliente(true);
        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        // Act
        var clienteOpcional = clienteRepository.findById(cliente.getId());
        // Assert
        assertThat(clienteOpcional).isNotNull().containsSame(cliente);
        clienteOpcional.ifPresent(
                clienteRecebido -> {
                    assertThat(clienteRecebido).isInstanceOf(Cliente.class).isNotNull();
                    assertThat(clienteRecebido.getId()).isEqualTo(cliente.getId());
                    assertThat(clienteRecebido.getNome()).isEqualTo(cliente.getNome());
                }
        );
        verify(clienteRepository, times(1)).findById(cliente.getId());
    }
    @Test
    void devePermitirRemoverCliente() {
        //Arrange
        var id = UUID.randomUUID();
        doNothing().when(clienteRepository).deleteById(id);
        //Act
        clienteRepository.deleteById(id);
        //Assert
        verify(clienteRepository, times(1)).deleteById(id);
    }
    @Test
    void devePermitirListarClientes() {
        // Arrange
        var cliente1 = ClienteHelper.getCliente(true);
        var cliente2 = ClienteHelper.getCliente(true);
        var listaClientes = Arrays.asList(
                cliente1,
                cliente2
        );
        when(clienteRepository.findAll()).thenReturn(listaClientes);
        // Act
        var clientesListados = clienteRepository.findAll();
        assertThat(clientesListados)
                .hasSize(2)
                .containsExactlyInAnyOrder(cliente1, cliente2);
        verify(clienteRepository, times(1)).findAll();
    }
}