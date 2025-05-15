package dsi.ppai.entities;

import lombok.Data;

@Data
public class MotivoFueraServicio {
    private String comentario;
    private MotivoTipo motivoTipo; // Relación con la clase MotivoTipo

    public MotivoFueraServicio(String comentario, MotivoTipo motivoTipo) {
        this.comentario = comentario;
        this.motivoTipo = motivoTipo;
    }
}
