package dsi.ppai.frontend;

import dsi.ppai.entities.OrdenDeInspeccion; // Asumiendo esta ruta
import dsi.ppai.services.GestorInspeccion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MainScreenController {

    @Autowired
    private GestorInspeccion gestorInspeccion;

    @FXML
    private TableView<OrdenDeInspeccion> ordenesTableView;
    @FXML
    private TableColumn<OrdenDeInspeccion, Long> numeroOrdenCol;
    @FXML
    private TableColumn<OrdenDeInspeccion, LocalDateTime> fechaInicioCol;
    @FXML
    private TableColumn<OrdenDeInspeccion, String> estadoCol; // Asumiendo que Estado tiene un toString o un campo de nombre
    @FXML
    private Label statusLabel;


    @FXML
    public void initialize() {
        // Configura las fábricas de valores de celda para las columnas de la TableView
        numeroOrdenCol.setCellValueFactory(new PropertyValueFactory<>("numeroOrden"));
        fechaInicioCol.setCellValueFactory(new PropertyValueFactory<>("fechaHoraInicio"));
        // Para objetos complejos como Estado, es posible que necesites una fábrica de celdas personalizada o asegurarte de que Estado tenga un campo 'nombre'
        estadoCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getEstado() != null) {
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEstado().getNombre());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        // Carga inicial (opcional, es posible que quieras cargar cuando el usuario haga clic en un botón)
        // handleBuscarOrdenes();
    }

    @FXML
    private void handleBuscarOrdenes() {
        try {
            // Llama al servicio del backend para obtener las órdenes
            // Nota: El método `buscarOrdenesInspeccionDeRI` en GestorInspeccion
            // actualmente solo filtra y devuelve una lista, pero no la almacena
            // ni la devuelve al llamador. Deberás modificar GestorInspeccion
            // para que devuelva la lista filtrada para que la interfaz de usuario pueda mostrarla.
            //
            // Por ahora, asumamos que GestorInspeccion devuelve un List<OrdenDeInspeccion>
            List<OrdenDeInspeccion> ordenes = gestorInspeccion.buscarOrdenesInspeccionDeRI();

            if (ordenes != null && !ordenes.isEmpty()) {
                ObservableList<OrdenDeInspeccion> observableOrdenes = FXCollections.observableArrayList(ordenes);
                ordenesTableView.setItems(observableOrdenes);
                statusLabel.setText("Se encontraron " + ordenes.size() + " órdenes.");
            } else {
                ordenesTableView.setItems(FXCollections.emptyObservableList());
                statusLabel.setText("No se encontraron órdenes de inspección completamente realizadas.");
            }
        } catch (Exception e) {
            statusLabel.setText("Error al buscar órdenes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}