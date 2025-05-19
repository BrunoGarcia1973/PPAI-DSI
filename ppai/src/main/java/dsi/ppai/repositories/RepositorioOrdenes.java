package dsi.ppai.repositories;

import dsi.ppai.entities.OrdenDeInspeccion;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RepositorioOrdenes {

    private final Map<Long, OrdenDeInspeccion> ordenes = new HashMap<>();

    public void insertar(OrdenDeInspeccion orden) {
        ordenes.put(orden.getNumOrden(), orden);
    }

    public OrdenDeInspeccion buscarOrdenDeInspeccion(Long numeroOrden) {
        return ordenes.get(numeroOrden);
    }

    public List<OrdenDeInspeccion> buscarOrdenesInspeccionDeRI(String legajoEmpleado) {
        return ordenes.values().stream()
                .filter(o -> o.getEmpleado().getLegajo().equals(legajoEmpleado))
                .collect(Collectors.toList());
    }
}
