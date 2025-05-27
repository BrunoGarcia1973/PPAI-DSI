package dsi.ppai.entities;

import org.springframework.stereotype.Component;

@Component
public class Usuario {
    private String nombreUsuario;
    private Empleado empleado;

    public Empleado obtenerEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public Usuario(String nombreUsuario, Empleado empleado) {
        this.nombreUsuario = nombreUsuario;
        this.empleado = empleado;
    }

    public Usuario() {
    }

    public Empleado getEmpleado() {
        return this.empleado;
    }
}