package dsi.ppai.controllers;

import dsi.ppai.entities.OrdenDeInspeccion;
import dsi.ppai.services.GestorInspeccion;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Controller;

@Controller
public class OrdenSeleccionController extends BaseController {

    private final GestorInspeccion gestor;

    @FXML private TableView<OrdenDeInspeccion> tablaOrdenes;
    @FXML private TableColumn<OrdenDeInspeccion, Integer> colNumero;
    @FXML private TableColumn<OrdenDeInspeccion, String> colEstacion;
    @FXML private TableColumn<OrdenDeInspeccion, String> colSismografo;

    public OrdenSeleccionController(ApplicationContext context, GestorInspeccion gestor) {
        super(context);
        this.gestor = gestor;
    }

    @FXML
    public void initialize() {
        configurarTabla();
        cargarOrdenes();
    }

    private void configurarTabla() {
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colEstacion.setCellValueFactory(cellData ->
                cellData.getValue().getEstacion().getNombreProperty());
        colSismografo.setCellValueFactory(cellData ->
                cellData.getValue().getSismografo().getIdProperty());
    }

    private void cargarOrdenes() {
        var empleado = gestor.obtenerEmpleadoLogueado();
        var ordenes = gestor.buscarOrdenesInspeccionDeRI(empleado.getLegajo())
                .stream()
                .filter(OrdenDeInspeccion::sosCompletamenteRealizada)
                .toList();

        tablaOrdenes.setItems(FXCollections.observableArrayList(ordenes));
    }

    @FXML
    private void seleccionarOrden() {
        OrdenDeInspeccion orden = tablaOrdenes.getSelectionModel().getSelectedItem();
        if (orden != null) {
            abrirPantallaCierre(orden);
        }
    }

    private void abrirPantallaCierre(OrdenDeInspeccion orden) {
        try {
            OrdenCierreController controller = context.getBean(OrdenCierreController.class);
            controller.setOrden(orden);

            Parent view = controller.loadView("/dsi/ppai/views/orden-cierre.fxml");

            Stage stage = new Stage();
            stage.setScene(new Scene(view));
            stage.setTitle("Cerrar Orden #" + orden.getNumero());
            stage.show();

            // Cerrar esta ventana
            tablaOrdenes.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
            // Manejar error
        }
    }
}