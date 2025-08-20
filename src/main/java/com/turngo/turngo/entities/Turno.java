package com.turngo.turngo.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "turnos")
@Getter
@Setter
public class Turno implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = true)  // Al tener optional, un turno puede o no tener un cliente para ser creado.
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cancha_id")
    private Cancha cancha;

    @Column(name = "fecha_hora_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_hora_fin")
    private LocalDateTime fechaFin;
}
