package dsi.ppai.inicio;
import lombok.Setter;
import lombok.Setter;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework;

@SpringBootAplication
public class FXMain extends Application{
    @Override
    public static void main(String[] args) {
        // Lanza la aplicación JavaFX
        Application.launch(args);
    }
    @Override
    public void init() {
        // Inicialización antes de start()
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Crea y configura la aplicación JavaFX con Spring
        new JavaFXApplication().start(primaryStage);
    }

    @Override
    public void stop() {
        // Código de limpieza al cerrar la aplicación
        System.out.println("Aplicación cerrada");
    }
}
