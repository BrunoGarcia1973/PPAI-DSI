package dsi.ppai.entities;

// import lombok.Getter; // @Data ya incluye @Getter
// import lombok.Setter; // @Data ya incluye @Setter
import lombok.Data; // Usamos @Data para getters, setters, equals, hashCode, toString
import lombok.NoArgsConstructor; // Constructor sin argumentos (si lo necesitas)
import lombok.AllArgsConstructor; // Opcional, pero si tienes todos los campos, es útil

// Importante: Eliminar la lista 'motivos' de aquí.
// MotivoTipo debe ser solo la definición del tipo de motivo, no una colección de instancias.

@Data // Proporciona getters, setters, equals, hashCode, toString por defecto
@NoArgsConstructor // Para constructor sin argumentos si es necesario
@AllArgsConstructor // Genera un constructor con todos los campos declarados (id, descripcion, detalle)
public class MotivoTipo {
    private Integer id; // Añade el ID
    private String descripcion;
    private String detalle; // Añade el detalle

    // Si usas @AllArgsConstructor, este constructor manual no es estrictamente necesario,
    // pero lo puedes dejar para mayor claridad o si quieres un constructor específico

    // ELIMINAR: La lista 'motivos' y el método 'agregarMotivo'
    // private final List<MotivoFueraServicio> motivos = new ArrayList<>();
    // public void agregarMotivo(MotivoFueraServicio motivo) {
    //     motivos.add(motivo);
    // }
}
