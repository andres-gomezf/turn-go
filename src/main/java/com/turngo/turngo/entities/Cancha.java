package com.turngo.turngo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "canchas")
public class Cancha implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "numero_cancha", unique = true)
    private Integer numero;

    @OneToMany(mappedBy = "cancha")
    @JsonIgnore
    private List<Turno> turnos;
}
