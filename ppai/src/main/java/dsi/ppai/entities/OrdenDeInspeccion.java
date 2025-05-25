package dsi.ppai.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor; // Opcional, pero bueno tenerlo si usas todos los campos en un constructor.

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data // <--- ¡ESTA ANOTACIÓN ES CRUCIAL PARA LOS SETTERS Y GETTERS!
@NoArgsConstructor // Necesario para Spring si usas un constructor personalizado
// @AllArgsConstructor // Descomentar si usas un constructor que incluya todos los campos (incluyendo la lista)
public class OrdenDeInspeccion {
    private Long numeroOrden;
    private LocalDateTime fechaHoraCreacion;
    private Empleado responsableInspeccion; // El empleado asociado a la orden
    private LocalDateTime fechaHoraCierre;
    private String observacionCierre;
    private List<CambioEstado> historialCambioEstado;
    private Estado estadoActual; // Estado actual de la orden
    private EstacionSismologica nombreES; // Referencia a la Estacion Sismologica
    private LocalDateTime fechaHoraFinalizacion; // <--- Nuevo atributo

    // Constructor personalizado para la creación de órdenes de inspección
    public OrdenDeInspeccion(Long numeroOrden, LocalDateTime fechaHoraCreacion, Empleado responsableInspeccion,
                             LocalDateTime fechaHoraCierre, String observacionCierre, List<CambioEstado> historialCambioEstado,
                             Estado estadoActual, EstacionSismologica nombreES, LocalDateTime fechaHoraFinalizacion) { // <--- Nuevo atributo en el constructor
        this.numeroOrden = numeroOrden;
        this.fechaHoraCreacion = fechaHoraCreacion;
        this.responsableInspeccion = responsableInspeccion;
        this.fechaHoraCierre = fechaHoraCierre;
        this.observacionCierre = observacionCierre;
        // Siempre inicializa la lista para evitar NullPointerException
        this.historialCambioEstado = historialCambioEstado != null ? new ArrayList<>(historialCambioEstado) : new ArrayList<>();
        this.estadoActual = estadoActual;
        this.nombreES = nombreES;
        this.fechaHoraFinalizacion = fechaHoraFinalizacion; // <--- Asignación del nuevo atributo
    }

    public void agregarCambioEstado(CambioEstado cambioEstado) {
        if (this.historialCambioEstado == null) {
            this.historialCambioEstado = new ArrayList<>();
        }
        this.historialCambioEstado.add(cambioEstado);
        // Opcional: Actualizar el estado actual de la orden al agregar un cambio de estado
        this.estadoActual = cambioEstado.getNuevoEstado();
    }
}