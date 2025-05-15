package dsi.ppai.entities;
import java.util.List;
import java.time.LocalDateTime;

public class CambioEstado {
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;

    private Empleado empleado;
    private List<MotivoFueraServicio> motivoFueraDeServicio;
    private Estado estado;

    public CambioEstado(Empleado empleado, Estado estado, LocalDateTime fechaHoraFin, LocalDateTime fechaHoraInicio, List<MotivoFueraServicio> motivoFueraDeServicio) {
        this.empleado = empleado;
        this.estado = estado;
        this.fechaHoraFin = fechaHoraFin;
        this.fechaHoraInicio = fechaHoraInicio;
        this.motivoFueraDeServicio = motivoFueraDeServicio;
    }

    // Metodos get y set

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public List<MotivoFueraServicio> getMotivoFueraDeServicio() {
        return motivoFueraDeServicio;
    }

    public void setMotivoFueraDeServicio(List<MotivoFueraServicio> motivoFueraDeServicio) {
        this.motivoFueraDeServicio = motivoFueraDeServicio;
    }

    //METODO PARA VER EL ESTADO ACTUAL
    public boolean esEstadoActual() {
        return fechaHoraFin == null;
    }
}
