package dsi.ppai.repositories;

import dsi.ppai.entities.Estado;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RepositorioEstados {

    private final Map<String, Estado> estados = new HashMap<>();

    @PostConstruct
    public void init() {
        System.out.println("Inicializando RepositorioEstados...");
        // Precargar estados con los nombres exactos que se esperan en DatosInicialesService
        estados.put("ABIERTA", new Estado("ABIERTA", "La orden está en curso de inspección."));
        estados.put("CERRADA", new Estado("CERRADA", "La orden ha sido finalizada."));
        estados.put("COMPLETAMENTE_REALIZADA", new Estado("COMPLETAMENTE_REALIZADA", "La inspección se completó sin problemas."));
        estados.put("FUERA DE SERVICIO", new Estado("FUERA DE SERVICIO", "El sismógrafo está inactivo debido a una falla."));
        estados.put("EN_MANTENIMIENTO", new Estado("EN_MANTENIMIENTO", "El sismógrafo está siendo revisado o reparado."));
        // Asegúrate de que todos los estados que uses en tu aplicación estén aquí.

        System.out.println("Estados precargados: " + estados.keySet());
    }

    public Estado buscarEstado(String nombre) {
        return estados.get(nombre);
    }
}