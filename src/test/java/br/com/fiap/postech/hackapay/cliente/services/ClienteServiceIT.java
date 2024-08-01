package br.com.fiap.postech.hackapay.cliente.services;

import br.com.fiap.postech.hackapay.cliente.entities.Cliente;
import br.com.fiap.postech.hackapay.cliente.helper.ClienteHelper;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class ClienteServiceIT {
    @Autowired
    private ClienteService clienteService;

    @Nested
    class CadastrarCliente {
        @Test
        void devePermitirCadastrarCliente() {
            // Arrange
            var cliente = ClienteHelper.getCliente(false);
            // Act
            var clienteSalvo = clienteService.save(cliente);
            // Assert
            assertThat(clienteSalvo)
                    .isInstanceOf(Cliente.class)
                    .isNotNull();
            assertThat(clienteSalvo.getNome()).isEqualTo(cliente.getNome());
            assertThat(clienteSalvo.getId()).isNotNull();
        }
    }

    @Nested
    class BuscarCliente {
        @Test
        void devePermitirBuscarClientePorId() {
            // Arrange
            var id = UUID.fromString("56833f9a-7fda-49d5-a760-8e1ba41f35a8");
            var nome = "Anderson Wagner";
            // Act
            var clienteObtido = clienteService.findById(id);
            // Assert
            assertThat(clienteObtido).isNotNull().isInstanceOf(Cliente.class);
            assertThat(clienteObtido.getNome()).isEqualTo(nome);
            assertThat(clienteObtido.getId()).isNotNull();
            assertThat(clienteObtido.getId()).isEqualTo(id);
        }

        @Test
        void deveGerarExcecao_QuandoBuscarClientePorId_idNaoExiste() {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            UUID uuid = cliente.getId();
            // Act &&  Assert
            assertThatThrownBy(() -> clienteService.findById(uuid))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Cliente não encontrado com o ID: " + cliente.getId());
        }

        @Test
        void devePermitirBuscarTodosCliente() {
            // Arrange
            Cliente criteriosDeBusca = new Cliente(null,null,null,null,null,null,null,null,null);
            criteriosDeBusca.setId(null);
            // Act
            var listaClientesObtidos = clienteService.findAll(Pageable.unpaged(), criteriosDeBusca);
            // Assert
            assertThat(listaClientesObtidos).isNotNull().isInstanceOf(Page.class);
            assertThat(listaClientesObtidos.getContent()).asList().hasSize(3);
            assertThat(listaClientesObtidos.getContent()).asList().allSatisfy(
                    clienteObtido -> {
                        assertThat(clienteObtido).isNotNull();
                    }
            );
        }
    }

    @Nested
    class AlterarCliente {

        @Test
        void devePermitirAlterarCliente() {
            // Arrange
            var id = UUID.fromString("ab8fdcd5-c9b5-471e-8ad0-380a65d6cc86");
            var nome = "Kaiby novo";
            var cpf = "52816804046";
            var cliente = new Cliente(nome, cpf,
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true)
            );
            cliente.setId(null);
            // Act
            var clienteAtualizada = clienteService.update(id, cliente);
            // Assert
            assertThat(clienteAtualizada).isNotNull().isInstanceOf(Cliente.class);
            assertThat(clienteAtualizada.getId()).isNotNull();
            assertThat(clienteAtualizada.getNome()).isEqualTo(nome);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarClientePorId_idNaoExiste() {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            var uuid = cliente.getId();
            // Act &&  Assert
            assertThatThrownBy(() -> clienteService.update(uuid, cliente))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Cliente não encontrado com o ID: " + cliente.getId());
        }
    }

    @Nested
    class RemoverCliente {
        @Test
        void devePermitirRemoverCliente() {
            // Arrange
            var id = UUID.fromString("8855e7b2-77b6-448b-97f8-8a0b529f3976");
            // Act
            clienteService.delete(id);
            // Assert
            assertThatThrownBy(() -> clienteService.findById(id))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Cliente não encontrado com o ID: " + id);
            ;
        }

        @Test
        void deveGerarExcecao_QuandRemoverClientePorId_idNaoExiste() {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            var uuid = cliente.getId();
            // Act &&  Assert
            assertThatThrownBy(() -> clienteService.delete(uuid))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Cliente não encontrado com o ID: " + cliente.getId());
            ;
        }
    }
}
