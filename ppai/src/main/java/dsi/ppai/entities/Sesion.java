package dsi.ppai.entities;

public class Sesion {
     private Usuario usuario;

    public Sesion(Usuario usuario) {
        this.usuario = usuario;
    }

    //Geter y seter

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    ///////////////////////////////////////////////////////
    public Empleado obtenerEmpleadoLogueado() {
        return usuario.obtenerEmpleado(); // Delegación directa
    }
    
}
