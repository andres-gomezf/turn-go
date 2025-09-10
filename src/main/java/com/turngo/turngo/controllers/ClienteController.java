package com.turngo.turngo.controllers;

import com.turngo.turngo.dtos.ClienteDto;
import com.turngo.turngo.entities.Cliente;
import com.turngo.turngo.entities.Turno;
import com.turngo.turngo.services.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<Cliente>> getAll(@RequestParam(required = false, name = "email") String email) {

        if (email == null) {
            List<Cliente> clientes = clienteService.findAll();
            return ResponseEntity.ok(clientes);
        }

        List<Cliente> cliente = clienteService.findByEmail(email);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getById(@PathVariable Long id) {
        return clienteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cliente> create(@Valid @RequestBody ClienteDto cliente) {
        Cliente nuevoCliente = clienteService.save(cliente);
        return ResponseEntity.status(201).body(nuevoCliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (clienteService.findById(id).isPresent()) {
            clienteService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
