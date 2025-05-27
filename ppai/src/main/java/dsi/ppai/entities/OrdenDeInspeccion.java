package dsi.ppai.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class OrdenDeInspeccion {
    private Long numOrden;
    private LocalDateTime fechaHoraInicio;
    private Empleado empleado; // Responsable de Inspección
    private LocalDateTime fechaHoraCierre;
    private String observacionCierre;
    private String diagnostico;
    private Estado estado; // Estado actual de la orden
    private EstacionSismologica estacionSismologica;
    private LocalDateTime fechaHoraFinalizacion; // Fecha de finalización (no de cierre)
    private List<CambioEstado> cambios; // Historial de estados de la orden

    // Constructor utilizado en DatosInicialesService
    public OrdenDeInspeccion(Long numOrden, LocalDateTime fechaHoraInicio, Empleado empleado,
                             LocalDateTime fechaHoraCierre, String observacionCierre, String diagnostico,
                             Estado estado, EstacionSismologica estacionSismologica, LocalDateTime fechaHoraFinalizacion) {
        this.numOrden = numOrden;
        this.fechaHoraInicio = fechaHoraInicio;
        this.empleado = empleado;
        this.fechaHoraCierre = fechaHoraCierre;
        this.observacionCierre = observacionCierre;
        this.diagnostico = diagnostico;
        this.estado = estado;
        this.estacionSismologica = estacionSismologica;
        this.fechaHoraFinalizacion = fechaHoraFinalizacion;
        this.cambios = new ArrayList<>(); // Inicializar la lista de cambios
    }

    // Métodos de negocio requeridos por el GestorInspeccion y la UI

    /**
     * Verifica si la orden de inspección está en estado "COMPLETAMENTE_REALIZADA".
     */
    public boolean sosCompletamenteRealizada() {
        return this.estado != null && "COMPLETAMENTE_REALIZADA".equals(this.estado.getNombre());
    }

    /**
     * Verifica si la orden de inspección pertenece a un empleado dado.
     */
    public boolean sosDeEmpleado(Empleado empleado) {
        return this.empleado != null && empleado != null &&
                this.empleado.getLegajo().equals(empleado.getLegajo());
    }

    /**
     * Pone el sismógrafo asociado a la estación en "FUERA_DE_SERVICIO".
     * Delega la responsabilidad al sismógrafo.
     *
     * @param motivos Los motivos por los cuales el sismógrafo será puesto fuera de servicio.
     * @param empleadoLogueado El empleado que realiza la acción (necesario para el registro del CambioEstado).
     * @param estadoFueraDeServicio El objeto Estado "FUERA_DE_SERVICIO".
     * NOTA: La clase Sismografo no utiliza este objeto directamente
     * porque crea el estado "FUERA_DE_SERVICIO" internamente.
     * Lo mantenemos en la firma para consistencia con la llamada del Gestor.
     */
    public void ponerFueraDeServicio(List<MotivoFueraServicio> motivos, Empleado empleadoLogueado, Estado estadoFueraDeServicio) {
        if (this.estacionSismologica == null || this.estacionSismologica.getSismografo() == null) {
            throw new IllegalStateException("La orden no tiene una estación o sismógrafo asociado para marcar fuera de servicio.");
        }
        if (motivos == null || motivos.isEmpty()) {
            throw new IllegalArgumentException("Se deben especificar motivos para poner el sismógrafo fuera de servicio.");
        }
        // El empleadoLogueado no se usa directamente aquí, se pasaría al sismógrafo si su método lo necesitara.
        // Tu Sismografo.marcarFueraDeServicio() obtiene el empleado del cambio de estado anterior.
        // Si necesitas que el empleadoLogueado se registre directamente en el nuevo CambioEstado del sismógrafo,
        // la lógica de Sismografo.marcarFueraDeServicio() o CambioEstado.createFueraDeServicio() debería ajustarse.
        // Por ahora, Sismografo.marcarFueraDeServicio() obtiene el empleado del CambioEstado anterior.

        // Delega la acción directamente al sismógrafo, que ya tiene su lógica de CambioEstado.
        this.estacionSismologica.getSismografo().marcarFueraDeServicio(motivos);
    }

    /**
     * Registra un cambio de estado en el historial de la orden de inspección.
     */
    public void registrarCambioEstado(CambioEstado cambio) {
        if (this.cambios == null) {
            this.cambios = new ArrayList<>();
        }
        this.cambios.add(cambio);
    }
}