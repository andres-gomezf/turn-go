package com.turngo.turngo.dtos;

import java.time.LocalTime;

public record HorarioDto(
        Long horarioId,
        LocalTime horaInicio,
        LocalTime horaFin
) {}