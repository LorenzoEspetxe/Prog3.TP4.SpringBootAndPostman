package com.example.restdb.repositorios;

import com.example.restdb.entidades.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/*
La interfaz ContactoRepository que extiende de JpaRepository ya tiene los métodos predefinidos para usar:

findAll(): se encarga del punto (a) Obtener la totalidad de ítems.
findById(Long id): se encarga del punto (b) Obtener un ítem específico por id.
save(Contacto contacto): se encarga del punto (d) Insertar un nuevo ítem (si el ID no existe), y el (e) Actualizar un ítem existente (con un ID existente).
deleteById(Long id): se encarga del punto (f) Eliminar un ítem.

Con solo una línea de código (extends JpaRepository<Contacto, Long>), ya tienes las funcionalidades básicas de CRUD implementadas.
 */

@Repository // componente de repositorio, que se encarga de la comunicación con la base de datos.
public interface ContactoRepository extends JpaRepository<Contacto, Long> {

    // Esta consulta es estatica, armarla flexible

    // c) Obtener ítems que coincidan parcialmente con nombre o apellido (usando JPQL)

    @Query("SELECT a FROM Contacto a WHERE LOWER(a.nombre) LIKE LOWER(CONCAT('%', :filtro, '%')) OR LOWER(a.apellido) LIKE LOWER(CONCAT('%', :filtro, '%'))")
    public List<Contacto> findByNombreOrApellidoLike(@Param("filtro") String filtro); // Este es un metodo, pero no es necesario escribir public porque en una interfaz de por si son publicos los metodos.

    // sintaxis Spring: find + By + [nombreCampo] + [operador].
    // @Query(consulta) tipoRetorno<> nombreMetodo (@Param() tipoArgumento argumento)

    // Request del profe:
    // Crear una clase contactoORM donde uso entityManager y EntityManagerFactory, de manera que le pasemos un String, y con ese string armar el query.


}