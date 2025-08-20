package com.turngo.turngo.controllers;

import com.turngo.turngo.entities.Cancha;
import com.turngo.turngo.entities.Cliente;
import com.turngo.turngo.services.CanchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/canchas")
public class CanchaController {

    @Autowired
    private CanchaService canchaService;

    @GetMapping
    public ResponseEntity<List<Cancha>> getAll() {
        List<Cancha> canchas = canchaService.findAll();
        return ResponseEntity.ok(canchas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cancha> getById(@PathVariable Long id) {
        return canchaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cancha> create(@RequestBody Cancha cancha) {
        Cancha nuevaCancha = canchaService.save(cancha);
        return ResponseEntity.status(201).body(nuevaCancha);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (canchaService.findById(id).isPresent()) {
            canchaService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
