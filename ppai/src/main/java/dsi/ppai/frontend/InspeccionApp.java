// dsi.ppai.frontend.InspeccionApp.java

package dsi.ppai.frontend;

import javafx.application.Application; // Importa la clase base de JavaFX Application
import javafx.fxml.FXMLLoader; // Para cargar archivos FXML
import javafx.scene.Parent; // Para la raíz del grafo de la escena
import javafx.scene.Scene; // Para la escena principal
import javafx.stage.Stage; // Para la ventana principal
import org.springframework.context.ConfigurableApplicationContext; // Para el contexto de Spring
import java.io.IOException; // Para manejar excepciones de entrada/salida
import java.util.Objects; // Para la verificación de nulos (Objects.requireNonNull)

/**
 * Clase principal de la aplicación JavaFX que se integra con Spring Boot.
 * Esta clase extiende javafx.application.Application y gestiona el ciclo de vida de la UI.
 */
public class InspeccionApp extends Application {

    // Campo estático para mantener una referencia al contexto de Spring Boot.
    // Esto es necesario porque el método `launch()` de JavaFX es estático,
    // y el contexto debe ser accesible antes de que se cree una instancia de InspeccionApp.
    private static ConfigurableApplicationContext springContext;

    /**
     * Método estático para que la clase principal de Spring Boot (`PpaiApplication`)
     * pueda establecer el contexto de Spring *antes* de que JavaFX inicie la aplicación.
     * @param context El contexto de aplicación de Spring Boot.
     */
    public static void setApplicationContextStatic(ConfigurableApplicationContext context) {
        InspeccionApp.springContext = context;
        System.out.println("DEBUG (InspeccionApp): Contexto de Spring estático establecido.");
    }

    /**
     * Método de inicialización de la aplicación JavaFX.
     * Se llama antes de `start()`. Aquí verificamos que el contexto de Spring ya esté disponible.
     */
    @Override
    public void init() throws Exception {
        System.out.println("DEBUG (InspeccionApp): Entrando al método init().");
        // Asegurarse de que el contexto de Spring fue establecido por PpaiApplication.main
        Objects.requireNonNull(springContext, "El contexto de Spring debe establecerse antes de que se llame a init().");
        System.out.println("DEBUG (InspeccionApp): Contexto de Spring disponible en init().");
        // No llamamos a SpringApplication.run() aquí, ya que se hizo en PpaiApplication.main
        // y el contexto ya está inyectado a través del método estático.
    }

    /**
     * Método principal para iniciar la interfaz de usuario de JavaFX.
     * @param primaryStage El Stage (ventana principal) proporcionado por JavaFX.
     * @throws IOException Si ocurre un error al cargar el archivo FXML.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        System.out.println("DEBUG (InspeccionApp): Entrando al método start().");

        // Crea un FXMLLoader para cargar el archivo FXML de la interfaz principal.
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main_screen.fxml"));

        // Configura el ControllerFactory del FXMLLoader para que Spring cree las instancias
        // de los controladores FXML. Esto permite la inyección de dependencias de Spring en ellos.
        fxmlLoader.setControllerFactory(springContext::getBean);

        // Carga el grafo de la escena desde el FXML.
        Parent root = fxmlLoader.load();

        // Configura el título de la ventana.
        primaryStage.setTitle("PPAI - Cerrar Orden de Inspección");
        // Establece la escena en la ventana principal con las dimensiones deseadas.
        primaryStage.setScene(new Scene(root, 800, 600));
        // Muestra la ventana.
        primaryStage.show();
    }

    /**
     * Método de parada de la aplicación JavaFX.
     * Se llama cuando la aplicación se va a cerrar. Aquí cerramos el contexto de Spring.
     */
    @Override
    public void stop() throws Exception {
        System.out.println("DEBUG (InspeccionApp): Entrando al método stop(). Cerrando contexto de Spring.");
        // Cierra el contexto de Spring para liberar recursos.
        if (springContext != null) {
            springContext.close();
        }
        System.out.println("DEBUG (InspeccionApp): Contexto de Spring cerrado.");
    }

    // El método `main` ya NO se define aquí.
    // El punto de entrada principal es `PpaiApplication.main()`.
    // Si tienes un `main` aquí, bórralo o coméntalo para evitar confusiones.

    // El setter `setApplicationContext` de instancia ya NO se necesita.
    // El contexto se pasa a través del método estático `setApplicationContextStatic`.
}