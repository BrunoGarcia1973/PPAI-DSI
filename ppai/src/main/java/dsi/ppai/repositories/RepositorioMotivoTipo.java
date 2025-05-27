package dsi.ppai.repositories;

import dsi.ppai.entities.MotivoTipo;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Data
public class RepositorioMotivoTipo {

    private final Map<String, MotivoTipo> motivosTipo = new HashMap<>();

    public RepositorioMotivoTipo() {
        MotivoTipo mantenimiento = new MotivoTipo("Mantenimiento");
        MotivoTipo calibracion = new MotivoTipo("Calibracion");
        MotivoTipo fallaSensor = new MotivoTipo("Falla de Sensor");
        MotivoTipo faltaPresion = new MotivoTipo("Falta de presión");
        MotivoTipo danoMecanico = new MotivoTipo("Daño mecánico");
        MotivoTipo software = new MotivoTipo("Problema de software");
        MotivoTipo hardware = new MotivoTipo("Falla de hardware");
        MotivoTipo vandalismo = new MotivoTipo("Acto de vandalismo");

        motivosTipo.put(mantenimiento.getDescripcion(), mantenimiento);
        motivosTipo.put(calibracion.getDescripcion(), calibracion);
        motivosTipo.put(fallaSensor.getDescripcion(), fallaSensor);
        motivosTipo.put(faltaPresion.getDescripcion(), faltaPresion);
        motivosTipo.put(danoMecanico.getDescripcion(), danoMecanico);
        motivosTipo.put(software.getDescripcion(), software);
        motivosTipo.put(hardware.getDescripcion(), hardware);
        motivosTipo.put(vandalismo.getDescripcion(), vandalismo);
    }

    public List<MotivoTipo> buscarTiposMotivosFueraDeServicios() {
        return new ArrayList<>(motivosTipo.values());
    }

    public MotivoTipo buscarMotivoPorDescripcion(String descripcion) {
        return motivosTipo.get(descripcion);
    }

}