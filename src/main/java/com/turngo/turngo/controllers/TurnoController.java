package com.turngo.turngo.controllers;

import com.turngo.turngo.dtos.CanchaConHorariosDto;
import com.turngo.turngo.dtos.TurnoDto;
import com.turngo.turngo.entities.Turno;
import com.turngo.turngo.services.TurnoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/turnos")
public class TurnoController {

    @Autowired
    private TurnoService turnoService;

    @GetMapping
    public ResponseEntity<List<Turno>> getAll() {
        List<Turno> turnos = turnoService.findAll();
        return ResponseEntity.ok(turnos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Turno> getById(@PathVariable Long id) {
        return turnoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Turno> create(@Valid @RequestBody TurnoDto turno) {
        Turno nuevo = turnoService.save(turno);
        return ResponseEntity.status(201).body(nuevo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (turnoService.findById(id).isPresent()) {
            turnoService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<CanchaConHorariosDto>> getAvailable(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<CanchaConHorariosDto> turnos = turnoService.findAvailableByDate(fecha);
        return ResponseEntity.ok(turnos);
    }
}
