package dsi.ppai.entities;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
@Data
public class OrdenDeInspeccion {

    private LocalDateTime fechaHoraCierre;
    private LocalDateTime fechaHoraFinalizacion;
    private LocalDateTime fechaHoraInicio;
    // Métodos llamados por el Gestor en el primer loop:
    private Long numOrden;
    private String observacionCierre;

    private Empleado empleado; // Relacion con la clase Empleado
    private Estado estado; // Relacion con el Estado 
    private EstacionSismologica estacionSismologica; // Relacion con la estacion
    private List<CambioEstado> cambios = new ArrayList<>();

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

    public OrdenDeInspeccion(Long numeroOrden, Empleado empleado) {
        this.numOrden = numeroOrden;
        this.empleado = empleado;
    }


    ///Me fijo asi es de empleado 
    public boolean sosDeEmpleado(Empleado empleado) {
        return this.empleado.equals(empleado);
    }


    public LocalDateTime getFechaFinalizacion() {
        return fechaHoraFinalizacion;
    }
    //Delegacion para que me de el nombre de la ES
    public EstacionSismologica getNombreES() {
        return this.estacionSismologica;
    }

    //Delegacion del identificador del sismografo
    public Integer getIdentificadorSismografo() {
        return estacionSismologica.obtenerIdentificadorSismografo();
    }


    public boolean sosCompletamenteRealizada() {
        return estado != null && estado.sosCompletamenteRealizada();
    }


    public void ponerFueraDeServicio(List<MotivoTipo> motivosSeleccionados) {
        // 1) Crear el nuevo estado
        Estado nuevoEstado = new Estado("FueraDeServicio");
        LocalDateTime ahora = LocalDateTime.now();

        // 2) Crear y registrar el cambio de estado
        CambioEstado cambio = new CambioEstado(
                this.empleado,
                this.estado,
                nuevoEstado,
                null, // fechaInicio (puede omitirse o usarse según tu modelo)
                ahora,
                motivosSeleccionados
        );

        this.estado = nuevoEstado;
        registrarCambioEstado(cambio);

        // 3) Inhabilitar el sismógrafo asociado
        Sismografo sismografo = estacionSismologica.getSismografo();
        if (sismografo != null) {
            sismografo.marcarFueraDeServicio(motivosSeleccionados);
        }

    }
    public List<MotivoTipo> getMotivos() {
        if (cambios.isEmpty()) {
            return List.of(); // lista vacía si no hay cambios
        }

        CambioEstado ultimoCambio = cambios.get(cambios.size() - 1);
        return ultimoCambio.getMotivos();
    }

    public void registrarCambioEstado(CambioEstado cambio) {
        this.cambios.add(cambio);
    }
}

