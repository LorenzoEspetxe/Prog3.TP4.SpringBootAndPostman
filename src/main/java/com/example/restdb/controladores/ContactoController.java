package com.example.restdb.controladores;

import com.example.restdb.entidades.Contacto;
import com.example.restdb.repositorios.ContactoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contactos")
public class ContactoController {

    @Autowired
    private ContactoRepository contactoRepository;

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

    // c) Obtener contactos por nombre o apellido (GET)
    @GetMapping("/buscar")
    public List<Contacto> buscarContactos(@RequestParam String filtro) {
        return contactoRepository.findByNombreOrApellidoLike(filtro);
    }

    // d) Insertar un nuevo ítem en la agenda (POST)
    @PostMapping
    public Contacto crearContacto(@RequestBody Contacto agenda) {
        return contactoRepository.save(agenda);
    }

    // e) Actualizar un contacto (PUT)
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

    // f) Eliminar un contacto (DELETE)
    @DeleteMapping("/{id}")
    public String eliminarContacto(@PathVariable Long id) {
        contactoRepository.deleteById(id);
        return "Contacto con ID " + id + " ha sido eliminado.";
    }
}
