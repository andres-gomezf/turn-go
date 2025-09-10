package com.turngo.turngo.dtos;

import java.util.List;

public record CanchaConHorariosDto(
        Long canchaId,
        String numeroCancha,
        List<HorarioDto> horarios
) {}
