package dsi.ppai.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sismografo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sismografo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identificador_sismografo", nullable = false, unique = true)
    private String identificadorSismografo;

    @Column(name = "nro_serie", nullable = false)
    private String nroSerie;

    @Column(name = "fecha_adquisicion", nullable = false)
    private LocalDate fechaAdquisicion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estacion_id", nullable = false)
    private EstacionSismologica estacionSismologica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_actual_id")
    private Estado estadoActual;

    @OneToMany(mappedBy = "sismografo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CambioEstado> cambiosDeEstados = new ArrayList<>();

    public Sismografo(String identificadorSismografo, LocalDate fechaAdquisicion, String nroSerie) {
        this.identificadorSismografo = identificadorSismografo;
        this.fechaAdquisicion = fechaAdquisicion;
        this.nroSerie = nroSerie;
        this.cambiosDeEstados = new ArrayList<>();
    }

    public boolean tieneEstadoActual() {
        return cambiosDeEstados.stream().anyMatch(CambioEstado::esEstadoActual);
    }

    private CambioEstado obtenerCambioEstadoActual() {
        return cambiosDeEstados.stream()
                .filter(ce -> ce.getFechaHoraFin() == null)
                .findFirst()
                .orElse(null);
    }

    public void marcarFueraDeServicio(List<MotivoFueraServicio> motivosSeleccionados, Empleado empleadoRI, Estado estadoFueraDeServicio) {
        Estado estadoAnterior = this.estadoActual;
        // 1. Finalizar el CambioEstado actual
        CambioEstado cambioActual = this.obtenerCambioEstadoActual();
        if (cambioActual != null) {
            cambioActual.cerrarCambio();
        }
        // 2. Crear el nuevo CambioEstado
        CambioEstado nuevoCambio = new CambioEstado(
                empleadoRI,
                estadoAnterior,
                estadoFueraDeServicio,
                OffsetDateTime.now(ZoneOffset.of("-03:00")),
                null,
                motivosSeleccionados
        );
        // 3. Asociar Motivos al NUEVO CAMBIO DE ESTADO
        if (motivosSeleccionados != null) {
            for (MotivoFueraServicio motivo : motivosSeleccionados) {
                motivo.setCambioEstado(nuevoCambio);
            }
        }
        // 4. Registrar el nuevo cambio de estado
        this.agregarCambioEstado(nuevoCambio);
        // 5. Actualizar estado actual de la entidad Sismografo
        this.estadoActual = estadoFueraDeServicio;
    }

    public Estado getEstadoActual() {
        if (estadoActual != null) {
            return estadoActual;
        }
        CambioEstado cambioActual = obtenerCambioEstadoActual();
        return cambioActual != null ? cambioActual.getEstadoNuevo() : null;
    }

    public void agregarCambioEstado(CambioEstado cambio) {
        if (this.cambiosDeEstados == null) {
            this.cambiosDeEstados = new ArrayList<>();
        }

        cambio.setSismografo(this);
        this.cambiosDeEstados.add(cambio);
    }

}