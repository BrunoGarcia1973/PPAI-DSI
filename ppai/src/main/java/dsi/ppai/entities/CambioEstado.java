package dsi.ppai.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CambioEstado {
    private Empleado empleado;
    private Estado estadoAnterior;
    private Estado estadoNuevo;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin; // Puede ser null si es el estado actual
    private List<MotivoFueraServicio> motivosSeleccionados; // Para cambios a "FUERA_DE_SERVICIO"

    // Constructor principal usado por entidades y DatosInicialesService
    public CambioEstado(Empleado empleado, Estado estadoAnterior, Estado estadoNuevo, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, List<MotivoFueraServicio> motivosSeleccionados) {
        this.empleado = empleado;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        // Asegurarse de que la lista no sea null y sea mutable si se va a modificar
        this.motivosSeleccionados = (motivosSeleccionados != null) ? new ArrayList<>(motivosSeleccionados) : new ArrayList<>();
    }

    /**
     * Factory method para crear un cambio de estado "FUERA_DE_SERVICIO".
     * Este método es llamado por Sismografo.marcarFueraDeServicio().
     * Se asume que el Estado "FUERA_DE_SERVICIO" ya existe en el RepositorioEstados.
     * @param empleado El empleado que realiza el cambio.
     * @param estadoAnterior El estado en el que se encontraba el sismógrafo antes de este cambio.
     * @param motivosFueraServicio Los motivos seleccionados para poner el sismógrafo fuera de servicio.
     * @return Una nueva instancia de CambioEstado con el estado "FUERA_DE_SERVICIO".
     */
    public static CambioEstado createFueraDeServicio(
            Empleado empleado,
            Estado estadoAnterior,
            List<MotivoFueraServicio> motivosFueraServicio
    ) {
        // En un entorno real, el Estado "FUERA_DE_SERVICIO" se obtendría del RepositorioEstados.
        // Aquí lo creamos directamente para simplificar la inicialización, asumiendo su existencia.
        Estado estadoFueraDeServicio = new Estado("FUERA_DE_SERVICIO");
        return new CambioEstado(
                empleado,
                estadoAnterior,
                estadoFueraDeServicio,
                LocalDateTime.now(),
                null, // Fecha de fin es nula porque es el estado actual
                motivosFueraServicio
        );
    }

    // TU MÉTODO ORIGINAL
    public boolean esEstadoActual() {
        return this.fechaHoraFin == null;
    }

    // Si también tenías este método, lo mantengo.
    public void cerrarCambio() {
        this.fechaHoraFin = LocalDateTime.now();
    }
}