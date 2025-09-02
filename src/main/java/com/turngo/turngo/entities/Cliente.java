package com.turngo.turngo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.List;

@Entity()
@Table(name = "clientes")
@Getter
@Setter
public class Cliente implements Serializable {

    public Cliente(Long id, String nombre, String apellido, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
    }

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
    @JsonIgnore
    private List<Turno> turnos;
}
