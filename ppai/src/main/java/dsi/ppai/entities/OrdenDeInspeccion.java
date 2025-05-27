package dsi.ppai.entities;

import lombok.Data;
import lombok.NoArgsConstructor; // Necesario para un constructor sin argumentos

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor // Para permitir la creación de objetos sin usar el constructor completo
public class OrdenDeInspeccion {
    private Long numOrden;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin; // Se incluye aquí para el constructor de 8 args
    private LocalDateTime fechaHoraCierre; // Fecha real de cierre de la orden
    private String observacionCierre; // Observación ingresada al cerrar la orden
    private String observacionInterna; // <-- Campo nuevo que se pasa en el constructor
    private LocalDateTime fechaHoraFinPrevista; // <-- Campo nuevo que se pasa en el constructor
    private Empleado empleado; // Empleado responsable de la orden (Responsable de Inspección)
    private EstacionSismologica estacionSismologica; // Estación asociada a la orden
    private Estado estado; // Estado actual de la orden
    private List<CambioEstado> cambiosDeEstados = new ArrayList<>(); // Lista de cambios de estado de la ORDEN

    // Constructor que coincide con los 8 argumentos de tu DataInitializerService
    public OrdenDeInspeccion(
            Long numOrden,
            LocalDateTime fechaHoraInicio,
            Empleado empleado,
            LocalDateTime fechaHoraFinPrevista, // Asumo que este es el 4to argumento
            String observacionInterna, // Asumo que este es el 5to argumento
            LocalDateTime fechaHoraFin, // Asumo que este es el 6to argumento
            Estado estado,
            EstacionSismologica estacionSismologica
    ) {
        this.numOrden = numOrden;
        this.fechaHoraInicio = fechaHoraInicio;
        this.empleado = empleado;
        this.fechaHoraFinPrevista = fechaHoraFinPrevista; // Asignación del nuevo campo
        this.observacionInterna = observacionInterna; // Asignación del nuevo campo
        this.fechaHoraFin = fechaHoraFin; // Asignación del nuevo campo
        this.estado = estado;
        this.estacionSismologica = estacionSismologica;
        // Inicializar el primer cambio de estado de la orden al crearla
        this.cambiosDeEstados.add(new CambioEstado(empleado, null, estado, fechaHoraInicio, null, new ArrayList<>()));
    }

    public boolean sosCompletamenteRealizada() {
        return this.estado != null && this.estado.getNombre().equals("Completamente Realizada");
    }

    public boolean sosDeEmpleado(Empleado empleado) {
        return this.empleado != null && this.empleado.equals(empleado);
    }

    public void registrarCambioEstado(CambioEstado nuevoCambio) {
        this.cambiosDeEstados.stream()
                .filter(CambioEstado::esEstadoActual)
                .findFirst()
                .ifPresent(CambioEstado::cerrarCambio);
        this.cambiosDeEstados.add(nuevoCambio);
        this.estado = nuevoCambio.getEstadoNuevo();
    }

    // Método delegador para poner el sismógrafo fuera de servicio
    public void ponerSismografoFueraDeServicio(Empleado empleadoQueCierra, List<MotivoFueraServicio> motivosSeleccionados) {
        if (this.estacionSismologica != null) {
            this.estacionSismologica.marcarSismografoFueraDeServicio(empleadoQueCierra, motivosSeleccionados);
        } else {
            System.err.println("Advertencia: Orden de inspección " + numOrden + " sin estación sismológica asociada. No se puede poner el sismógrafo fuera de servicio.");
        }
    }
}