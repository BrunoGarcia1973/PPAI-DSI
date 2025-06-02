package dsi.ppai.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class OrdenDeInspeccion {
    private Long numOrden;
    private LocalDateTime fechaHoraInicio;
    private Empleado empleado;
    private LocalDateTime fechaHoraCierre;
    private String observacionCierre;
    private String diagnostico;
    private Estado estado;
    private EstacionSismologica estacionSismologica;
    private LocalDateTime fechaHoraFinalizacion;
    private List<CambioEstado> cambios;

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

    public boolean sosCompletamenteRealizada() {
        return this.estado.sosCompletamenteRealizada();
    }

    public boolean sosDeEmpleado(Empleado empleado) {
        return this.empleado != null && empleado != null &&
                this.empleado.getLegajo().equals(empleado.getLegajo());
    }

    public void ponerFueraDeServicio(List<MotivoFueraServicio> motivos, Empleado empleadoLogueado, Estado estadoFueraDeServicio) {
        if (this.estacionSismologica == null || this.estacionSismologica.getSismografo() == null) {
            throw new IllegalStateException("La orden no tiene una estación o sismógrafo asociado para marcar fuera de servicio.");
        }
        if (motivos == null || motivos.isEmpty()) {
            throw new IllegalArgumentException("Se deben especificar motivos para poner el sismógrafo fuera de servicio.");
        }
        this.estacionSismologica.ponerSismografoFueraServicio(motivos, empleadoLogueado, estadoFueraDeServicio);
    }

    public void registrarCambioEstado(CambioEstado cambio) {
        if (this.cambios == null) {
            this.cambios = new ArrayList<>();
        }
        this.cambios.add(cambio);
    }
}