package dsi.ppai.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "motivo_fuera_servicio", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cambio_estado_id", "motivo_tipo_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotivoFueraServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cambio_estado_id", nullable = false)
    private CambioEstado cambioEstado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motivo_tipo_id", nullable = false)
    private MotivoTipo motivoTipo;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    public MotivoFueraServicio(String comentario, MotivoTipo motivoTipo) {
        this.comentario = comentario;
        this.motivoTipo = motivoTipo;
    }

    public void setCambioEstado(CambioEstado cambioEstado) {
        this.cambioEstado = cambioEstado;
    }
}