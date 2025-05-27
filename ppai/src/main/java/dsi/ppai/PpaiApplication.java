package dsi.ppai;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.autoconfigure.SpringBootApplication; // Importar SpringBootApplication

@SpringBootApplication
public class PpaiApplication extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(PpaiApplication.class)
                .web(WebApplicationType.NONE)
                .run();
    }

    @Override
    public void start(Stage primaryStage) {
        // Obtener el bean de ApplicationUI del contexto de Spring
        InterfazInspeccion interfazInspeccion = applicationContext.getBean(InterfazInspeccion.class);
        interfazInspeccion.start(primaryStage); // Iniciar la UI de JavaFX
    }

    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(PpaiApplication.class, args);
    }
}