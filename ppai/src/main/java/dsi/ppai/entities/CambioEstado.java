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
    private LocalDateTime fechaHoraFin;
    private List<MotivoFueraServicio> motivosSeleccionados;

    // Constructor principal usado por entidades y DatosInicialesService
    public CambioEstado(Empleado empleado, Estado estadoAnterior, Estado estadoNuevo, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, List<MotivoFueraServicio> motivosSeleccionados) {
        this.empleado = empleado;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
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
        Estado estadoFueraDeServicio = new Estado("FUERA_DE_SERVICIO");
        return new CambioEstado(
                empleado,
                estadoAnterior,
                estadoFueraDeServicio,
                LocalDateTime.now(),
                null,
                motivosFueraServicio
        );
    }

    public boolean esEstadoActual() {
        return this.fechaHoraFin == null;
    }

    public void cerrarCambio() {
        this.fechaHoraFin = LocalDateTime.now();
    }
}