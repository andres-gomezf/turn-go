package com.turngo.turngo.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "Canchas")
public class Cancha implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "numero_cancha", unique = true)
    private Integer numero;

    @OneToMany(mappedBy = "cancha")
    private List<Turno> turnos;
}
