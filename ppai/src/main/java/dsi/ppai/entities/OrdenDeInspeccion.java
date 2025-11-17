package dsi.ppai.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class OrdenDeInspeccion {
    private Long numOrden;
    private LocalDateTime fechaHoraInicio;
    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;
    private LocalDateTime fechaHoraCierre;
    private String observacionCierre;
    private String diagnostico;
    @ManyToOne
    @JoinColumn(name = "estado_id")
    private Estado estado;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estacion_sismologica_id")
    private EstacionSismologica estacionSismologica;
    private LocalDateTime fechaHoraFinalizacion;

    // MappedBy requiere que CambioEstado tenga el campo 'ordenInspeccion'
    @OneToMany(mappedBy = "ordenInspeccion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CambioEstado> cambios;

    @Id
    private Long id;

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
        this.cambios = new ArrayList<>();
    }

    public boolean sosCompletamenteRealizada() {
        return this.estado != null && "COMPLETAMENTE_REALIZADA".equals(this.estado.getNombre());
    }

    public boolean sosDeEmpleado(Empleado empleado) {
        return this.empleado != null && empleado != null &&
                this.empleado.getLegajo().equals(empleado.getLegajo());
    }

    /**
     * Registra un nuevo CambioEstado y establece la relación bidireccional.
     */
    public void registrarCambioEstado(CambioEstado cambio) {
        if (this.cambios == null) {
            this.cambios = new ArrayList<>();
        }

        // --- CORRECCIÓN CRUCIAL ---
        // Establece la referencia inversa (FK) en el objeto CambioEstado
        cambio.setOrdenInspeccion(this);
        // -------------------------

        this.cambios.add(cambio);
    }
}