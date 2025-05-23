package dsi.ppai;

import dsi.ppai.services.GestorInspeccion;
import dsi.ppai.services.Sesion;

import javafx.application.Application;
import javafx.stage.Stage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import dsi.ppai.ApplicationUI; // Importación explícita para ApplicationUI


@SpringBootApplication
public class PpaiApplication extends Application {

    private ConfigurableApplicationContext springContext;
    private static String[] savedArgs;

    public static void main(String[] args) {
        savedArgs = args;
        Application.launch(PpaiApplication.class, args);
    }

    @Override
    public void init() throws Exception {
        springContext = SpringApplication.run(PpaiApplication.class, savedArgs);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ApplicationUI applicationUI = springContext.getBean(ApplicationUI.class);
        applicationUI.start(primaryStage);
    }

    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
    }
}