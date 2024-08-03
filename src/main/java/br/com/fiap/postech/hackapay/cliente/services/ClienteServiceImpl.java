package br.com.fiap.postech.hackapay.cliente.services;

import br.com.fiap.postech.hackapay.cliente.entities.Cliente;
import br.com.fiap.postech.hackapay.cliente.repository.ClienteRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Cliente save(Cliente cliente) {
        if (clienteRepository.findByCpf(cliente.getCpf()).isPresent()) {
            throw new IllegalArgumentException("Já existe um cliente cadastrado com esse cpf.");
        }
        cliente.setId(UUID.randomUUID());
        return clienteRepository.save(cliente);
    }

    @Override
    public Page<Cliente> findAll(Pageable pageable, Cliente cliente) {
        Example<Cliente> clienteExample = Example.of(cliente);
        return clienteRepository.findAll(clienteExample, pageable);
    }

    @Override
    public Cliente findById(UUID id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com o ID: " + id));
    }

    @Override
    public Cliente findByCpf(String cpf) {
        return clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com o cpf: " + cpf));
    }

    @Override
    public Cliente update(UUID id, Cliente clienteParam) {
        Cliente cliente = findById(id);
        if (StringUtils.isNotEmpty(clienteParam.getNome())) {
            cliente.setNome(clienteParam.getNome());
        }
        if (clienteParam.getId() != null && !cliente.getId().equals(clienteParam.getId())) {
            throw new IllegalArgumentException("Não é possível alterar o id de um cliente.");
        }
        if (clienteParam.getCpf() != null && !cliente.getCpf().equals(clienteParam.getCpf())) {
            throw new IllegalArgumentException("Não é possível alterar o cpf de um cliente.");
        }
        if (StringUtils.isNotEmpty(clienteParam.getNome())) {
            cliente.setNome(clienteParam.getNome());
        }
        cliente = clienteRepository.save(cliente);
        return cliente;
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        clienteRepository.deleteById(id);
    }
}
