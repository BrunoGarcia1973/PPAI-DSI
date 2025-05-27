package dsi.ppai.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor; // Si lo usas

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
// @AllArgsConstructor // Descomentar si usas Lombok y todos los campos deben ser parte del constructor

public class MotivoFueraServicio {
    private String comentario; // Este es el campo 'String'
    private MotivoTipo motivoTipo; // Este es el campo 'MotivoTipo'
    private LocalDateTime fechaHoraMotivo; // Puedes añadir esto para registrar cuándo se seleccionó

    public MotivoFueraServicio(MotivoTipo motivoTipo, LocalDateTime fechaHoraMotivo) {
        this.motivoTipo = motivoTipo;
        this.comentario = motivoTipo.getDescripcion(); // <-- Usamos la descripción del MotivoTipo
        this.fechaHoraMotivo = fechaHoraMotivo;
    }

    // Constructor existente (asumo)
    public MotivoFueraServicio(String comentario, MotivoTipo motivoTipo) {
        this.comentario = comentario;
        this.motivoTipo = motivoTipo;
        this.fechaHoraMotivo = LocalDateTime.now(); // Asigna la fecha actual por defecto
    }
}
