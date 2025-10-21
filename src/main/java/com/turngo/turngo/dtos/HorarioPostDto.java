package com.turngo.turngo.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
@Getter
@Setter
public class HorarioPostDto {

    @NotNull(message = "El id de la cancha no puede estar vacío. Debe tener una cancha asociada.")
    @Positive(message = "El id de la cancha debe ser mayor a 0.")
    private Long canchaId;
    
    @NotNull(message = "La hora de inicio no puede estar vacía.")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de finalizacion no puede estar vacía.")
    private LocalTime horaFin;

}
