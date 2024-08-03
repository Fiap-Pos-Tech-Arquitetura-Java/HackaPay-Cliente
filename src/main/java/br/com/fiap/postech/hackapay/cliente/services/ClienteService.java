package br.com.fiap.postech.hackapay.cliente.services;

import br.com.fiap.postech.hackapay.cliente.entities.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ClienteService {
    Cliente save(Cliente cliente);

    Page<Cliente> findAll(Pageable pageable, Cliente cliente);

    Cliente findById(UUID id);

    Cliente update(UUID id, Cliente cliente);

    void delete(UUID id);

    Cliente findByCpf(String cpf);
}
