package dsi.ppai.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // Asegúrate de que @Data esté presente
@NoArgsConstructor
@AllArgsConstructor
public class Empleado {
    private String legajo;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private Rol rol; // El Empleado tiene un Rol
}