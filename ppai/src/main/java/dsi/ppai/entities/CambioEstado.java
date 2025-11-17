package dsi.ppai.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cambio_estado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambioEstado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "ambito", nullable = false)
    private Estado.AmbitoEstado ambito;

    // --- RELACIÓN RESTAURADA 1: Sismografo ---
    // ESTO DEBE EXISTIR para que Sismografo.cambiosDeEstados compile (mappedBy="sismografo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sismografo_id")
    private Sismografo sismografo;

    // --- RELACIÓN RESTAURADA 2: OrdenDeInspeccion ---
    // Esto ya lo restauraste y es lo que Hibernate espera para el mappedBy="ordenInspeccion"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_inspeccion_id")
    private OrdenDeInspeccion ordenInspeccion;

    // El estado nuevo SÍ debe existir como FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    private Estado estadoNuevo;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private OffsetDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin")
    private OffsetDateTime fechaHoraFin;

    // RELACIÓN CLAVE (Motivos)
    @OneToMany(mappedBy = "cambioEstado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MotivoFueraServicio> motivosSeleccionados = new ArrayList<>();

    @Transient
    private Empleado empleado; // Transitorio

    @Transient
    private Estado estadoAnterior; // Transitorio

    // Constructor principal usado por entidades
    public CambioEstado(Empleado empleado, Estado estadoAnterior, Estado estadoNuevo, OffsetDateTime fechaHoraInicio, OffsetDateTime fechaHoraFin, List<MotivoFueraServicio> motivosSeleccionados) {
        this.empleado = empleado;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.motivosSeleccionados = (motivosSeleccionados != null) ? new ArrayList<>(motivosSeleccionados) : new ArrayList<>();
        if (estadoNuevo != null) {
            this.ambito = estadoNuevo.getAmbito();
        }
    }

    public boolean esEstadoActual() {
        return this.fechaHoraFin == null;
    }

    public void cerrarCambio() {
        this.fechaHoraFin = OffsetDateTime.now(ZoneOffset.of("-03:00"));
    }
}