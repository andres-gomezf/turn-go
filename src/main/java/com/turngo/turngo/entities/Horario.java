package com.turngo.turngo.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "Horarios")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cancha_id")
    private Cancha cancha;

    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaFin;
}
