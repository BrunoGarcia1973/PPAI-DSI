package dsi.ppai.repositories;

import dsi.ppai.entities.OrdenDeInspeccion;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class RepositorioOrdenes {

    private final Map<Long, OrdenDeInspeccion> ordenes = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong(1); // Para asignar IDs si no vienen

    /**
     * Inserta una nueva orden o actualiza una existente si ya tiene un numOrden.
     */
    public void insertar(OrdenDeInspeccion orden) { // Mantener el nombre 'insertar'
        if (orden.getNumOrden() == null) {
            orden.setNumOrden(nextId.getAndIncrement());
        }
        ordenes.put(orden.getNumOrden(), orden);
    }

    public OrdenDeInspeccion buscarOrdenDeInspeccion(Long numeroOrden) {
        return ordenes.get(numeroOrden);
    }

    public List<OrdenDeInspeccion> buscarOrdenesInspeccionDeRI(String legajoRI) {
        // Esto asume que el legajo del RI está en la OrdenDeInspeccion.empleado.legajo
        return ordenes.values().stream()
                .filter(o -> o.getEmpleado() != null && o.getEmpleado().getLegajo().equals(legajoRI))
                .collect(Collectors.toList());
    }

    /**
     * Devuelve una lista con todas las órdenes de inspección cargadas en el repositorio.
     * Renombrado de 'buscarTodos()' a 'findAll()' para mayor consistencia.
     */
    public List<OrdenDeInspeccion> findAll() {
        return new ArrayList<>(ordenes.values());
    }
}