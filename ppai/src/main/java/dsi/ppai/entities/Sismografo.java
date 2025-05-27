package dsi.ppai.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Importación necesaria para stream().findFirst()

@Data
@NoArgsConstructor
public class Sismografo {

    private Integer identificadorSismografo; // Mantenido como Integer
    private LocalDate fechaAdquisicion; // Mantenido como fechaAdquisicion
    private Integer nroSerie;
    private EstacionSismologica estacionSismologica; // Asumiendo que esta es una referencia a EstacionSismologica
    private List<CambioEstado> cambiosDeEstados = new ArrayList<>(); // Inicialización aquí por defecto

    // Constructor que usa DatosInicialesService
    public Sismografo(Integer identificadorSismografo, LocalDate fechaAdquisicion, Integer nroSerie) {
        this.identificadorSismografo = identificadorSismografo;
        this.fechaAdquisicion = fechaAdquisicion;
        this.nroSerie = nroSerie;
        this.cambiosDeEstados = new ArrayList<>(); // Asegura que la lista se inicialice
    }

    // TU MÉTODO ORIGINAL
    public boolean tieneEstadoActual() {
        return cambiosDeEstados.stream().anyMatch(CambioEstado::esEstadoActual);
    }

    // TU MÉTODO ORIGINAL
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
        // NOTA: CambioEstado.createFueraDeServicio necesita el Empleado y EstadoAnterior.
        // Aquí se pasa el estado ACTUAL del sismógrafo como el estado ANTERIOR para el nuevo CambioEstado.
        CambioEstado nuevoCambio = CambioEstado.createFueraDeServicio(
                cambioActual != null ? cambioActual.getEmpleado() : null, // Empleado del cambio anterior o null
                cambioActual != null ? cambioActual.getEstadoNuevo() : null, // Estado previo del sismografo
                motivosSeleccionados
        );

        // 3) Registrar el nuevo cambio de estado
        cambiosDeEstados.add(nuevoCambio);
    }

    // *** MÉTODOS AÑADIDOS NECESARIAMENTE PARA QUE TU CÓDIGO EXTERNO (OrdenDeInspeccion, DatosInicialesService) COMPILE Y FUNCIONE ***

    /**
     * Retorna el estado actual del sismógrafo (el único en la lista de cambios con fechaHoraFin == null).
     * Necesario para OrdenDeInspeccion.ponerFueraDeServicio() y otras posibles consultas.
     */
    public Estado getEstadoActual() {
        return cambiosDeEstados.stream()
                .filter(ce -> ce.getFechaHoraFin() == null) // Busca el cambio de estado que no tiene fecha de fin
                .findFirst()
                .map(CambioEstado::getEstadoNuevo)
                .orElse(null); // Retorna null si no hay un estado activo
    }

    /**
     * Agrega un cambio de estado al historial del sismógrafo.
     * Necesario para DatosInicialesService (para la carga inicial) y OrdenDeInspeccion
     * para agregar el CambioEstado del sismógrafo tras ponerlo fuera de servicio.
     */
    public void agregarCambioEstado(CambioEstado cambio) {
        if (this.cambiosDeEstados == null) {
            this.cambiosDeEstados = new ArrayList<>();
        }
        this.cambiosDeEstados.add(cambio);
    }
}