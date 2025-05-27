package dsi.ppai.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
public class Sismografo {

    private Integer identificadorSismografo;
    private LocalDate fechaAdquisicion;
    private Integer nroSerie;
    private EstacionSismologica estacionSismologica;
    private List<CambioEstado> cambiosDeEstados = new ArrayList<>();

    public Sismografo(Integer identificadorSismografo, LocalDate fechaAdquisicion, Integer nroSerie) {
        this.identificadorSismografo = identificadorSismografo;
        this.fechaAdquisicion = fechaAdquisicion;
        this.nroSerie = nroSerie;
        this.cambiosDeEstados = new ArrayList<>(); // Asegura que la lista se inicialice
    }

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
            cambioActual.setFechaHoraFin(LocalDateTime.now()); // Cierra el estado anterior
        }
        // 2) Crear el nuevo estado 'FueraDeServicio' y el CambioEstado usando el factory method
        CambioEstado nuevoCambio = CambioEstado.createFueraDeServicio(
                cambioActual != null ? cambioActual.getEmpleado() : null,
                cambioActual != null ? cambioActual.getEstadoNuevo() : null,
                motivosSeleccionados
        );

        // 3) Registrar el nuevo cambio de estado
        cambiosDeEstados.add(nuevoCambio);
    }

    public Estado getEstadoActual() {
        return cambiosDeEstados.stream()
                .filter(ce -> ce.getFechaHoraFin() == null) // Busca el cambio de estado que no tiene fecha de fin
                .findFirst()
                .map(CambioEstado::getEstadoNuevo)
                .orElse(null); // Retorna null si no hay un estado activo
    }

    public void agregarCambioEstado(CambioEstado cambio) {
        if (this.cambiosDeEstados == null) {
            this.cambiosDeEstados = new ArrayList<>();
        }
        this.cambiosDeEstados.add(cambio);
    }
}