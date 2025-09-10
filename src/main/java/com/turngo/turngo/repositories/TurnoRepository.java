package com.turngo.turngo.repositories;

import com.turngo.turngo.dtos.HorarioFlatDto;
import com.turngo.turngo.entities.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    @Query(value = """
        SELECT
               h.id AS horarioId,
               h.hora_inicio AS horaInicio,
               h.hora_fin AS horaFin,
               c.id AS canchaId,
               c.numero_cancha AS numeroCancha
        FROM horarios h
        JOIN canchas c ON h.cancha_id = c.id
        LEFT JOIN turnos t 
          ON h.id = t.horario_id 
         AND t.fecha = :fecha
        WHERE t.id IS NULL
        """, nativeQuery = true)
    List<HorarioFlatDto> findAvailableByDate(@Param("fecha") LocalDate fecha);
}
