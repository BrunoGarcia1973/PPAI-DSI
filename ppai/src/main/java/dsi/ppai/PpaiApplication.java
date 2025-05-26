package dsi.ppai;

import dsi.ppai.frontend.InspeccionApp; // Importa tu clase JavaFX
import javafx.application.Application; // Importa la clase base de JavaFX
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class PpaiApplication {

    public static void main(String[] args) {
        System.out.println("DEBUG (PpaiApplication): Iniciando contexto de Spring Boot.");
        // Paso 1: Iniciar el contexto de Spring Boot
        ConfigurableApplicationContext context = SpringApplication.run(PpaiApplication.class, args);
        System.out.println("DEBUG (PpaiApplication): Contexto de Spring Boot iniciado.");

        // Paso 2: Pasar el contexto de Spring a la aplicación JavaFX
        // Esto permite que los controladores de JavaFX obtengan beans de Spring.
        InspeccionApp.setApplicationContext(context);
        System.out.println("DEBUG (PpaiApplication): Contexto de Spring pasado a InspeccionApp.");

        // Paso 3: Lanzar la aplicación JavaFX en su propio hilo
        // El método launch() de JavaFX debe ser llamado desde el hilo principal
        // y luego JavaFX tomará el control para llamar a init() y start().
        System.out.println("DEBUG (PpaiApplication): Lanzando la aplicación JavaFX...");
        Application.launch(InspeccionApp.class, args);
        System.out.println("DEBUG (PpaiApplication): La aplicación JavaFX ha terminado.");
        // El código aquí solo se ejecutará después de que la ventana de JavaFX se cierre.
    }
}