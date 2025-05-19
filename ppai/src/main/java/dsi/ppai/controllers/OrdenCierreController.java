package dsi.ppai.controllers;

import dsi.ppai.entities.*;
import dsi.ppai.services.GestorInspeccion;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class OrdenCierreController extends BaseController {

    private final GestorInspeccion gestor;
    private OrdenDeInspeccion orden;

    @FXML private Label lblTitulo;
    @FXML private TextArea txtObservacion;
    @FXML private TableView<MotivoTipoWrapper> tablaMotivos;
    @FXML private TableColumn<MotivoTipoWrapper, Boolean> colSeleccion;
    @FXML private TableColumn<MotivoTipoWrapper, String> colMotivo;
    @FXML private TableColumn<MotivoTipoWrapper, String> colComentario;
    @FXML private Button btnConfirmar;

    public OrdenCierreController(ApplicationContext context, GestorInspeccion gestor) {
        super(context);
        this.gestor = gestor;
    }

    public void setOrden(OrdenDeInspeccion orden) {
        this.orden = orden;
        lblTitulo.setText("Cerrar Orden #" + orden.getNumero());
    }

    @FXML
    public void initialize() {
        configurarTablaMotivos();
        configurarValidaciones();
    }

    private void configurarTablaMotivos() {
        // Configurar columnas
        colSeleccion.setCellFactory(CheckBoxTableCell.forTableColumn(colSeleccion));
        colMotivo.setCellValueFactory(cellData -> cellData.getValue().motivoProperty());
        colComentario.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        colComentario.setCellValueFactory(cellData -> cellData.getValue().comentarioProperty());

        // Cargar datos
        List<MotivoTipo> motivos = gestor.obtenerMotivosFueraServicio();
        tablaMotivos.getItems().addAll(motivos.stream()
                .map(MotivoTipoWrapper::new)
                .collect(Collectors.toList()));

        // Editable
        tablaMotivos.setEditable(true);
    }

    private void configurarValidaciones() {
        txtObservacion.textProperty().addListener((obs, old, newVal) -> validarCampos());
        tablaMotivos.getItems().forEach(item ->
                item.seleccionadoProperty().addListener((obs, old, newVal) -> validarCampos())
        );
    }

    private void validarCampos() {
        boolean observacionValida = !txtObservacion.getText().trim().isEmpty();
        boolean motivosSeleccionados = tablaMotivos.getItems().stream()
                .anyMatch(MotivoTipoWrapper::isSeleccionado);

        btnConfirmar.setDisable(!observacionValida || !motivosSeleccionados);
    }

    @FXML
    private void confirmarCierre() {
        try {
            String observacion = txtObservacion.getText();
            List<MotivoTipo> motivos = tablaMotivos.getItems().stream()
                    .filter(MotivoTipoWrapper::isSeleccionado)
                    .map(MotivoTipoWrapper::getMotivoTipo)
                    .collect(Collectors.toList());

            gestor.cerrarOrden(orden.getNumero(), observacion, motivos);
            mostrarConfirmacion();
            cerrarVentana();
        } catch (Exception e) {
            mostrarError("Error al cerrar orden: " + e.getMessage());
        }
    }

    private void mostrarConfirmacion() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Orden Cerrada");
        alert.setHeaderText("La orden ha sido cerrada exitosamente");
        alert.setContentText("Se ha registrado el cierre y notificado a los responsables.");
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("No se pudo completar la operación");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrarVentana() {
        btnConfirmar.getScene().getWindow().hide();
    }

    // Clase wrapper para motivos en la tabla
    public static class MotivoTipoWrapper {
        private final MotivoTipo motivoTipo;
        private final BooleanProperty seleccionado = new SimpleBooleanProperty();
        private final StringProperty comentario = new SimpleStringProperty("");

        // Constructor, getters, properties...
    }
}