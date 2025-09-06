package com.example.restdb.ORM;

import com.example.restdb.entidades.Contacto;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ContactoORM {
    @PersistenceContext
    private EntityManager em;

    // En este metodo voy a poner el parametro telefono como String porque la consulta JPA puede manejar una comparacion entre String y Long, y la IA lo recomienda, siempre y cuando el fomato de String sea numerico.
    // Dice que recibir el recibir el teléfono como String en el Controller te da mayor robustez y control sobre los datos que el usuario puede enviar, permitiéndote manejar errores y formatos de manera explícita.
    public List<Contacto> findByFiltro(String filtro, String email, String telefono, String domicilio) {

        // Como los String son inmutables en Java, usamos StringBuilder para no usar memoria al pedo, esto se hace cuando va a modificarse mas de una vez el String.
        StringBuilder jpql = new StringBuilder("");
        jpql.append("SELECT c FROM Contacto c WHERE 1=1");
        // Esto siempre sera verdadero, es la base de la consulta: select contacto from contacto.

        // En todos los if, agrego !parametro.isEmpty() por recomendacion de la IA.
        // Asegura que solo se intente buscar por el parametro si realmente se busco algo.
        if(filtro != null && !filtro.trim().isEmpty()){ // si hay un parametro filtro entonces concatenamos esta parte:
            jpql.append(" AND (LOWER(c.nombre) LIKE LOWER(:filtro) OR LOWER(c.apellido) LIKE LOWER(:filtro))");
        }
        if(email != null && !email.trim().isEmpty()){ // si hay un parametro email entonces concatenamos esta parte:
            jpql.append(" AND LOWER(c.email) LIKE LOWER(:email)"); //
        }
        if(telefono != null && !telefono.trim().isEmpty()){ // same para telefono:
            jpql.append(" AND c.telefono = :telefono"); //
        }
        if(domicilio != null && !domicilio.trim().isEmpty()){ // same para domicilio:
            jpql.append(" AND LOWER(c.domicilio) LIKE LOWER(:domicilio)"); //
        }


        TypedQuery<Contacto> query = em.createQuery(jpql.toString(), Contacto.class);

        if(filtro != null && !filtro.trim().isEmpty()){ // si hay un parametro filtro entonces lo seteamos como parametro:
            query.setParameter("filtro", "%" + filtro + "%");
        }
        if(email != null && !email.trim().isEmpty()){ // lo mismo con email:
            query.setParameter("email", "%" + email + "%");
        }
        if(telefono != null && !telefono.trim().isEmpty()){ // lo mismo con felefono:
            query.setParameter("telefono", Long.parseLong(telefono));
        }
        if(domicilio != null && !domicilio.trim().isEmpty()){ // lo mismo con email:
            query.setParameter("domicilio", "%" + domicilio + "%");
        }
        return query.getResultList(); // retornamos el resulta
    }
}