package dsi.ppai.entities;

public class Sesion {
    private static Sesion instancia; // patrón singleton para sesión activa
    private Usuario usuarioLogueado;

    private Sesion() {}

    public static Sesion getInstancia() {
        if (instancia == null) {
            instancia = new Sesion();
        }
        return instancia;
    }

    public void setUsuarioLogueado(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public Empleado obtenerEmpleado() {
        if (usuarioLogueado == null) {
            throw new IllegalStateException("No hay un usuario logueado.");
        }
        return usuarioLogueado.getEmpleado();
    }
}
