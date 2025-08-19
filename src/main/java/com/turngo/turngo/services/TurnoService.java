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

    public List<Turno> findAll() {
        return turnoRepository.findAll();
    }

    public Optional<Turno> findById(Long id) {
        return turnoRepository.findById(id);
    }

    public Turno save(Turno turno) {
        return turnoRepository.save(turno);
    }

    public void delete(Long id) {
        turnoRepository.deleteById(id);
    }
}