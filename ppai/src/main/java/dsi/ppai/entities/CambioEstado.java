package dsi.ppai.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Representa el cambio de estado de una OrdenDeInspeccion,
 * incluyendo el estado anterior, el nuevo estado, timestamps y motivos.
 */
@Data

public class CambioEstado {
    private final Empleado empleado;
    private final Estado estadoAnterior;
    private final Estado estadoNuevo;
    private final LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private final List<MotivoTipo> motivosSeleccionados;

    /**
     * Constructor completo. Fecha de fin puede ser null si el cambio está activo.
     */
    public CambioEstado(
            Empleado empleado,
            Estado estadoAnterior,
            Estado estadoNuevo,
            LocalDateTime fechaHoraInicio,
            LocalDateTime fechaHoraFin,
            List<MotivoTipo> motivosSeleccionados
    ) {
        this.empleado = empleado;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.motivosSeleccionados = motivosSeleccionados;
    }

    /**
     * Factory method para crear un cambio de estado "FueraDeServicio".
     * - El estadoNuevo se obtiene de una constante o enum en Estado.
     * - La fechaHoraInicio se fija a ahora.
     * - La fechaHoraFin queda null hasta que se cierre el cambio.
     */
    public static CambioEstado createFueraDeServicio(
            Empleado empleado,
            Estado estadoAnterior,
            List<MotivoTipo> motivos
    ) {
        Estado nuevo = new Estado("FueraDeServicio");
        return new CambioEstado(
                empleado,
                estadoAnterior,
                nuevo,
                LocalDateTime.now(),
                null,
                motivos
        );
    }


    /**
     * Marca este cambio como cerrado, fijando la fechaHoraFin al instante actual.
     */
    public void cerrarCambio() {
        this.fechaHoraFin = LocalDateTime.now();
    }

    /**
     * Devuelve true si el cambio aún no fue cerrado (fechaHoraFin == null).
     */
    public boolean esCambioActivo() {
        return this.fechaHoraFin == null;
    }

    public Boolean esEstadoActual() {
        return this.fechaHoraFin == null;
    }
}