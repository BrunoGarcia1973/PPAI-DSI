package dsi.ppai.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext; // Mantén este import

public class InspeccionApp extends Application {

    // Declaramos el contexto de Spring como estático para que PpaiApplication lo pueda establecer.
    private static ConfigurableApplicationContext applicationContext;

    // Nuevo método estático para que PpaiApplication pueda inyectar el contexto de Spring.
    public static void setApplicationContext(ConfigurableApplicationContext context) {
        InspeccionApp.applicationContext = context;
        System.out.println("DEBUG (InspeccionApp): Contexto de Spring recibido por setter.");
    }

    @Override
    public void init() {
        System.out.println("DEBUG (InspeccionApp): Entrando al método init().");
        // Ya NO necesitamos inicializar Spring Boot aquí.
        // Se hace en PpaiApplication.main() y se pasa a través de setApplicationContext().
        if (applicationContext == null) {
            System.err.println("ERROR (InspeccionApp): ¡El contexto de Spring no se ha establecido correctamente en init()!");
            // Esto es un error crítico, salimos si el contexto no está disponible.
            System.exit(1);
        }
        System.out.println("DEBUG (InspeccionApp): Contexto de Spring disponible en init().");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("DEBUG (InspeccionApp): Entrando al método start().");
        // Carga el archivo FXML para la pantalla inicial
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main_screen.fxml"));
        // Usamos el contexto estático para que Spring inyecte las dependencias en los controladores FXML.
        fxmlLoader.setControllerFactory(applicationContext::getBean);
        Parent root = fxmlLoader.load();
        System.out.println("DEBUG (InspeccionApp): FXML cargado exitosamente. Mostrando ventana...");

        primaryStage.setTitle("Gestor de Inspección");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
        System.out.println("DEBUG (InspeccionApp): Ventana mostrada.");
    }

    @Override
    public void stop() {
        System.out.println("DEBUG (InspeccionApp): Entrando al método stop(). Cerrando contexto de Spring.");
        // Cierra el contexto de Spring Boot cuando la aplicación JavaFX se detiene.
        if (applicationContext != null && applicationContext.isActive()) {
            applicationContext.close();
        }
        // No llamar a System.exit(0) aquí. Deja que el hilo principal (PpaiApplication) termine.
    }

    // El método main() en InspeccionApp ya no es el punto de entrada principal para la aplicación combinada.
    // Solo es útil si quieres ejecutar InspeccionApp como una aplicación JavaFX independiente (sin Spring Boot).
    public static void main(String[] args) {
        System.out.println("DEBUG (InspeccionApp): Entrando al main() de InspeccionApp (uso alternativo/standalone).");
        // Si se ejecuta este main directamente, 'applicationContext' será null, así que ten cuidado.
        // Es preferible que PpaiApplication sea el punto de entrada.
        launch(args);
        System.out.println("DEBUG (InspeccionApp): launch() ha terminado en main() de InspeccionApp.");
    }
}
