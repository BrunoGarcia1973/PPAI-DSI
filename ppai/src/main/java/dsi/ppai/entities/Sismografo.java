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

    // Relación que gestiona la FK 'sismografo_id' en la tabla 'cambio_estado'
    // La entidad CambioEstado ya no tiene el campo Sismografo, pero el mapeo ocurre aquí
    // NOTA: 'mappedBy' requiere que CambioEstado tenga un campo 'sismografo'. Mantendremos la firma, asumiendo que el campo @Transient en CambioEstado se usa para la persistencia inversa.
    @OneToMany(mappedBy = "sismografo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CambioEstado> cambiosDeEstados = new ArrayList<>();

    public Sismografo(String identificadorSismografo, LocalDate fechaAdquisicion, String nroSerie) {
        this.identificadorSismografo = identificadorSismografo;
        this.fechaAdquisicion = fechaAdquisicion;
        this.nroSerie = nroSerie;
        this.cambiosDeEstados = new ArrayList<>();
    }

    // --- MÉTODOS DE LÓGICA DE ESTADO ---

    public boolean tieneEstadoActual() {
        return cambiosDeEstados.stream().anyMatch(CambioEstado::esEstadoActual);
    }

    private CambioEstado obtenerCambioEstadoActual() {
        return cambiosDeEstados.stream()
                .filter(ce -> ce.getFechaHoraFin() == null)
                .findFirst()
                .orElse(null);
    }

    /**
     * Mueve el sismógrafo al estado FUERA DE SERVICIO, registra los motivos y el CambioEstado.
     */
    public void marcarFueraDeServicio(List<MotivoFueraServicio> motivosSeleccionados, Empleado empleadoRI, Estado estadoFueraDeServicio) {

        Estado estadoAnterior = this.estadoActual;

        // 1. Finalizar el CambioEstado actual
        CambioEstado cambioActual = this.obtenerCambioEstadoActual();
        if (cambioActual != null) {
            cambioActual.cerrarCambio();
        }

        // 2. Crear el nuevo CambioEstado (Usando la entidad Estado persistente)
        CambioEstado nuevoCambio = new CambioEstado(
                empleadoRI,
                estadoAnterior,
                estadoFueraDeServicio,
                OffsetDateTime.now(ZoneOffset.of("-03:00")),
                null,
                motivosSeleccionados
        );

        // 3. Asociar Motivos al NUEVO CAMBIO DE ESTADO (MotivoFueraServicio.cambioEstado_id)
        if (motivosSeleccionados != null) {
            for (MotivoFueraServicio motivo : motivosSeleccionados) {
                motivo.setCambioEstado(nuevoCambio);
            }
        }

        // 4. Registrar el nuevo cambio de estado (Bidireccionalidad)
        this.agregarCambioEstado(nuevoCambio);

        // 5. Actualizar estado actual de la entidad Sismografo
        this.estadoActual = estadoFueraDeServicio;
    }

    // --- MÉTODOS AUXILIARES DE PERSISTENCIA Y ENTIDAD ---


    public Estado getEstadoActual() {
        if (estadoActual != null) {
            return estadoActual;
        }
        CambioEstado cambioActual = obtenerCambioEstadoActual();
        return cambioActual != null ? cambioActual.getEstadoNuevo() : null;
    }

    /**
     * Agrega el cambio de estado, estableciendo la referencia bidireccional.
     * Esta es la línea crucial que persiste la FK 'sismografo_id' en la tabla 'cambio_estado'.
     */
    public void agregarCambioEstado(CambioEstado cambio) {
        if (this.cambiosDeEstados == null) {
            this.cambiosDeEstados = new ArrayList<>();
        }
        // NECESARIO para que la columna sismografo_id se guarde en la tabla cambio_estado.
        cambio.setSismografo(this);
        this.cambiosDeEstados.add(cambio);
    }

}