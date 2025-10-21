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
    
    // TODO: PROBLEMA IDENTIFICADO - El servicio no maneja la relación con Cancha al crear horarios
    // SOLUCIÓN: Modificar el método save para recibir CanchaService y establecer la relación
    // Ejemplo: 
    // @Autowired
    // private CanchaService canchaService;
    // 
    // public Horario save(HorarioDto horarioDto) {
    //     Cancha cancha = canchaService.findById(horarioDto.getCanchaId())
    //         .orElseThrow(() -> new RuntimeException("Cancha no encontrada"));
    //     Horario horario = new Horario(cancha, horarioDto.getHoraInicio(), horarioDto.getHoraFin());
    //     return horarioRepository.save(horario);
    // }

    public List<Horario> findAll() {
        return horarioRepository.findAll();
    }

    public Optional<Horario> findById(Integer id) {
        return horarioRepository.findById(id);
    }

    public Horario save(Horario horario) {
        return horarioRepository.save(horario);
    }

    public void delete(Integer id) {
        horarioRepository.deleteById(id);
    }
}
