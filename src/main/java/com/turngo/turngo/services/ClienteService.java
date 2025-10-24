package com.turngo.turngo.services;

import com.turngo.turngo.dtos.ClienteDto;
import com.turngo.turngo.entities.Cliente;
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

    public List<Cliente> findByEmail(String email) {
        return clienteRepository.findByCorreo(email);
    }

    public Cliente findOrCreateByEmail(ClienteDto clienteDto) {
        List<Cliente> clientesExistentes = clienteRepository.findByCorreo(clienteDto.getCorreo());
        
        if (!clientesExistentes.isEmpty()) {
            // Cliente ya existe, retornar el primero encontrado
            return clientesExistentes.get(0);
        } else {
            // Cliente no existe, crear uno nuevo
            return save(clienteDto);
        }
    }
}
