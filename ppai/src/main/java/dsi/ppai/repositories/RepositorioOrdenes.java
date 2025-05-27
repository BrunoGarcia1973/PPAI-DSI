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
    private final AtomicLong nextId = new AtomicLong(1);

    public void insertar(OrdenDeInspeccion orden) {
        if (orden.getNumOrden() == null) {
            orden.setNumOrden(nextId.getAndIncrement());
        }
        ordenes.put(orden.getNumOrden(), orden);
    }

    public OrdenDeInspeccion buscarOrdenDeInspeccion(Long numeroOrden) {
        return ordenes.get(numeroOrden);
    }

    public List<OrdenDeInspeccion> buscarOrdenesInspeccionDeRI(String legajoRI) {
        return ordenes.values().stream()
                .filter(o -> o.getEmpleado() != null && o.getEmpleado().getLegajo().equals(legajoRI))
                .collect(Collectors.toList());
    }

    public List<OrdenDeInspeccion> findAll() {
        return new ArrayList<>(ordenes.values());
    }
}