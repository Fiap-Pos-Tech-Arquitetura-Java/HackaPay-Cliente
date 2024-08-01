package br.com.fiap.postech.hackapay.cliente.controller;

import br.com.fiap.postech.hackapay.cliente.entities.Cliente;
import br.com.fiap.postech.hackapay.cliente.services.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Operation(summary = "registra um cliente")
    @PostMapping
    public ResponseEntity<Cliente> save(@Valid @RequestBody Cliente clienteDTO) {
        Cliente savedClienteDTO = clienteService.save(clienteDTO);
        return new ResponseEntity<>(savedClienteDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "lista todos os clientes")
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<Cliente>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf
    ) {
        Cliente cliente = new Cliente(nome, cpf, null, null, null, null, null, null, null);
        cliente.setId(null);
        var pageable = PageRequest.of(page, size);
        var clientes = clienteService.findAll(pageable, cliente);
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    @Operation(summary = "lista um cliente por seu id")
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        try {
            Cliente cliente = clienteService.findById(id);
            return ResponseEntity.ok(cliente);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "altera um cliente por seu id")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody Cliente clienteDTO) {
        try {
            Cliente updatedCliente = clienteService.update(id, clienteDTO);
            return new ResponseEntity<>(updatedCliente, HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "remove um cliente por seu id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            clienteService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException
                exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
