package dsi.ppai.entities;

import lombok.AllArgsConstructor; // Para un constructor con todos los campos
import lombok.Data;
import lombok.NoArgsConstructor; // Para un constructor vacío (si lo necesitas)
import lombok.NonNull; // Si 'motivo' es requerido y final

@Data
@NoArgsConstructor // Puedes mantener este si necesitas un constructor vacío en otros lugares
@AllArgsConstructor // Generará un constructor con todos los campos: (String observacion, MotivoTipo motivo)
public class MotivoFueraServicio {

    private String observacion; // Campo para una observación específica
    @NonNull // Si MotivoTipo siempre debe estar presente
    private MotivoTipo motivo; // Referencia al tipo de motivo (Mantenimiento, Calibracion, etc.)

    // Si solo quieres un constructor para el motivo, sin observación inicial, lo puedes hacer así:
    // public MotivoFueraServicio(@NonNull MotivoTipo motivo) {
    //     this.motivo = motivo;
    // }
}