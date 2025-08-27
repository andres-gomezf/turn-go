package com.turngo.turngo.services;

import com.turngo.turngo.entities.Horario;
import com.turngo.turngo.repositories.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HorarioService {

    @Autowired
    private HorarioRepository horarioRepository;

    public List<Horario> findAll() {
        return horarioRepository.findAll();
    }

    public Optional<Horario> findById(Long id) {
        return horarioRepository.findById(id);
    }

    public Horario save(Horario horario) {
        return horarioRepository.save(horario);
    }

    public void delete(Long id) {
        horarioRepository.deleteById(id);
    }
}
