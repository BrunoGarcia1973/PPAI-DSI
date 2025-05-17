package dsi.ppai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import dsi.ppai.entities.Empleado;
import dsi.ppai.entities.Rol;
import dsi.ppai.entities.Sesion;
import dsi.ppai.entities.Usuario;
import dsi.ppai.services.GestorInspeccion;

@SpringBootApplication
public class PpaiApplication {
	//public static void main(String[] args) {
	//	SpringApplication.run(PpaiApplication.class, args);
	//}

	//////MAIN PARA OBTENER EMPLEADO/////////////////
	public static void main(String[] args) {
        // Crear el rol
          Rol rolSoporte = new Rol("SOPORTE", "Acceso limitado a funciones técnicas");

        // Crear el empleado con los datos necesarios (sin legajo)
        Empleado empleado = new Empleado(
                "López",                      // apellido
                "soporte@empresa.com",        // mail
                "Marcos",                     // nombre
                "1133557799",                 // teléfono
                rolSoporte                    // rol
        );

        // Crear el usuario con solo nombre de usuario y empleado (sin contraseña)
        Usuario usuario = new Usuario("mlopez", empleado);

        // Crear la sesión con el usuario
        Sesion sesion = new Sesion(usuario);

        // Crear el gestor que tiene la sesión actual
        GestorInspeccion gestor = new GestorInspeccion(sesion);

        // Obtener el empleado logueado a través del gestor
        Empleado empleadoLogueado = gestor.obtenerEmpleadoLogueado();

        // Mostrar los datos del empleado logueado
        System.out.println("Empleado logueado:");
        System.out.println(empleadoLogueado);
    }
}

