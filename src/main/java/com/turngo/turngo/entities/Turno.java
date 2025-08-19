package com.turngo.turngo.entities;

import jakarta.persistence.*;

import java.io.Serializable;



@Entity
@Table(name = "turnos")
public class Turno implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

}
