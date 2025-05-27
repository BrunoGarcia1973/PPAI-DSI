package dsi.ppai.entities;

import lombok.AllArgsConstructor; // ¡QUITAR ESTO SI USAS @Data y tienes campos 'final' con constructor manual!
import lombok.Data;
// import lombok.Getter; // Ya no es necesario si eliminas el campo problemático

import java.time.LocalDateTime;
import java.util.ArrayList; // Para inicializar la lista en el constructor
import java.util.List;

/**
 * Representa el cambio de estado de una OrdenDeInspeccion o Sismografo,
 * incluyendo el estado anterior, el nuevo estado, timestamps y motivos.
 */
@Data // Proporciona getters, setters, equals, hashCode, toString
// @AllArgsConstructor // <-- QUITAR ESTO SI MANTIENES EL CONSTRUCTOR MANUAL Y CAMPOS FINAL.
//    Lombok y 'final' + constructor manual pueden causar conflictos.
//    Si quieres un constructor sin argumentos, usa @NoArgsConstructor
//    Si quieres un constructor con todos los args y sin campos final, puedes dejar @AllArgsConstructor
//    Para simplificar, usaremos un constructor manual y @Data para los getters.
public class CambioEstado {
    private final Empleado empleado; // Campo final
    private final Estado estadoAnterior; // Campo final
    private final Estado estadoNuevo; // Campo final
    private final LocalDateTime fechaHoraInicio; // Campo final
    private LocalDateTime fechaHoraFin; // No es final, se modifica al cerrar
    private final List<MotivoFueraServicio> motivosSeleccionados; // Campo final

    /**
     * Constructor completo. Fecha de fin puede ser null si el cambio está activo.
     */
    public CambioEstado(
            Empleado empleado,
            Estado estadoAnterior,
            Estado estadoNuevo,
            LocalDateTime fechaHoraInicio,
            LocalDateTime fechaHoraFin,
            List<MotivoFueraServicio> motivosSeleccionados
    ) {
        this.empleado = empleado;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        // Asegúrate de que la lista se inicialice correctamente para evitar NullPointerException si se usa
        this.motivosSeleccionados = motivosSeleccionados != null ? new ArrayList<>(motivosSeleccionados) : new ArrayList<>();
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
            List<MotivoFueraServicio> motivosFueraServicio
    ) {
        Estado nuevo = new Estado("FueraDeServicio"); // Asume que este Estado es válido o lo buscas
        return new CambioEstado(
                empleado,
                estadoAnterior,
                nuevo,
                LocalDateTime.now(),
                null, // Fecha de fin nula para indicar que está activo
                motivosFueraServicio
        );
    }

    /**
     * Marca este cambio como cerrado, fijando la fechaHoraFin al instante actual.
     */
    public void cerrarCambio() {
        this.fechaHoraFin = LocalDateTime.now();
    }

    /**
     * Devuelve true si el cambio aún no fue cerrado (fechaHoraFin == null), indicando que es el estado actual.
     */
    public boolean esEstadoActual() {
        return this.fechaHoraFin == null;
    }

    // ¡¡¡CAMPO ELIMINADO: @Getter private List<MotivoTipo> motivos;!!!
    // Este campo era redundante y causaba el problema de compilación.
}