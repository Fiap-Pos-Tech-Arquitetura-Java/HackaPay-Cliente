package br.com.fiap.postech.hackapay.cliente.controller;


import br.com.fiap.postech.hackapay.cliente.entities.Cliente;
import br.com.fiap.postech.hackapay.cliente.helper.ClienteHelper;
import br.com.fiap.postech.hackapay.cliente.helper.UserHelper;
import br.com.fiap.postech.hackapay.security.UserDetailsServiceImpl;
import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class ClienteControllerIT {

    public static final String CLIENTE = "/api/cliente";
    @LocalServerPort
    private int port;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    class CadastrarCliente {
        @Test
        void devePermitirCadastrarCliente() {
            var cliente = ClienteHelper.getCliente(false);
            var userDetails = UserHelper.getUserDetails("umUsuarioQualquer");
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE).body(cliente)
                    .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(userDetails.getUsername()))
            .when()
                .post(CLIENTE)
            .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("schemas/cliente.schema.json"));
            // TODO VERIFICAR A OBRIGATORIEDADE DO ID
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarCliente_RequisicaoXml() {
            /*
              Na aula o professor instanciou uma string e enviou no .body()
              Mas como o teste valida o contentType o body pode ser enviado com qualquer conteudo
              ou nem mesmo ser enviado como ficou no teste abaixo.
             */
            var userDetails = UserHelper.getUserDetails("umUsuarioQualquer");
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            given()
                .contentType(MediaType.APPLICATION_XML_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(userDetails.getUsername()))
            .when()
                .post(CLIENTE)
            .then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"));
        }
    }

    @Nested
    class BuscarCliente {
        @Test
        void devePermitirBuscarClientePorId() {
            var id = "56833f9a-7fda-49d5-a760-8e1ba41f35a8";
            var userDetails = UserHelper.getUserDetails("umUsuarioQualquer");
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(userDetails.getUsername()))
            .when()
                .get(CLIENTE + "/{id}", id)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/cliente.schema.json"));
            // TODO VERIFICAR A OBRIGATORIEDADE DO ID
        }
        @Test
        void deveGerarExcecao_QuandoBuscarClientePorId_idNaoExiste() {
            var id = ClienteHelper.getCliente(true).getId();
            var userDetails = UserHelper.getUserDetails("umUsuarioQualquer");
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(userDetails.getUsername()))
            .when()
                .get(CLIENTE + "/{id}", id)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void devePermitirBuscarTodosCliente() {
            var userDetails = UserHelper.getUserDetails("umUsuarioQualquer");
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(userDetails.getUsername()))
            .when()
                .get(CLIENTE)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/cliente.page.schema.json"));
        }

        @Test
        void devePermitirBuscarTodosCliente_ComPaginacao() {
            var userDetails = UserHelper.getUserDetails("umUsuarioQualquer");
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            given()
                .queryParam("page", "1")
                .queryParam("size", "1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(userDetails.getUsername()))
            .when()
                .get(CLIENTE)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/cliente.page.schema.json"));
        }
    }

    @Nested
    class AlterarCliente {
        @Test
        void devePermitirAlterarCliente() {
            var userDetails = UserHelper.getUserDetails("umUsuarioQualquer");
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            var cliente = new Cliente(
                    "Kaiby o mestre do miro !!!",
                    "52816804046",
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true)
            );
            cliente.setId(UUID.fromString("ab8fdcd5-c9b5-471e-8ad0-380a65d6cc86"));
            given()
                .body(cliente).contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(userDetails.getUsername()))
            .when()
                .put(CLIENTE + "/{id}", cliente.getId())
            .then()
                .statusCode(HttpStatus.ACCEPTED.value())
                .body(matchesJsonSchemaInClasspath("schemas/cliente.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarCliente_RequisicaoXml() {
            var cliente = ClienteHelper.getCliente(true);
            var userDetails = UserHelper.getUserDetails("umUsuarioQualquer");
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            given()
                .body(cliente).contentType(MediaType.APPLICATION_XML_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(userDetails.getUsername()))
            .when().log().all()
                .put(CLIENTE + "/{id}", cliente.getId())
            .then().log().all()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        }

        @Test
        void deveGerarExcecao_QuandoAlterarClientePorId_idNaoExiste() {
            var userDetails = UserHelper.getUserDetails("umUsuarioQualquer");
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            var cliente = ClienteHelper.getCliente(true);
            given()
                .body(cliente).contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(userDetails.getUsername()))
            .when()
                .put(CLIENTE + "/{id}", cliente.getId())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Cliente não encontrado com o ID: " + cliente.getId()));
        }
    }

    @Nested
    class RemoverCliente {
        @Test
        void devePermitirRemoverCliente() {
            var userDetails = UserHelper.getUserDetails("umUsuarioQualquer");
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            var cliente = new Cliente(
                    "Janaina",
                    "ccc@ddd.com",
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true)
            );
            cliente.setId(UUID.fromString("8855e7b2-77b6-448b-97f8-8a0b529f3976"));
            given()
                    .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(userDetails.getUsername()))
            .when()
                .delete(CLIENTE + "/{id}", cliente.getId())
            .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        void deveGerarExcecao_QuandoRemoverClientePorId_idNaoExiste() {
            var userDetails = UserHelper.getUserDetails("umUsuarioQualquer");
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            var cliente = ClienteHelper.getCliente(true);
            given()
                    .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(userDetails.getUsername()))
            .when()
                .delete(CLIENTE + "/{id}", cliente.getId())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Cliente não encontrado com o ID: " + cliente.getId()));
        }
    }
}
