package dsi.ppai.repositories;

import dsi.ppai.entities.Empleado;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio en memoria para gestionar objetos Empleado.
 * Simula el comportamiento de una base de datos para pruebas.
 */
@Repository // Anotación de Spring para indicar que es un componente de repositorio
public class RepositorioEmpleado {
    // Lista en memoria para almacenar los objetos Empleado
    private final List<Empleado> empleados = new ArrayList<>();

    /**
     * Guarda un objeto Empleado en la lista en memoria.
     * Si ya existe un empleado con el mismo legajo, lo actualiza.
     * Si no existe, lo añade a la lista.
     * @param empleado El objeto Empleado a guardar.
     */
    public void guardar(Empleado empleado) {
        // Busca si ya existe un empleado con el mismo legajo
        Optional<Empleado> existingEmpleado = empleados.stream()
                .filter(e -> e.getLegajo().equals(empleado.getLegajo()))
                .findFirst();

        if (existingEmpleado.isPresent()) {
            // Si existe, lo actualiza (reemplaza en la misma posición)
            int index = empleados.indexOf(existingEmpleado.get());
            empleados.set(index, empleado);
            System.out.println("DEBUG (RepositorioEmpleado): Empleado actualizado en memoria: " + empleado.getLegajo());
        } else {
            // Si no existe, lo añade a la lista
            empleados.add(empleado);
            System.out.println("DEBUG (RepositorioEmpleado): Nuevo empleado guardado en memoria: " + empleado.getLegajo());
        }
    }

    /**
     * Busca un objeto Empleado en la lista por su legajo.
     * @param legajo El legajo del empleado a buscar.
     * @return El objeto Empleado si se encuentra, o null si no existe.
     */
    public Empleado buscarPorLegajo(String legajo) {
        return empleados.stream()
                .filter(e -> e.getLegajo().equals(legajo))
                .findFirst()
                .orElse(null); // Retorna null si no lo encuentra
    }

    /**
     * Retorna una lista con todos los objetos Empleado almacenados.
     * Se devuelve una nueva ArrayList para evitar modificaciones directas de la lista interna.
     * @return Una lista de todos los Empleado.
     */
    public List<Empleado> findAll() {
        return new ArrayList<>(empleados);
    }
}