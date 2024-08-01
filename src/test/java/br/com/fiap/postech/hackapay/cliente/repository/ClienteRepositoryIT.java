package br.com.fiap.postech.hackapay.cliente.repository;

import br.com.fiap.postech.hackapay.cliente.entities.Cliente;
import br.com.fiap.postech.hackapay.cliente.helper.ClienteHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class ClienteRepositoryIT {
    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    void devePermitirCriarEstrutura() {
        var totalRegistros = clienteRepository.count();
        assertThat(totalRegistros).isEqualTo(3);
    }

    @Test
    void devePermitirCadastrarCliente() {
        // Arrange
        var cliente = ClienteHelper.getCliente(true);
        // Act
        var clienteCadastrado = clienteRepository.save(cliente);
        // Assert
        assertThat(clienteCadastrado).isInstanceOf(Cliente.class).isNotNull();
        assertThat(clienteCadastrado.getId()).isEqualTo(cliente.getId());
        assertThat(clienteCadastrado.getNome()).isEqualTo(cliente.getNome());
        assertThat(clienteCadastrado.getRua()).isEqualTo(cliente.getRua());
    }
    @Test
    void devePermitirBuscarCliente() {
        // Arrange
        var id = UUID.fromString("56833f9a-7fda-49d5-a760-8e1ba41f35a8");
        var nome = "Anderson Wagner";
        // Act
        var clienteOpcional = clienteRepository.findById(id);
        // Assert
        assertThat(clienteOpcional).isPresent();
        clienteOpcional.ifPresent(
                clienteRecebido -> {
                    assertThat(clienteRecebido).isInstanceOf(Cliente.class).isNotNull();
                    assertThat(clienteRecebido.getId()).isEqualTo(id);
                    assertThat(clienteRecebido.getNome()).isEqualTo(nome);
                }
        );
    }
    @Test
    void devePermitirRemoverCliente() {
        // Arrange
        var id = UUID.fromString("8855e7b2-77b6-448b-97f8-8a0b529f3976");
        // Act
        clienteRepository.deleteById(id);
        // Assert
        var clienteOpcional = clienteRepository.findById(id);
        assertThat(clienteOpcional).isEmpty();
    }
    @Test
    void devePermitirListarClientes() {
        // Arrange
        // Act
        var clientesListados = clienteRepository.findAll();
        // Assert
        assertThat(clientesListados).hasSize(3);
    }
}
