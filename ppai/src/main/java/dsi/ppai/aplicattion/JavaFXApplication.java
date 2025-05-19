package dsi.ppai.application;

import dsi.ppai.controllers.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFXApplication extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        this.context = new SpringApplicationBuilder(PpaiApplication.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        MainController controller = context.getBean(MainController.class);

        // Cargar el FXML principal
        Parent root = controller.loadView("/dsi/ppai/views/main.fxml");

        // Configurar el escenario principal
        primaryStage.setScene(new Scene(root, 1024, 768));
        primaryStage.setTitle("Sistema de Inspecciones Sismológicas");
        primaryStage.show();
    }

    @Override
    public void stop() {
        this.context.close();
        Platform.exit();
    }
}