package dsi.ppai.entities;

import lombok.AllArgsConstructor;
import lombok.Data; // <--- ¡ASEGÚRATE DE QUE ESTA IMPORTACIÓN ESTÉ PRESENTE!
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data // <--- ¡ESTA ANOTACIÓN ES CRUCIAL PARA LOS GETTERS Y SETTERS!
@NoArgsConstructor
@AllArgsConstructor
public class CambioEstado {
    private Empleado empleado; // El empleado que realizó el cambio de estado
    private Estado estadoAnterior;
    private Estado nuevoEstado; // El nuevo estado al que se cambió
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin; // Puede ser nulo si el estado es el actual
    private List<MotivoFueraServicio> motivos; // Motivos asociados a este cambio de estado
}