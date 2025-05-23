package dsi.ppai.repositories;

import dsi.ppai.entities.OrdenDeInspeccion;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RepositorioOrdenes {

    private final Map<Long, OrdenDeInspeccion> ordenes = new HashMap<>();

    public void insertar(OrdenDeInspeccion orden) {
        ordenes.put(orden.getNumeroOrden(), orden);
    }

    public OrdenDeInspeccion buscarOrdenDeInspeccion(Long numeroOrden) {
        return ordenes.get(numeroOrden);
    }

    public List<OrdenDeInspeccion> buscarTodasLasOrdenes() {
        return new ArrayList<>(ordenes.values());
    }

    // Método para buscar órdenes de inspección por el legajo del Responsable de Inspección
    public List<OrdenDeInspeccion> buscarOrdenesInspeccionDeRI(String legajoRI) {
        return ordenes.values().stream()
                // Corrección: usar getResponsableInspeccion() y getLegajo()
                .filter(orden -> orden.getResponsableInspeccion() != null &&
                        orden.getResponsableInspeccion().getLegajo().equals(legajoRI))
                .collect(Collectors.toList());
    }
}