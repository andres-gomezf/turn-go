package com.turngo.turngo.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity()
@Table(name = "Clientes")
public class Cliente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    @Basic(optional = true)
    private String apellido;

    @Column(name = "correo")
    @Basic(optional = true)
    private String correo;

    @OneToMany(mappedBy = "cliente")
    private List<Turno> turnos;
}
