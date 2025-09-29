package com.example.restdb.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getter, setter, toString, hashCode, equals.
@NoArgsConstructor

@Entity // Mapea la entidad como una tabla en la base de datos.
public class Contacto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Define id como la PK, que se autogenera.
    private Long id;
    private String nombre;
    private String apellido;
    private Long telefono;
    private String email;
    private String domicilio;

    public Contacto(String nombre, String apellido, Long telefono, String email, String domicilio) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.domicilio = domicilio;
    }
}
