package dsi.ppai.entities;

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

    public OrdenDeInspeccion(Long numOrden, LocalDateTime fechaHoraInicio, Empleado empleado, LocalDateTime fechaHoraCierre, String observacion, LocalDateTime fechaHoraFinalizacion, Estado estado, Estacion estacion) {
        this.numOrden = numOrden;
        this.fechaHoraInicio = fechaHoraInicio;
        this.empleado = empleado;
        this.fechaHoraCierre = fechaHoraCierre;
        this.observacionCierre = observacion;
        this.fechaHoraFinalizacion = fechaHoraFinalizacion;
        this.estado = estado;
        this.estacion = estacion;
    }

}
