package dsi.ppai.repositories;

import dsi.ppai.entities.OrdenDeInspeccion;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RepositorioOrdenes {

    private Map<Long, OrdenDeInspeccion> ordenes = new HashMap<>();

    public void agregarOrden(OrdenDeInspeccion orden) {
        ordenes.put(orden.getNumOrden(), orden);
    }

    public OrdenDeInspeccion buscarOrdenesInspeccion(Long numOrden) {
        return ordenes.get(numOrden);
    }
}
