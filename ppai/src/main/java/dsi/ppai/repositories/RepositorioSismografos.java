package dsi.ppai.repositories;

import dsi.ppai.entities.Sismografo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class RepositorioSismografos {

    private final Map<Long, Sismografo> sismografos = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public void guardar(Sismografo sismografo) {
        if (sismografo.getIdentificadorSismografo() == null) {
            sismografo.setIdentificadorSismografo((int) nextId.getAndIncrement());
        }
        sismografos.put(Long.valueOf(sismografo.getIdentificadorSismografo()), sismografo);
    }

    public Sismografo buscar(Long id) {
        return sismografos.get(id);
    }

    public List<Sismografo> buscarTodos() {
        return new ArrayList<>(sismografos.values());
    }

    public void eliminar(Long id) {
        sismografos.remove(id);
    }

    // Puedes agregar otros métodos de búsqueda si es necesario,
    // por ejemplo, buscar por número de serie.
    public Sismografo buscarPorNroSerie(String nroSerie) {
        return sismografos.values().stream()
                .filter(s -> s.getNroSerie().equals(nroSerie))
                .findFirst()
                .orElse(null); // Devuelve null si no se encuentra
    }
}