package dsi.ppai.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public void marcarFueraDeServicio(List<MotivoFueraServicio> motivosSeleccionados) {
        // 1) Obtener el cambio de estado actual
        CambioEstado cambioActual = cambiosDeEstados.stream()
                .filter(CambioEstado::esEstadoActual)
                .findFirst()
                .orElse(null);
        if (cambioActual != null) {
            cambioActual.setFechaHoraFin(OffsetDateTime.now());
        }
        // 2) Crear el nuevo estado 'FueraDeServicio' y el CambioEstado usando el factory method
        CambioEstado nuevoCambio = CambioEstado.createFueraDeServicio(
                cambioActual != null ? cambioActual.getEmpleado() : null,
                cambioActual != null ? cambioActual.getEstadoNuevo() : null,
                motivosSeleccionados
        );
        nuevoCambio.setSismografo(this);

        // 3) Registrar el nuevo cambio de estado
        cambiosDeEstados.add(nuevoCambio);
        
        // 4) Actualizar estado actual
        if (nuevoCambio.getEstadoNuevo() != null) {
            this.estadoActual = nuevoCambio.getEstadoNuevo();
        }
    }

    public Estado getEstadoActual() {
        if (estadoActual != null) {
            return estadoActual;
        }
        return cambiosDeEstados.stream()
                .filter(ce -> ce.getFechaHoraFin() == null)
                .findFirst()
                .map(CambioEstado::getEstadoNuevo)
                .orElse(null);
    }

    public void agregarCambioEstado(CambioEstado cambio) {
        if (this.cambiosDeEstados == null) {
            this.cambiosDeEstados = new ArrayList<>();
        }
        cambio.setSismografo(this);
        this.cambiosDeEstados.add(cambio);
    }
}