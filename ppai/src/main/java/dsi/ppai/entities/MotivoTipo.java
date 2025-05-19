package dsi.ppai.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MotivoTipo {

    private String descripcion;
    private final List<MotivoFueraServicio> motivos = new ArrayList<>();

    public MotivoTipo(String descripcion) {
        this.descripcion = descripcion;
    }
    public void agregarMotivo(MotivoFueraServicio motivo) {
        motivos.add(motivo);
    }
}
