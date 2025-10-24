package com.turngo.turngo.controllers;

import com.turngo.turngo.dtos.ClienteDto;
import com.turngo.turngo.entities.Cliente;
import com.turngo.turngo.services.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
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

    @PostMapping("/find-or-create")
    public ResponseEntity<Cliente> findOrCreate(@Valid @RequestBody ClienteDto cliente) {
        Cliente clienteResult = clienteService.findOrCreateByEmail(cliente);
        return ResponseEntity.ok(clienteResult);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (clienteService.findById(id).isPresent()) {
            clienteService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping("/{id}/assign-user-id")
    public ResponseEntity<Void> assignUserId(@PathVariable Long id, @RequestBody Map<String, Long> body) {
      Long userId = body.get("userId");
      clienteService.assignUserId(id, userId);
      return ResponseEntity.ok().build();
    }
 
}
