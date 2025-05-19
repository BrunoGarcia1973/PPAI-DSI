package dsi.ppai.repositories;

import dsi.ppai.entities.OrdenDeInspeccion;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class RepositorioOrdenes {

    private Map<Long, OrdenDeInspeccion> ordenes = new HashMap<>();

    public void agregarOrden(OrdenDeInspeccion orden) {
        ordenes.put(orden.getNumOrden(), orden);
    }

    public OrdenDeInspeccion buscarOrdenDeInspeccion(Long numeroOrden) {
        return ordenes.get(numeroOrden);
    }

    public List<OrdenDeInspeccion> obtenerTodasLasOrdenes() {
        return List.copyOf(ordenes.values());
    }

    /**
     * Nombre según diagrama: buscarOrdenesInspeccionDeRI
     * Filtra en memoria las órdenes cuyo empleado-RI (o “responsableInspeccion”)
     * tenga el legajo dado.
     */
    public List<OrdenDeInspeccion> buscarOrdenesInspeccionDeRI(String legajo) {
        return ordenes.values()
                .stream()
                .filter(o -> Objects.equals(o.getEmpleado().getLegajo(), legajo))
                .collect(Collectors.toList());
    }

    public void add(OrdenDeInspeccion orden) {
        // Verifica si la orden ya existe
        if (ordenes.containsKey(orden.getNumOrden())) {
            throw new IllegalArgumentException("La orden ya existe: " + orden.getNumOrden());
        }
        // Agrega la nueva orden
        ordenes.put(orden.getNumOrden(), orden);
    }
}
