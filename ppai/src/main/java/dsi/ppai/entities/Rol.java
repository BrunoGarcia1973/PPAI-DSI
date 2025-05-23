package dsi.ppai.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // Asegúrate de que @Data esté presente
@NoArgsConstructor
@AllArgsConstructor
public class Rol {
    private String nombre;
    private String descripcion;
}