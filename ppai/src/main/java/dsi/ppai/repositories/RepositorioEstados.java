package dsi.ppai.repositories;

import dsi.ppai.entities.Estado;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class RepositorioEstados {

    private final Map<String, Estado> estados = new HashMap<>();

    public RepositorioEstados() {
        estados.put("PENDIENTE", new Estado("PENDIENTE"));
        estados.put("COMPLETAMENTE_REALIZADA", new Estado("COMPLETAMENTE_REALIZADA"));
        estados.put("CERRADA", new Estado("CERRADA"));
        estados.put("FUERA_DE_SERVICIO", new Estado("FUERA_DE_SERVICIO"));
        estados.put("ABIERTA", new Estado("ABIERTA"));
        estados.put("EN_MANTENIMIENTO", new Estado("EN_MANTENIMIENTO"));
    }

    public Estado buscarEstado(String nombre) {
        return estados.get(nombre);
    }
}