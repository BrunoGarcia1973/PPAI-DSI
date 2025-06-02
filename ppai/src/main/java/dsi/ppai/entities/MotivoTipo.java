package dsi.ppai.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotivoTipo {
    private String descripcion;
    private List<MotivoFueraServicio> motivos;

    public MotivoTipo(String descripcion) {
        this.descripcion = descripcion;
        this.motivos = new ArrayList<>();
    }
    public MotivoTipo(List<MotivoFueraServicio> motivos) {
        this.motivos = (motivos != null) ? new ArrayList<>(motivos) : new ArrayList<>();
    }

    public void agregarMotivo(MotivoFueraServicio motivo) {
        if (this.motivos == null) {
            this.motivos = new ArrayList<>();
        }
        this.motivos.add(motivo);
    }
}