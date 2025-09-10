package com.turngo.turngo.dtos;

import java.time.LocalTime;

public interface HorarioFlatDto {
    Long getHorarioId();
    LocalTime getHoraInicio();
    LocalTime getHoraFin();
    Long getCanchaId();
    String getNumeroCancha();
}
