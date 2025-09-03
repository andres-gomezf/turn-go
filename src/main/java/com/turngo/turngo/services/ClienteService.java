package com.turngo.turngo.services;

import com.turngo.turngo.dtos.ClienteDto;
import com.turngo.turngo.entities.Cliente;
import com.turngo.turngo.entities.Turno;
import com.turngo.turngo.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente save(ClienteDto cliente) {

        Cliente nuevoCliente = new Cliente(cliente.getNombre(), cliente.getApellido(), cliente.getCorreo());

        return clienteRepository.save(nuevoCliente);
    }

    public void delete(Long id) {
        clienteRepository.deleteById(id);
    }
}
