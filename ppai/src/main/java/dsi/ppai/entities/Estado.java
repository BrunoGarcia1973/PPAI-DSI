package dsi.ppai.entities;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Representa un Estado dentro del sistema.
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Estado {

    /**
     * Ámbito al que pertenece el estado (opcional).
     */
    private String ambito;

    /**
     * Nombre del estado. No puede ser null.
     */
    @NonNull
    private String nombre;

    /**
     * Nombre constante para el estado "CompletamenteRealizada".
     */
    public static final String ESTADO_COMPLETAMENTE_REALIZADA = "CompletamenteRealizada";

    /**
     * Indica si este estado corresponde a "CompletamenteRealizada".
     */
    public boolean sosCompletamenteRealizada() {
        return ESTADO_COMPLETAMENTE_REALIZADA.equalsIgnoreCase(this.nombre);
    }
}

