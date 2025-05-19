package dsi.ppai.entities;

import lombok.Data;

@Data
public class Empleado {
    private String legajo;
    private String nombre;
    private String apellido;
    private String mail;
    private String telefono;
    private Rol rol; // Relación de Empleado al Rol

    // Agregar el legajo en el constructor
    public Empleado(String legajo, String apellido, String mail, String nombre, String telefono, Rol rol) {
        this.legajo = legajo;
        this.apellido = apellido;
        this.mail = mail;
        this.nombre = nombre;
        this.telefono = telefono;
        this.rol = rol;
    }
}