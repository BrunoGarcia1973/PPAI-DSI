package dsi.ppai.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class OrdenDeInspeccion {

    private LocalDateTime fechaHoraCierre;
    private LocalDateTime fechaHoraFinalizacion;
    private LocalDateTime fechaHoraInicio;
    private Long numOrden;
    private String observacionCierre;

    private Empleado empleado; // Relacion con la clase Empleado
    private Estado estado; // Relacion con el Estado 
    private EstacionSismologica estacionSismologica; // Relacion con la estacion

    public OrdenDeInspeccion(Long numOrden, LocalDateTime fechaHoraInicio, Empleado empleado, LocalDateTime fechaHoraCierre, String observacion, LocalDateTime fechaHoraFinalizacion, Estado estado, EstacionSismologica estacion) {
        this.numOrden = numOrden;
        this.fechaHoraInicio = fechaHoraInicio;
        this.empleado = empleado;
        this.fechaHoraCierre = fechaHoraCierre;
        this.observacionCierre = observacion;
        this.fechaHoraFinalizacion = fechaHoraFinalizacion;
        this.estado = estado;
        this.estacionSismologica = estacion;
    }

    ///Me fijo asi es de empleado 
    public boolean sosDeEmpleado(Empleado empleado) {
        return this.empleado.equals(empleado);
    }

    ///Me fijo si es completamenteRealizada
    public boolean sosCompletamenteRealizado() {
        return estado.sosCompletamenteRealizada();
    }

    // Métodos llamados por el Gestor en el primer loop:
    public Long getNumOrden() {
        return numOrden;
    }

    public LocalDateTime getFechaFinalizacion() {
        return fechaHoraFinalizacion;
    }
    //Delegacion para que me de el nombre de la ES
    public String getNombreES() {
        return estacionSismologica.getNombre();
    }

    //Delegacion del identificador del sismografo
    public Integer getIdentificadorSismografo() {
        return estacionSismologica.obtenerIdentificadorSismografo();
    }

}
