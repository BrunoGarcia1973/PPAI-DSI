// dsi.ppai.PpaiApplication.java

package dsi.ppai;

import dsi.ppai.frontend.InspeccionApp; // Importa tu clase JavaFX Application
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import javafx.application.Application; // Importa javafx.application.Application para el método launch

/**
 * Clase principal de la aplicación Spring Boot.
 * Esta clase se encarga de iniciar el contexto de Spring y luego lanzar la aplicación JavaFX.
 */
@SpringBootApplication
public class PpaiApplication {

    /**
     * El método `main` es el punto de entrada principal para la ejecución de la aplicación.
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        // 1. Inicia la aplicación Spring Boot y obtiene el contexto de la aplicación.
        // Este contexto es crucial ya que contendrá todos los beans de Spring,
        // incluyendo los servicios y repositorios.
        ConfigurableApplicationContext springContext = SpringApplication.run(PpaiApplication.class, args);

        // 2. Pasa el contexto de Spring a la clase JavaFX Application.
        // Esto se hace a través de un método estático en InspeccionApp.
        // Es necesario porque `Application.launch()` (llamado en el siguiente paso)
        // es un método estático y crea la instancia de `InspeccionApp` de forma interna.
        // El contexto debe estar disponible para la instancia de `InspeccionApp` cuando se inicialice.
        InspeccionApp.setApplicationContextStatic(springContext);

        // 3. Lanza la aplicación JavaFX.
        // `Application.launch()` es el método que inicia el ciclo de vida de JavaFX,
        // incluyendo la llamada a los métodos `init()` y `start()` de `InspeccionApp`.
        Application.launch(InspeccionApp.class, args);
    }
}