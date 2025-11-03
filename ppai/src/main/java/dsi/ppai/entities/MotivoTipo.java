package dsi.ppai.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "motivo_tipo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotivoTipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "descripcion", nullable = false, unique = true)
    private String descripcion;
    
    @OneToMany(mappedBy = "motivoTipo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MotivoFueraServicio> motivos = new ArrayList<>();

    public MotivoTipo(String descripcion) {
        this.descripcion = descripcion;
        this.motivos = new ArrayList<>();
    }

    public void agregarMotivo(MotivoFueraServicio motivo) {
        if (this.motivos == null) {
            this.motivos = new ArrayList<>();
        }
        motivo.setMotivoTipo(this);
        this.motivos.add(motivo);
    }
}