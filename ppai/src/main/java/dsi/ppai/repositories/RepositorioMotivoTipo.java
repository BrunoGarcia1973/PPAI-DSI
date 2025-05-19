package dsi.ppai.repositories;

import dsi.ppai.entities.MotivoFueraServicio;
import dsi.ppai.entities.MotivoTipo;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Data
public class RepositorioMotivoTipo {

    private final List<MotivoFueraServicio> motivos = new ArrayList<>();

    public RepositorioMotivoTipo() {
        MotivoTipo correcto = new MotivoTipo("Correcto");
        MotivoTipo incorrecto = new MotivoTipo("Incorrecto");

        MotivoFueraServicio m1 = new MotivoFueraServicio("Sin novedades", correcto);
        MotivoFueraServicio m2 = new MotivoFueraServicio("Fuga detectada", incorrecto);
        MotivoFueraServicio m3 = new MotivoFueraServicio("Faltan datos", incorrecto);

        correcto.agregarMotivo(m1);
        incorrecto.agregarMotivo(m2);
        incorrecto.agregarMotivo(m3);
    }

    /**
     * Retorna los motivos según el tipo de cierre ("Correcto" o "Incorrecto").
     */
    public List<MotivoFueraServicio> getMotivosPorTipo(String tipoDescripcion) {
        return motivos.stream()
                .filter(m -> m.getMotivoTipo().getDescripcion().equalsIgnoreCase(tipoDescripcion))
                .collect(Collectors.toList());
    }


    public List<MotivoTipo> getMotivos() {
        return List.of(
                new MotivoTipo("Correcto"),
                new MotivoTipo("Incorrecto"),
                new MotivoTipo("Falta de presión"),
                new MotivoTipo("Daño mecánico")
        );
    }
}
