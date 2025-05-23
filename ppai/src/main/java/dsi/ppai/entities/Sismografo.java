package dsi.ppai.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data // <-- ASEGURATE QUE ESTA ANOTACION ESTA AQUI
@NoArgsConstructor
@AllArgsConstructor
public class Sismografo {
    private Long identificadorSismografo;
    private LocalDate fechaCalibracion;
    private Integer numeroSerie; // <-- ¡ASEGURATE QUE EL NOMBRE DEL CAMPO ES EXACTAMENTE "numeroSerie" (con 'n' minúscula)!
    private List<CambioEstado> historialCambioEstado;
    private Estado estadoActual;

    public Sismografo(Long identificadorSismografo, LocalDate fechaCalibracion, Integer numeroSerie) {
        this.identificadorSismografo = identificadorSismografo;
        this.fechaCalibracion = fechaCalibracion;
        this.numeroSerie = numeroSerie;
        this.historialCambioEstado = new ArrayList<>();
    }

    public void cambiarEstado(Empleado empleadoQueCambia, Estado nuevoEstado) {
        Estado estadoAnterior = this.estadoActual;
        this.estadoActual = nuevoEstado;

        CambioEstado cambio = new CambioEstado(
                empleadoQueCambia,
                estadoAnterior,
                nuevoEstado,
                LocalDateTime.now(),
                null,
                null
        );
        this.historialCambioEstado.add(cambio);
    }

    public void marcarFueraDeServicio(Empleado empleado, Estado estadoFueraDeServicio, List<MotivoFueraServicio> motivos) {
        if (this.estadoActual == null || !this.estadoActual.equals(estadoFueraDeServicio)) {
            CambioEstado cambio = new CambioEstado(empleado, this.estadoActual, estadoFueraDeServicio, LocalDateTime.now(), null, motivos);
            this.historialCambioEstado.add(cambio);
            this.estadoActual = estadoFueraDeServicio;
            System.out.println("Sismógrafo " + identificadorSismografo + " marcado como FUERA DE SERVICIO.");
        } else {
            System.out.println("Sismógrafo " + identificadorSismografo + " ya está FUERA DE SERVICIO.");
        }
    }
}