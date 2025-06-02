package dsi.ppai.repositories;

import dsi.ppai.entities.Estado;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class RepositorioEstados {

    private final Map<String, Estado> estados = new HashMap<>();

    public RepositorioEstados() {
        estados.put("PENDIENTE", new Estado("OI", "PENDIENTE"));
        estados.put("COMPLETAMENTE_REALIZADA", new Estado("OI","COMPLETAMENTE_REALIZADA"));
        estados.put("CERRADA", new Estado("OI","CERRADA"));
        estados.put("FUERA_DE_SERVICIO", new Estado("SISMOGRAFO","FUERA_DE_SERVICIO"));
        estados.put("ABIERTA", new Estado("OI","ABIERTA"));
        estados.put("EN_MANTENIMIENTO", new Estado("SISMOGRAFO","EN_MANTENIMIENTO"));
    }

    public Estado sosCerrado(String nombre) {

        for (Estado estado : estados.values()) {
            if (estado.sosCerrada() && estado.getNombre().equals(nombre)) {
                return estado;
            }
        }
        return null;
    }

    public Estado sosFueraDeServicio(String nombre) {
        for (Estado estado : estados.values()) {
            if (estado.sosFueraDeServicio() && estado.getNombre().equals(nombre)) {
                return estado;
            }
        }
        return null;
    }


    public Estado buscarEstado(String completamenteRealizada) {
        Estado estado = estados.get(completamenteRealizada);
        if (estado == null) {
            throw new IllegalArgumentException("Estado no encontrado: " + completamenteRealizada);
        }
        return estado;
    }

    public boolean sosAmbitoOI(String nombre) {
        for (Estado estado : estados.values()) {
            if (estado.sosAmbitoOI() && estado.getNombre().equals(nombre)) {
                return true;
            }
        }
        return false;
    }
}