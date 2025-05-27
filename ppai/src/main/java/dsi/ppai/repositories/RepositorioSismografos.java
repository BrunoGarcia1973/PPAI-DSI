package dsi.ppai.repositories;

import dsi.ppai.entities.Sismografo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger; // Importante: cambiamos a AtomicInteger

@Component
public class RepositorioSismografos {

    private final Map<Integer, Sismografo> sismografos = new HashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public void guardar(Sismografo sismografo) {
        if (sismografo.getIdentificadorSismografo() == null) {
            sismografo.setIdentificadorSismografo(nextId.getAndIncrement());
        }
        sismografos.put(sismografo.getIdentificadorSismografo(), sismografo);
    }

    public Sismografo buscar(Integer id) { // Buscar por Integer
        return sismografos.get(id);
    }

    public List<Sismografo> buscarTodos() {
        return new ArrayList<>(sismografos.values());
    }

    public void eliminar(Integer id) { // Eliminar por Integer
        sismografos.remove(id);
    }

    public Sismografo buscarPorNroSerie(Integer nroSerie) { // Buscar por Integer nroSerie
        return sismografos.values().stream()
                .filter(s -> s.getNroSerie() != null && s.getNroSerie().equals(nroSerie))
                .findFirst()
                .orElse(null);
    }
}