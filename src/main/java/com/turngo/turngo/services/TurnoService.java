package com.turngo.turngo.services;

import com.turngo.turngo.entities.Turno;
import com.turngo.turngo.repositories.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TurnoService {

    @Autowired
    private TurnoRepository turnoRepository;
    @Autowired
    private CanchaService canchaService;
    @Autowired
    private ClienteService clienteService;

    public List<Turno> findAll() {
        return turnoRepository.findAll();
    }

    public Optional<Turno> findById(Long id) {
        return turnoRepository.findById(id);
    }

    public Turno save(Turno turno) {
        Turno savedTurno = turnoRepository.save(turno);

        savedTurno.setCancha(
                canchaService.findById(turno.getCancha().getId()).orElseThrow(() -> new RuntimeException("Cancha no encontrada"))
        );

        savedTurno.setCliente(
                clienteService.findById(turno.getCliente().getId()).orElseThrow(() -> new RuntimeException("Cliente no encontrado"))
        );

        return savedTurno;
    }

    public void delete(Long id) {
        turnoRepository.deleteById(id);
    }
}