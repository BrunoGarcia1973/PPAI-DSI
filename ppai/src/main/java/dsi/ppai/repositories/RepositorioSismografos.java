package dsi.ppai.repositories;

import dsi.ppai.entities.Sismografo;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RepositorioSismografos {

    private final Map<Long, Sismografo> sismografos = new HashMap<>();

    @PostConstruct
    public void init() {
        System.out.println("Inicializando RepositorioSismografos...");
        // Los sismógrafos se crearán y se les asignará un estado inicial en DatosInicialesService
        // Este repositorio simplemente los almacenará cuando se inserten desde DatosInicialesService.
    }

    public void insertar(Sismografo sismografo) {
        sismografos.put(sismografo.getIdentificadorSismografo(), sismografo);
    }

    public Sismografo buscarPorIdentificador(Long identificador) {
        return sismografos.get(identificador);
    }

    public List<Sismografo> buscarTodos() {
        return new ArrayList<>(sismografos.values());
    }

    // Método para buscar por número de serie (usando el getter correcto de Lombok)
    public Sismografo buscarPorNumeroDeSerie(Integer numeroSerie) {
        return sismografos.values().stream()
                .filter(s -> s.getNumeroSerie() != null && s.getNumeroSerie().equals(numeroSerie))
                .findFirst()
                .orElse(null);
    }
}