package dsi.ppai.repositories;

import dsi.ppai.entities.Estado;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RepositorioEstados {

    private final Map<String, Estado> estados = new HashMap<>();

    public RepositorioEstados() {
        // Simulación de estados predefinidos
        estados.put("PENDIENTE", new Estado("PENDIENTE"));
        estados.put("CompletamenteRealizada", new Estado("CompletamenteRealizada"));
        estados.put("CERRADA", new Estado("CERRADA"));
        estados.put("FUERA DE SERVICIO", new Estado("FUERA DE SERVICIO"));
        estados.put("ABIERTA", new Estado("ABIERTA"));
    }

    public Estado buscarEstado(String nombre) {
        return estados.get(nombre);
    }
}
