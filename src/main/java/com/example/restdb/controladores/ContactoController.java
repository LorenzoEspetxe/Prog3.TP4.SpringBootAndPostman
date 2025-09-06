package com.example.restdb.controladores;

import com.example.restdb.ORM.ContactoORM;
import com.example.restdb.entidades.Contacto;
import com.example.restdb.repositorios.ContactoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contactos")
public class ContactoController {

    private final ContactoRepository contactoRepository;
    private final ContactoORM contactoORM;

    @Autowired
    public ContactoController(ContactoRepository contactoRepository, ContactoORM contactoORM) {
        this.contactoRepository = contactoRepository;
        this.contactoORM =  contactoORM;
    } // Aunque tenemos constructor, el contenedor IoC (?) controla el ciclo de vida de este objeto, asi como muchos otros.

    // a) Obtener la totalidad de los ítems de la agenda (GET)
    @GetMapping
    public List<Contacto> obtenerContactos() {
        return contactoRepository.findAll();
    }

    // b) Obtener item por ID (GET)
    @GetMapping("/{id}")
    public Optional<Contacto> obtenerContactoPorId(@PathVariable Long id) {
        return contactoRepository.findById(id);
    }

    // c) Obtener contactos por multiples criterios (GET)
    @GetMapping("/buscar")
    public List<Contacto> buscarContactos(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String domicilio) {

        // ¡Aquí usas directamente el método de tu clase ORM!
        return contactoORM.findByFiltro(filtro, email, telefono, domicilio);
    }

    /* Reemplazamos esta manera de hacer el c) a pedido del profe.
    @GetMapping("/buscar")
    public List<Contacto> buscarContactos(@RequestParam String filtro) {
        return contactoRepository.findByNombreOrApellidoLike(filtro);
    } */

    // d) Insertar un nuevo ítem en la agenda (POST)
    @PostMapping
    public Contacto crearContacto(@RequestBody Contacto agenda) {
        return contactoRepository.save(agenda);
    }

    // e) Actualizar un contacto (PUT) / PROFE PIDE CORREGIR!
    // Intento de correccion.
    @PutMapping
    public Contacto actualizarContacto(@RequestBody Contacto contactoActualizado) {
        // El id de contactoActualizado tiene que ser el mismo que le damos en postman.
        // contactoActualizado.setId(id); // Esta linea de codigo deeberia estar de mas
        // Si contactoActualizado tiene un id que ya existe entonces save() lo actualiza.
        // Si no (no existe el id en la DB) save() crea un nuevo registro.
        return contactoRepository.save(contactoActualizado);
    }


    /* Este metodo deberia trabajar solo con el Save, sin tanta logica adentro, como le paso un contacto que ya tiene id
    entonces deberia reconocer que es un put, y  no un post.
    @PutMapping("/{id}")
    public Contacto actualizarContacto(@PathVariable Long id, @RequestBody Contacto contactoActualizado) {
        return contactoRepository.findById(id).map(contacto -> {
            contacto.setNombre(contactoActualizado.getNombre());
            contacto.setApellido(contactoActualizado.getApellido());
            contacto.setTelefono(contactoActualizado.getTelefono());
            contacto.setEmail(contactoActualizado.getEmail());
            contacto.setDomicilio(contactoActualizado.getDomicilio());
            return contactoRepository.save(contacto);
        }).orElseGet(() -> {
            contactoActualizado.setId(id);
            return contactoRepository.save(contactoActualizado);
        });
    }
    */



    // f) Eliminar un contacto (DELETE)
    @DeleteMapping("/{id}")
    public String eliminarContacto(@PathVariable Long id) {
        contactoRepository.deleteById(id);
        return "Contacto con ID " + id + " ha sido eliminado.";
    }
}
