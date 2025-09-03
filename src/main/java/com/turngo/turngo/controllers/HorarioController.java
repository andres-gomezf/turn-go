package com.turngo.turngo.controllers;

import com.turngo.turngo.entities.Horario;
import com.turngo.turngo.services.HorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/horarios")
public class HorarioController {

    @Autowired
    private HorarioService horarioService;

    @GetMapping
    public ResponseEntity<List<Horario>> getAll() {
        List<Horario> horarios = horarioService.findAll();
        return ResponseEntity.ok(horarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Horario> getById(@PathVariable Integer id) {
        return horarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Horario> create(@RequestBody Horario horario) {
        Horario nuevo = horarioService.save(horario);
        return ResponseEntity.status(201).body(nuevo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (horarioService.findById(id).isPresent()) {
            horarioService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
