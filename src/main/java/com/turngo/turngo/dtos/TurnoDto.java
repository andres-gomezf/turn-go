package com.turngo.turngo.dtos;

import com.turngo.turngo.entities.EstadoReserva;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TurnoDto {

    private Long clienteId;
    private Long canchaId;
    private Integer horarioId;
    private LocalDate fecha;

}
