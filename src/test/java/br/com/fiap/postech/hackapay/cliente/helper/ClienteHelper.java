package br.com.fiap.postech.hackapay.cliente.helper;

import br.com.fiap.postech.hackapay.cliente.entities.Cliente;

import java.util.UUID;

public class ClienteHelper {
    public static Cliente getCliente(boolean geraId) {
        var cliente = new Cliente(
                "Anderson Wagner",
                "25310413030",
                "anderson.wagner@gmail.com",
                "11999999999",
                "rua de ferias, 234",
                "12345-123",
                "Nurburg",
                "NR",
                "Alemanha"
        );
        if (geraId) {
            cliente.setId(UUID.randomUUID());
        }
        return cliente;
    }
}
