package dsi.ppai.config;

// import dsi.ppai.entities.Empleado;
// import dsi.ppai.entities.Rol;
import dsi.ppai.services.GestorInspeccion;
import dsi.ppai.entities.Sesion;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component
public class InspeccionRunner implements CommandLineRunner {

    private final GestorInspeccion gestorInspeccion;
    private final Sesion sesion;

    public InspeccionRunner(GestorInspeccion gestorInspeccion, Sesion sesion) {
        this.gestorInspeccion = gestorInspeccion;
        this.sesion = sesion;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Ejecutando InspeccionRunner...");

        // Rol rolRI = new Rol("RESPONSABLE_INSPECCION", "Responsable de inspección del área de sismógrafos.");
        // Empleado empleadoRI_Juan = new Empleado("1001", "Pérez", "juan.perez@empresa.com", "Juan", "123456789", rolRI);
        // sesion.setEmpleadoLogueado(empleadoRI_Juan);
        // System.out.println("Sesión iniciada para el Responsable de Inspección: " + sesion.getEmpleadoLogueado().getNombre());

        System.out.println("InspeccionRunner finalizado. La selección de RI se realizará en la UI.");
    }
}