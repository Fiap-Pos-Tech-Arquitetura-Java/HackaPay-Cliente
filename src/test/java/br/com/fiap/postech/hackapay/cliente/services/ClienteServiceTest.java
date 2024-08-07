package br.com.fiap.postech.hackapay.cliente.services;

import br.com.fiap.postech.hackapay.cliente.entities.Cliente;
import br.com.fiap.postech.hackapay.cliente.helper.ClienteHelper;
import br.com.fiap.postech.hackapay.cliente.repository.ClienteRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClienteServiceTest {
    private ClienteService clienteService;

    @Mock
    private ClienteRepository clienteRepository;

    private AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        clienteService = new ClienteServiceImpl(clienteRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class CadastrarCliente {
        @Test
        void devePermitirCadastrarCliente() {
            // Arrange
            var cliente = ClienteHelper.getCliente(false);
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            var clienteSalvo = clienteService.save(cliente);
            // Assert
            assertThat(clienteSalvo)
                    .isInstanceOf(Cliente.class)
                    .isNotNull();
            assertThat(clienteSalvo.getNome()).isEqualTo(cliente.getNome());
            assertThat(clienteSalvo.getId()).isNotNull();
            verify(clienteRepository, times(1)).save(any(Cliente.class));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarCliente_cpfExistente() {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            when(clienteRepository.findByCpf(cliente.getCpf())).thenReturn(Optional.of(cliente));
            // Act
            assertThatThrownBy(() -> clienteService.save(cliente))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Já existe um cliente cadastrado com esse cpf.");
            // Assert
            verify(clienteRepository, times(1)).findByCpf(anyString());
            verify(clienteRepository, never()).save(any(Cliente.class));
        }
    }

    @Nested
    class BuscarCliente {
        @Test
        void devePermitirBuscarClientePorId() {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
            // Act
            var clienteObtido = clienteService.findById(cliente.getId());
            // Assert
            assertThat(clienteObtido).isEqualTo(cliente);
            verify(clienteRepository, times(1)).findById(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarClientePorId_idNaoExiste() {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.empty());
            UUID uuid = cliente.getId();
            // Act
            assertThatThrownBy(() -> clienteService.findById(uuid))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Cliente não encontrado com o ID: " + cliente.getId());
            // Assert
            verify(clienteRepository, times(1)).findById(any(UUID.class));
        }

        @Test
        void devePermitirBuscarTodosCliente() {
            // Arrange
            Cliente criteriosDeBusca = ClienteHelper.getCliente(false);
            Page<Cliente> clientes = new PageImpl<>(Arrays.asList(
                    ClienteHelper.getCliente(true),
                    ClienteHelper.getCliente(true),
                    ClienteHelper.getCliente(true)
            ));
            when(clienteRepository.findAll(any(Example.class), any(Pageable.class))).thenReturn(clientes);
            // Act
            var clientesObtidos = clienteService.findAll(Pageable.unpaged(), criteriosDeBusca);
            // Assert
            assertThat(clientesObtidos).hasSize(3);
            assertThat(clientesObtidos.getContent()).asList().allSatisfy(
                    cliente -> {
                        assertThat(cliente)
                                .isNotNull()
                                .isInstanceOf(Cliente.class);
                    }
            );
            verify(clienteRepository, times(1)).findAll(any(Example.class), any(Pageable.class));
        }
        @Test
        void devePermitirBuscarClientePorCpf() {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            when(clienteRepository.findByCpf(cliente.getCpf())).thenReturn(Optional.of(cliente));
            // Act
            var clienteObtido = clienteService.findByCpf(cliente.getCpf());
            // Assert
            assertThat(clienteObtido).isEqualTo(cliente);
            verify(clienteRepository, times(1)).findByCpf(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoBuscarClientePorCpf_CpfNaoExiste() {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            when(clienteRepository.findByCpf(cliente.getCpf())).thenReturn(Optional.empty());
            // Act
            assertThatThrownBy(() -> clienteService.findByCpf(cliente.getCpf()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Cliente não encontrado com o cpf: " + cliente.getCpf());
            // Assert
            verify(clienteRepository, times(1)).findByCpf(anyString());
        }
    }

    @Nested
    class AlterarCliente {
        @Test
        void devePermitirAlterarCliente() {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            var clienteReferencia = new Cliente(
                    cliente.getNome(),
                    cliente.getCpf(),
                    cliente.getEmail(),
                    cliente.getTelefone(),
                    cliente.getRua(),
                    cliente.getCep(),
                    cliente.getCidade(),
                    cliente.getEstado(),
                    cliente.getPais()
            );
            var novoCliente = new Cliente(
                    RandomStringUtils.random(20, true, true),
                    cliente.getCpf(),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true)
            );
            novoCliente.setId(cliente.getId());
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            var clienteSalvo = clienteService.update(cliente.getId(), novoCliente);
            // Assert
            assertThat(clienteSalvo)
                    .isInstanceOf(Cliente.class)
                    .isNotNull();
            assertThat(clienteSalvo.getNome()).isEqualTo(novoCliente.getNome());
            assertThat(clienteSalvo.getNome()).isNotEqualTo(clienteReferencia.getNome());

            verify(clienteRepository, times(1)).findById(any(UUID.class));
            verify(clienteRepository, times(1)).save(any(Cliente.class));
        }

        @Test
        void devePermitirAlterarCliente_enderecoComId() {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            var clienteReferencia = new Cliente(
                    cliente.getNome(),
                    cliente.getCpf(),
                    cliente.getEmail(),
                    cliente.getTelefone(),
                    cliente.getRua(),
                    cliente.getCep(),
                    cliente.getCidade(),
                    cliente.getEstado(),
                    cliente.getPais()
            );
            var novoCliente = new Cliente(
                    RandomStringUtils.random(20, true, true),
                    cliente.getCpf(),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true)
            );
            novoCliente.setId(cliente.getId());
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            var clienteSalvo = clienteService.update(cliente.getId(), novoCliente);
            // Assert
            assertThat(clienteSalvo)
                    .isInstanceOf(Cliente.class)
                    .isNotNull();
            assertThat(clienteSalvo.getNome()).isEqualTo(novoCliente.getNome());
            assertThat(clienteSalvo.getNome()).isNotEqualTo(clienteReferencia.getNome());

            verify(clienteRepository, times(1)).findById(any(UUID.class));
            verify(clienteRepository, times(1)).save(any(Cliente.class));
        }

        @Test
        void devePermitirAlterarCliente_semBody() {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            var clienteReferencia = new Cliente(
                    cliente.getNome(),
                    cliente.getCpf(),
                    cliente.getEmail(),
                    cliente.getTelefone(),
                    cliente.getRua(),
                    cliente.getCep(),
                    cliente.getCidade(),
                    cliente.getEstado(),
                    cliente.getPais()
            );
            var novoCliente = new Cliente(null, null, null, null, null, null, null, null, null);

            novoCliente.setId(cliente.getId());
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            var clienteSalvo = clienteService.update(cliente.getId(), novoCliente);
            // Assert
            assertThat(clienteSalvo)
                    .isInstanceOf(Cliente.class)
                    .isNotNull();
            assertThat(clienteSalvo.getNome()).isEqualTo(clienteReferencia.getNome());

            verify(clienteRepository, times(1)).findById(any(UUID.class));
            verify(clienteRepository, times(1)).save(any(Cliente.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarClientePorId_idNaoExiste() {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.empty());
            UUID uuid = cliente.getId();
            // Act && Assert
            assertThatThrownBy(() -> clienteService.update(uuid, cliente))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Cliente não encontrado com o ID: " + cliente.getId());
            verify(clienteRepository, times(1)).findById(any(UUID.class));
            verify(clienteRepository, never()).save(any(Cliente.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarClientePorId_alterandoId() {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            var clienteParam = ClienteHelper.getCliente(true);
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
            UUID uuid = cliente.getId();
            // Act && Assert
            assertThatThrownBy(() -> clienteService.update(uuid, clienteParam))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Não é possível alterar o id de um cliente.");
            verify(clienteRepository, times(1)).findById(any(UUID.class));
            verify(clienteRepository, never()).save(any(Cliente.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarClientePorId_alterandoCpf() {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            var clienteParam = ClienteHelper.getCliente(true);
            clienteParam.setId(cliente.getId());
            clienteParam.setCpf("03485066001");
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
            UUID uuid = cliente.getId();
            // Act && Assert
            assertThatThrownBy(() -> clienteService.update(uuid, clienteParam))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Não é possível alterar o cpf de um cliente.");
            verify(clienteRepository, times(1)).findById(any(UUID.class));
            verify(clienteRepository, never()).save(any(Cliente.class));
        }
    }

    @Nested
    class RemoverCliente {
        @Test
        void devePermitirRemoverCliente() {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
            doNothing().when(clienteRepository).deleteById(cliente.getId());
            // Act
            clienteService.delete(cliente.getId());
            // Assert
            verify(clienteRepository, times(1)).findById(any(UUID.class));
            verify(clienteRepository, times(1)).deleteById(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandRemoverClientePorId_idNaoExiste() {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            doNothing().when(clienteRepository).deleteById(cliente.getId());
            UUID uuid = cliente.getId();
            // Act && Assert
            assertThatThrownBy(() -> clienteService.delete(uuid))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Cliente não encontrado com o ID: " + cliente.getId());
            verify(clienteRepository, times(1)).findById(any(UUID.class));
            verify(clienteRepository, never()).deleteById(any(UUID.class));
        }
    }
}