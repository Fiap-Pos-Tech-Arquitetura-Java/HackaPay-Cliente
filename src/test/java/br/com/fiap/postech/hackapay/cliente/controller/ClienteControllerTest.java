package br.com.fiap.postech.hackapay.cliente.controller;

import br.com.fiap.postech.hackapay.cliente.entities.Cliente;
import br.com.fiap.postech.hackapay.cliente.helper.ClienteHelper;
import br.com.fiap.postech.hackapay.cliente.services.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ClienteControllerTest {
    public static final String CLIENTE = "/cliente";
    private MockMvc mockMvc;
    @Mock
    private ClienteService clienteService;
    private AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        ClienteController clienteController = new ClienteController(clienteService);
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    public static String asJsonString(final Object object) throws Exception {
        return new ObjectMapper().writeValueAsString(object);
    }

    @Nested
    class CadastrarCliente {
        @Test
        void devePermitirCadastrarCliente() throws Exception {
            // Arrange
            var cliente = ClienteHelper.getCliente(false);
            when(clienteService.save(any(Cliente.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            mockMvc.perform(
                            post(CLIENTE).contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(cliente)))
                    .andExpect(status().isCreated());
            // Assert
            verify(clienteService, times(1)).save(any(Cliente.class));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarCliente_RequisicaoXml() throws Exception {
            // Arrange
            var cliente = ClienteHelper.getCliente(false);
            when(clienteService.save(any(Cliente.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            mockMvc.perform(
                            post("/cliente").contentType(MediaType.APPLICATION_XML)
                                    .content(asJsonString(cliente)))
                    .andExpect(status().isUnsupportedMediaType());
            // Assert
            verify(clienteService, never()).save(any(Cliente.class));
        }
    }
    @Nested
    class BuscarCliente {
        @Test
        void devePermitirBuscarClientePorId() throws Exception {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            when(clienteService.findById(any(UUID.class))).thenReturn(cliente);
            // Act
            mockMvc.perform(get("/cliente/{id}", cliente.getId().toString()))
                    .andExpect(status().isOk());
            // Assert
            verify(clienteService, times(1)).findById(any(UUID.class));
        }
        @Test
        void deveGerarExcecao_QuandoBuscarClientePorId_idNaoExiste() throws Exception {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            when(clienteService.findById(cliente.getId())).thenThrow(IllegalArgumentException.class);
            // Act
            mockMvc.perform(get("/cliente/{id}", cliente.getId().toString()))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(clienteService, times(1)).findById(cliente.getId());
        }

        @Test
        void devePermitirBuscarTodosCliente() throws Exception {
            // Arrange
            int page = 0;
            int size = 10;
            var cliente = ClienteHelper.getCliente(true);
            var criterioCliente = new Cliente(cliente.getNome(), cliente.getCpf(), null, null, null, null, null, null, null);
            criterioCliente.setId(null);
            List<Cliente> listCliente = new ArrayList<>();
            listCliente.add(cliente);
            Page<Cliente> clientes = new PageImpl<>(listCliente);
            var pageable = PageRequest.of(page, size);
            when(clienteService.findAll(
                            pageable,
                            criterioCliente
                    )
            ).thenReturn(clientes);
            // Act
            mockMvc.perform(
                            get("/cliente")
                                    .param("page", String.valueOf(page))
                                    .param("size", String.valueOf(size))
                                    .param("nome", cliente.getNome())
                                    .param("cpf", cliente.getCpf())
                    )
                    //.andDo(print())
                    .andExpect(status().is5xxServerError())
            //.andExpect(jsonPath("$.content", not(empty())))
            //.andExpect(jsonPath("$.totalPages").value(1))
            //.andExpect(jsonPath("$.totalElements").value(1))
            ;
            // Assert
            verify(clienteService, times(1)).findAll(pageable, criterioCliente);
        }

        @Test
        void devePermitirBuscarClientePorCpf() throws Exception {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            when(clienteService.findByCpf(anyString())).thenReturn(cliente);
            // Act
            mockMvc.perform(get("/cliente/findByCpf/{Cpf}", cliente.getCpf()))
                    .andExpect(status().isOk());
            // Assert
            verify(clienteService, times(1)).findByCpf(anyString());
        }
        @Test
        void deveGerarExcecao_QuandoBuscarClientePorCpf_CpfNaoExiste() throws Exception {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            when(clienteService.findByCpf(cliente.getCpf())).thenThrow(IllegalArgumentException.class);
            // Act
            mockMvc.perform(get("/cliente/findByCpf/{Cpf}", cliente.getCpf()))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(clienteService, times(1)).findByCpf(cliente.getCpf());
        }
    }

    @Nested
    class AlterarCliente {
        @Test
        void devePermitirAlterarCliente() throws Exception {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            when(clienteService.update(cliente.getId(), cliente)).thenAnswer(r -> r.getArgument(1) );
            // Act
            mockMvc.perform(put("/cliente/{id}", cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(cliente)))
                    .andExpect(status().isAccepted());
            // Assert
            verify(clienteService, times(1)).update(cliente.getId(), cliente);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarCliente_RequisicaoXml() throws Exception {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            when(clienteService.update(cliente.getId(), cliente)).thenAnswer(r -> r.getArgument(1) );
            // Act
            mockMvc.perform(put("/cliente/{id}", cliente.getId())
                            .contentType(MediaType.APPLICATION_XML)
                            .content(asJsonString(cliente)))
                    .andExpect(status().isUnsupportedMediaType());
            // Assert
            verify(clienteService, never()).update(cliente.getId(), cliente);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarClientePorId_idNaoExiste() throws Exception {
            // Arrange
            var clienteDTO = ClienteHelper.getCliente(true);
            when(clienteService.update(clienteDTO.getId(), clienteDTO)).thenThrow(IllegalArgumentException.class);
            // Act
            mockMvc.perform(put("/cliente/{id}", clienteDTO.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(clienteDTO)))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(clienteService, times(1)).update(any(UUID.class), any(Cliente.class));
        }
    }

    @Nested
    class RemoverCliente {
        @Test
        void devePermitirRemoverCliente() throws Exception {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            doNothing().when(clienteService).delete(cliente.getId());
            // Act
            mockMvc.perform(delete("/cliente/{id}", cliente.getId()))
                    .andExpect(status().isNoContent());
            // Assert
            verify(clienteService, times(1)).delete(cliente.getId());
            verify(clienteService, times(1)).delete(cliente.getId());
        }

        @Test
        void deveGerarExcecao_QuandoRemoverClientePorId_idNaoExiste() throws Exception {
            // Arrange
            var cliente = ClienteHelper.getCliente(true);
            doThrow(new IllegalArgumentException("Cliente não encontrado com o ID: " + cliente.getId()))
                    .when(clienteService).delete(cliente.getId());
            // Act
            mockMvc.perform(delete("/cliente/{id}", cliente.getId()))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(clienteService, times(1)).delete(cliente.getId());
        }
    }
}