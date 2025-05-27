package dsi.ppai.repositories;

import dsi.ppai.entities.Rol; // Asegúrate de importar la clase Rol
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class RepositorioRol {

    private final List<Rol> roles = new ArrayList<>();

    public RepositorioRol() {
        // La inicialización real se hará desde DataInitializerService.
    }

    /**
     * Guarda un objeto Rol en el repositorio.
     * Si un rol con el mismo nombre ya existe, no lo añade nuevamente.
     * @param rol El objeto Rol a guardar.
     */
    public void guardar(Rol rol) {
        Optional<Rol> existingRol = roles.stream()
                .filter(r -> r.getNombre().equalsIgnoreCase(rol.getNombre()))
                .findFirst();
        if (existingRol.isPresent()) {
            System.out.println("DEBUG (RepositorioRol): Rol '" + rol.getNombre() + "' ya existe. No se añadió duplicado.");
        } else {
            roles.add(rol);
            System.out.println("DEBUG (RepositorioRol): Rol guardado: " + rol.getNombre());
        }
    }

    /**
     * Busca un rol por su nombre.
     * @param nombre El nombre del rol a buscar.
     * @return El objeto Rol si se encuentra, o null si no existe.
     */
    public Rol buscarRol(String nombre) {
        return roles.stream()
                .filter(rol -> rol.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retorna todos los roles almacenados.
     * @return Una lista de todos los objetos Rol.
     */
    public List<Rol> findAll() {
        return new ArrayList<>(roles); // Devuelve una copia para evitar modificaciones externas
    }
}