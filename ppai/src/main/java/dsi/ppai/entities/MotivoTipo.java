package dsi.ppai.entities;

// import lombok.AllArgsConstructor; // ELIMINAR O COMENTAR
import lombok.Data;
// import lombok.NoArgsConstructor; // ELIMINAR O COMENTAR
import lombok.NonNull;
import lombok.RequiredArgsConstructor; // MANTENER ESTA

/**
 * Representa un tipo de motivo.
 */
@Data // Proporciona getters, setters, toString, equals y hashCode.
@RequiredArgsConstructor // Genera un constructor para los campos @NonNull (en este caso, 'descripcion').
// @NoArgsConstructor // No es necesario si RequiredArgsConstructor ya satisface las necesidades de inicialización o si no necesitas un constructor vacío explícitamente.
// @AllArgsConstructor // No es necesario si RequiredArgsConstructor ya genera el constructor deseado y no tienes otros campos.
public class MotivoTipo {

    /**
     * Descripción del motivo. No puede ser null.
     */
    @NonNull
    private String descripcion;

    // Si tuvieras otros campos no @NonNull, y aún quisieras un constructor vacío,
    // entonces dejarías @NoArgsConstructor. Pero para tu uso actual, con @NonNull,
    // @RequiredArgsConstructor es suficiente para el constructor con String.

    // Si necesitaras un constructor con un ID y descripción, lo podrías definir así:
    // public MotivoTipo(String id, @NonNull String descripcion) {
    //     this.id = id; // Si tuvieras un campo 'id'
    //     this.descripcion = descripcion;
    // }
}