package dsi.ppai.entities;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor

public class Sismografo {


    private Integer identificadorSismografo;
    private LocalDate fechaAdquisicion;
    private Integer nroSerie;
    private EstacionSismologica estacionSismologica;
    private List<CambioEstado> cambiosDeEstados = new ArrayList<>();

    // Métodos adicionales

    public boolean tieneEstadoActual() {
        return cambiosDeEstados.stream().anyMatch(CambioEstado::esEstadoActual);
    }




public void marcarFueraDeServicio(List<MotivoFueraServicio> motivosSeleccionados) {
    // 1) Obtener el cambio de estado actual (aquel que no tiene fecha de fin)
    CambioEstado cambioActual = cambiosDeEstados.stream()
            .filter(CambioEstado::esEstadoActual)
            .findFirst()
            .orElse(null);

    if (cambioActual != null) {
        // 2) Finalizar el cambio de estado actual
        cambioActual.setFechaHoraFin(LocalDateTime.now());
    }

    // 3) Crear el nuevo estado 'FueraDeServicio'
    Estado estadoFueraServicio = new Estado("FueraDeServicio");

    // 4) Crear un nuevo CambioEstado con el nuevo estado
    CambioEstado nuevoCambio = new CambioEstado(
            cambioActual != null ? cambioActual.getEmpleado() : null, // delegación segura
            cambioActual != null ? cambioActual.getEstadoAnterior() : null,
            estadoFueraServicio,
            LocalDateTime.now(), // fecha de inicio ahora
            null,       // sin fecha de fin, porque es el estado actual
            motivosSeleccionados
    );

    // 5) Registrar el nuevo cambio de estado
    cambiosDeEstados.add(nuevoCambio);
}

}
