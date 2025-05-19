package dsi.ppai.config;

import dsi.ppai.entities.Empleado;
import dsi.ppai.entities.Rol;
import dsi.ppai.entities.Sesion;
import dsi.ppai.entities.Usuario;
import dsi.ppai.services.GestorInspeccion;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InspeccionRunner implements CommandLineRunner {

    // Si GestorInspeccion es un bean administrado, déjalo inyectado por constructor.
    private final GestorInspeccion gestor;

    public InspeccionRunner(GestorInspeccion gestor) {
        this.gestor = gestor;
    }

    @Override
    public void run(String... args) throws Exception {
        // Puedes optar por leer datos desde la consola o trabajar con datos hardcodeados.
        // En este ejemplo, seguimos el mismo flujo que en tu main original.

        // Crear el rol
        Rol rolSoporte = new Rol("SOPORTE", "Acceso limitado a funciones técnicas");

        // Crear el empleado (sin legajo)
        Empleado empleado = new Empleado(
                "123456",                    // Legajo
                "López",                      // Apellido
                "soporte@empresa.com",        // Mail
                "Marcos",                     // Nombre
                "1133557799",                 // Teléfono
                rolSoporte                    // Rol
        );

        // Crear el usuario asociado al empleado (sin contraseña)
        Usuario usuario = new Usuario("mlopez", empleado);

        // Crear la sesión con el usuario
        Sesion sesion = new Sesion(usuario);

        // Actualizar el gestor con la sesión actual
        // Nota: En este ejemplo, en lugar de crear un nuevo objeto de GestorInspeccion,
        // podrías tenerlo configurado con la sesión actual mediante inyección de dependencia.
        // Pero si lo necesitas instanciar manualmente, lo haces así:
        gestor.setSesion(sesion);

        // Obtener el empleado logueado a través del gestor
        Empleado empleadoLogueado = gestor.obtenerEmpleadoLogueado();

        // Mostrar los datos del empleado logueado
        System.out.println("Empleado logueado:");
        System.out.println(empleadoLogueado);
    }
}