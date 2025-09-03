package com.turngo.turngo.services;

import com.turngo.turngo.dtos.TurnoDto;
import com.turngo.turngo.entities.*;
import com.turngo.turngo.exceptions.TurnoNoDisponibleException;
import com.turngo.turngo.repositories.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    @Autowired
    private HorarioService horarioService;

    public List<Turno> findAll() {
        return turnoRepository.findAll();
    }

    public Optional<Turno> findById(Long id) {
        return turnoRepository.findById(id);
    }

    public Turno save(TurnoDto turnoDto) {

            Cliente cliente = this.clienteService.findById(turnoDto.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            Horario horario = this.horarioService.findById(turnoDto.getHorarioId())
                    .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

            if (!esAlquilable(horario, turnoDto.getFecha())) {
                throw new TurnoNoDisponibleException("El turno ya está ocupado");
            }

            Turno turno = new Turno(cliente, horario, turnoDto.getFecha(), EstadoReserva.PENDIENTE);

            return turnoRepository.save(turno);
    }

    public void delete(Long id) {
        turnoRepository.deleteById(id);
    }

    public boolean esAlquilable(Horario horario, LocalDate fecha) {
        return this.findAll().stream().noneMatch(
                turno -> turno.getHorario().equals(horario) &&
                turno.getFechaInicio().equals(fecha));
    }
}