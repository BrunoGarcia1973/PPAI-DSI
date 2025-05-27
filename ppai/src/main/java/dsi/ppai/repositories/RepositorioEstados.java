package dsi.ppai.repositories;

import dsi.ppai.entities.Estado; // Asegúrate de importar la clase Estado
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Para buscar elementos

@Repository
// @Data // No es necesario para un repositorio en memoria, ya que los datos se gestionan internamente
public class RepositorioEstados {

    private final List<Estado> estados = new ArrayList<>();

    // Constructor vacío o sin argumentos si no necesitas inicializar estados aquí.
    // La inicialización se hará desde DataInitializerService.
    public RepositorioEstados() {
        // En una aplicación real, aquí podrías cargar desde una base de datos.
    }

    /**
     * Guarda un objeto Estado en el repositorio.
     * Si un estado con el mismo nombre ya existe, lo actualiza (opcional, o podrías no permitir duplicados).
     * @param estado El objeto Estado a guardar.
     */
    public void guardar(Estado estado) {
        // Aquí puedes decidir la lógica:
        // Opción A: Simplemente añade el estado (permitiendo duplicados si el nombre no es la clave única)
        // estados.add(estado);

        // Opción B (más robusta): Evita duplicados o actualiza si ya existe por nombre
        Optional<Estado> existingEstado = estados.stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(estado.getNombre()))
                .findFirst();
        if (existingEstado.isPresent()) {
            // Podrías actualizar el estado existente si hay más propiedades
            // Por simplicidad, si ya existe por nombre, no lo añadimos de nuevo.
            System.out.println("DEBUG (RepositorioEstados): Estado '" + estado.getNombre() + "' ya existe. No se añadió duplicado.");
        } else {
            estados.add(estado);
            System.out.println("DEBUG (RepositorioEstados): Estado guardado: " + estado.getNombre());
        }
    }

    /**
     * Busca un estado por su nombre.
     * @param nombre El nombre del estado a buscar.
     * @return El objeto Estado si se encuentra, o null si no existe.
     */
    public Estado buscarEstado(String nombre) {
        return estados.stream()
                .filter(estado -> estado.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retorna todos los estados almacenados.
     * @return Una lista de todos los objetos Estado.
     */
    public List<Estado> findAll() {
        return new ArrayList<>(estados); // Devuelve una copia para evitar modificaciones externas
    }
}
