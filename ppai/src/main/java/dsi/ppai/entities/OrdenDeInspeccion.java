package dsi.ppai.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orden_de_inspeccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenDeInspeccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_orden", nullable = false, unique = true)
    private String numeroOrden;
    
    @Column(name = "fecha_hora_inicio")
    private OffsetDateTime fechaHoraInicio;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_inspeccion_id", nullable = false)
    private Empleado empleado;
    
    @Column(name = "fecha_hora_cierre")
    private OffsetDateTime fechaHoraCierre;
    
    @Column(name = "observacion_cierre", columnDefinition = "TEXT")
    private String observacionCierre;
    
    @Column(name = "fecha_hora_finalizacion")
    private OffsetDateTime fechaHoraFinalizacion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_actual_id")
    private Estado estado;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estacion_id", nullable = false)
    private EstacionSismologica estacionSismologica;
    
    @OneToMany(mappedBy = "ordenInspeccion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CambioEstado> cambios = new ArrayList<>();
    
    @Transient
    private String diagnostico; // Campo no presente en DDL, mantenerlo transitorio

    public Long getNumOrden() {
        return this.id;
    }

    public OrdenDeInspeccion(String numeroOrden, OffsetDateTime fechaHoraInicio, Empleado empleado,
                             OffsetDateTime fechaHoraCierre, String observacionCierre, String diagnostico,
                             Estado estado, EstacionSismologica estacionSismologica, OffsetDateTime fechaHoraFinalizacion) {
        this.numeroOrden = numeroOrden;
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

    public void ponerFueraDeServicio(List<MotivoFueraServicio> motivos, Empleado empleadoLogueado, Estado estadoFueraDeServicio) {
        if (this.estacionSismologica == null || this.estacionSismologica.getSismografo() == null) {
            throw new IllegalStateException("La orden no tiene una estación o sismógrafo asociado para marcar fuera de servicio.");
        }
        if (motivos == null || motivos.isEmpty()) {
            throw new IllegalArgumentException("Se deben especificar motivos para poner el sismógrafo fuera de servicio.");
        }
        this.estacionSismologica.getSismografo().marcarFueraDeServicio(motivos);
    }

    public void registrarCambioEstado(CambioEstado cambio) {
        if (this.cambios == null) {
            this.cambios = new ArrayList<>();
        }
        cambio.setOrdenInspeccion(this);
        this.cambios.add(cambio);
    }
}