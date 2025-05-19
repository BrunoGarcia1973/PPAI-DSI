package dsi.ppai.controllers;

import dsi.ppai.services.GestorInspeccion;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.stereotype.Controller;

@Controller
public class MainController extends BaseController {

    private final GestorInspeccion gestorInspeccion;

    @FXML private Button btnCerrarOrden;

    public MainController(ApplicationContext context, GestorInspeccion gestorInspeccion) {
        super(context);
        this.gestorInspeccion = gestorInspeccion;
    }

    @FXML
    public void initialize() {
        // Configurar acciones de botones
        btnCerrarOrden.setOnAction(event -> abrirCierreOrden());
    }

    private void abrirCierreOrden() {
        try {
            OrdenSeleccionController controller = context.getBean(OrdenSeleccionController.class);
            Parent view = controller.loadView("/dsi/ppai/views/orden-seleccion.fxml");

            Stage stage = new Stage();
            stage.setScene(new Scene(view));
            stage.setTitle("Seleccionar Orden para Cierre");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Manejar error adecuadamente
        }
    }
}