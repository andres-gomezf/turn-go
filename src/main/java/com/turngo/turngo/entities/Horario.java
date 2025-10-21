package com.turngo.turngo.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "Horarios")
public class Horario {

    public Horario(){}

    public Horario(Cancha cancha, LocalTime horaInicio, LocalTime horaFin) {
    this.cancha = cancha;
    this.horaInicio = horaInicio;
    this.horaFin = horaFin;
  }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(optional = false)
    @JsonIgnore
    @JoinColumn(name = "cancha_id")
    private Cancha cancha;
    
    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaFin;
}
