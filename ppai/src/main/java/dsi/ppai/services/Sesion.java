package dsi.ppai.services; // <-- ¡Este paquete debe coincidir con la ubicación del archivo!

import dsi.ppai.entities.Empleado;
import org.springframework.stereotype.Component;

// Esta es una clase muy simple para manejar la sesión del usuario logueado.
// Podrías añadir más atributos como la fecha de inicio de sesión, etc.
@Component // Para que Spring la gestione como un bean
public class Sesion {

    private Empleado empleadoLogueado; // Asumo que Empleado tiene un campo 'legajo'

    // Constructor por defecto, Spring lo usará
    public Sesion() {
    }

    public Empleado getEmpleadoLogueado() {
        return empleadoLogueado;
    }

    public void setEmpleadoLogueado(Empleado empleadoLogueado) {
        this.empleadoLogueado = empleadoLogueado;
    }

    public boolean isEmpleadoLogueado() {
        return this.empleadoLogueado != null;
    }

    // Método para obtener el legajo del empleado logueado, útil para consultas.
    public String getLegajoEmpleadoLogueado() {
        return empleadoLogueado != null ? empleadoLogueado.getLegajo() : null;
    }

    // Otros métodos de Sesion que puedas necesitar (ej. cerrarSesion)
    public void cerrarSesion() {
        this.empleadoLogueado = null;
        System.out.println("Sesión cerrada.");
    }
}