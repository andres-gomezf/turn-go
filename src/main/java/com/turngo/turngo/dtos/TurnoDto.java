package com.turngo.turngo.dtos;

import com.turngo.turngo.entities.EstadoReserva;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
public class TurnoDto {

    // TODO: Agregar validaciones como en ClienteDto

    @NotNull(message = "El id del cliente no puede estar vacío. Debe tener un cliente asociado.")
    @Positive(message = "El id del cliente debe ser mayor a 0.")
    private Long clienteId;

    @NotNull(message = "El id de la cancha no puede estar vacío. Debe tener una cancha asociada.")
    @Positive(message = "El id de la cancha debe ser mayor a 0.")
    private Long canchaId;

    @NotNull(message = "El id del horario no puede estar vacío. Debe tener un horario asociado.")
    @Positive(message = "El id del horario debe ser mayor a 0.")
    private Integer horarioId;

    @FutureOrPresent(message = "La fecha debe ser hoy o posterior.")
    @NotNull(message = "La fecha no puede estar vacía.")
    private LocalDate fecha;

}
