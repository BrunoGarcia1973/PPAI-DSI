package dsi.ppai.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
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

    public boolean tieneEstadoActual() {
        return cambiosDeEstados.stream().anyMatch(CambioEstado::esEstadoActual);
    }

    public void marcarFueraDeServicio(List<MotivoFueraServicio> motivosSeleccionados) {
        // 1) Obtener el cambio de estado actual
        CambioEstado cambioActual = cambiosDeEstados.stream()
                .filter(CambioEstado::esEstadoActual)
                .findFirst()
                .orElse(null);

        if (cambioActual != null) {
            cambioActual.setFechaHoraFin(LocalDateTime.now());
        }

        // 2) Crear el nuevo estado 'FueraDeServicio' y el CambioEstado usando el factory method
        CambioEstado nuevoCambio = CambioEstado.createFueraDeServicio(
                cambioActual != null ? cambioActual.getEmpleado() : null,
                cambioActual != null ? cambioActual.getEstadoAnterior() : null,
                motivosSeleccionados
        );

        // 3) Registrar el nuevo cambio de estado
        cambiosDeEstados.add(nuevoCambio);
    }
}