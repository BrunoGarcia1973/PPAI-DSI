package dsi.ppai.repositories;

import dsi.ppai.entities.MotivoTipo;
import jakarta.annotation.PostConstruct; // Importar PostConstruct
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RepositorioMotivoTipo {

    private final Map<String, MotivoTipo> motivos = new HashMap<>();

    // Usamos @PostConstruct para inicializar los motivos al inicio de la aplicación
    @PostConstruct
    public void init() {
        System.out.println("Inicializando RepositorioMotivoTipo...");
        // Precargar datos
        MotivoTipo motivoMantenimiento = new MotivoTipo("Mantenimiento");
        MotivoTipo motivoCalibracion = new MotivoTipo("Calibracion");
        MotivoTipo motivoFallaSensor = new MotivoTipo("Falla de Sensor");

        motivos.put(motivoMantenimiento.getDescripcion(), motivoMantenimiento);
        motivos.put(motivoCalibracion.getDescripcion(), motivoCalibracion);
        motivos.put(motivoFallaSensor.getDescripcion(), motivoFallaSensor);
        System.out.println("Motivos de Tipo precargados: " + motivos.keySet());
    }

    public MotivoTipo buscarMotivoPorDescripcion(String descripcion) {
        return motivos.get(descripcion);
    }

    // ¡¡NUEVO MÉTODO AÑADIDO PARA RESOLVER EL ERROR!!
    public List<MotivoTipo> buscarTodos() {
        return new ArrayList<>(motivos.values());
    }
}