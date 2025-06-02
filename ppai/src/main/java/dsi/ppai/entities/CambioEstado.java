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

    public static CambioEstado crearMotivoFueraServicio(
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