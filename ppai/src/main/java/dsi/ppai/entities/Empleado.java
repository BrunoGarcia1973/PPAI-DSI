package dsi.ppai.entities;

import lombok.Data

@Data

public class Empleado {
    private String legajo;
    private String nombre;
    private String apellido;
    private String mail;
    private String telefono;

    public Empleado(String apellido, String mail, String nombre, String telefono) {
        this.apellido = apellido;
        this.mail = mail;
        this.nombre = nombre;
        this.telefono = telefono;
    }
}
