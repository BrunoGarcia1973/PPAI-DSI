package dsi.ppai;

import dsi.ppai.entities.*;
import dsi.ppai.repositories.RepositorioEmpleados;
import dsi.ppai.services.GestorInspeccion;
import dsi.ppai.entities.Sesion;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import java.util.stream.Collectors; // Asegúrate de importar esto

@Component
public class InterfazInspeccion {

    private final GestorInspeccion gestorInspeccion;
    private final Sesion sesion;
    private final RepositorioEmpleados repoEmpleados;
    private ObservableList<OrdenDeInspeccion> observableOrdenes;
    private TableView<OrdenDeInspeccion> tablaOrdenes;
    private Label labelUsuarioLogueado;
    private ComboBox<Empleado> cmbEmpleados; // Nuevo ComboBox para seleccionar empleados
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Autowired
    public InterfazInspeccion(GestorInspeccion gestorInspeccion, Sesion sesion, RepositorioEmpleados repoEmpleados) {
        this.gestorInspeccion = gestorInspeccion;
        this.sesion = sesion;
        this.repoEmpleados = repoEmpleados;
    }

    public void start(Stage primaryStage) {
        simularLogin("1001"); // Logueamos a Juan Pérez (legajo 1001)

        primaryStage.setTitle("Sistema de Cierre de Órdenes de Inspección");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        labelUsuarioLogueado = new Label("Usuario: No logueado");
        if (sesion.obtenerEmpleadoLogueado() != null) {
            labelUsuarioLogueado.setText("Usuario: " + sesion.obtenerEmpleadoLogueado().getNombre() + " (Legajo: " + sesion.obtenerEmpleadoLogueado().getLegajo() + ")");
        }
        HBox topBox = new HBox(labelUsuarioLogueado);
        topBox.setPadding(new Insets(5));
        topBox.setAlignment(Pos.CENTER_LEFT);

        // --- Filter Section: Employee Selection ---
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.setPadding(new Insets(0, 0, 10, 0));
        Label lblSelectEmployee = new Label("Ver órdenes de:");
        cmbEmpleados = new ComboBox<>();
        cargarEmpleadosEnComboBox(); // Cargar la lista de empleados
        cmbEmpleados.setPromptText("Seleccione un Empleado"); // Texto por defecto
        // Listener para cuando se selecciona un empleado
        cmbEmpleados.valueProperty().addListener((obs, oldVal, newVal) -> {
            cargarOrdenes(newVal); // Recargar la tabla con el empleado seleccionado
        });

        filterBox.getChildren().addAll(lblSelectEmployee, cmbEmpleados);

        VBox topCombinedBox = new VBox(5, topBox, filterBox); // Combinar info de usuario y filtro
        root.setTop(topCombinedBox);
        // --- Center Section: Orders Table ---
        tablaOrdenes = new TableView<>();
        setupTablaOrdenes();
        root.setCenter(tablaOrdenes);
        // --- Bottom Section: Buttons ---
        Button btnCerrarOrden = new Button("Cerrar Orden Seleccionada");
        btnCerrarOrden.setOnAction(e -> iniciarCierreOrdenInspeccion());
        Button btnSalir = new Button("Salir");
        btnSalir.setOnAction(e -> Platform.exit());
        HBox bottomBox = new HBox(10, btnCerrarOrden, btnSalir);
        bottomBox.setPadding(new Insets(10, 0, 0, 0));
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        root.setBottom(bottomBox);
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        // Cargar órdenes inicialmente para el usuario logueado
        cargarOrdenes(null);
    }

    private void simularLogin(String legajo) {
        System.out.println("--- SIMULANDO LOGIN ---");
        try {
            Usuario usuario = new Usuario("usuario" + legajo, repoEmpleados.buscarEmpleadoPorLegajo(legajo));
            if (usuario.getEmpleado() == null) {
                showAlert(Alert.AlertType.ERROR, "Error de Login", "No se encontró el empleado con legajo: " + legajo);
                Platform.exit();
            }
            sesion.setUsuarioLogueado(usuario);
            System.out.println("Empleado '" + sesion.obtenerEmpleadoLogueado().getNombre() + "' logueado exitosamente.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error de Login", "Error durante el login simulado: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }

    private void setupTablaOrdenes() {
        TableColumn<OrdenDeInspeccion, Long> colNumOrden = new TableColumn<>("Nº Orden");
        colNumOrden.setCellValueFactory(new PropertyValueFactory<>("numOrden"));
        colNumOrden.setPrefWidth(80);

        TableColumn<OrdenDeInspeccion, String> colEstacion = new TableColumn<>("Estación Sismológica");
        colEstacion.setCellValueFactory(cellData -> {
            OrdenDeInspeccion orden = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(orden.getEstacionSismologica().getNombre());
        });
        colEstacion.setPrefWidth(150);

        TableColumn<OrdenDeInspeccion, Integer> colSismografoId = new TableColumn<>("Sismógrafo ID");
        colSismografoId.setCellValueFactory(cellData -> {
            OrdenDeInspeccion orden = cellData.getValue();
            return new javafx.beans.property.SimpleObjectProperty<>(orden.getEstacionSismologica().getSismografo().getIdentificadorSismografo());
        });
        colSismografoId.setPrefWidth(120);

        TableColumn<OrdenDeInspeccion, String> colEstado = new TableColumn<>("Estado Actual");
        colEstado.setCellValueFactory(cellData -> {
            OrdenDeInspeccion orden = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(orden.getEstado().getNombre());
        });
        colEstado.setPrefWidth(120);

        TableColumn<OrdenDeInspeccion, String> colFechaFin = new TableColumn<>("Fecha Finalización");
        colFechaFin.setCellValueFactory(cellData -> {
            OrdenDeInspeccion orden = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    orden.getFechaHoraFinalizacion() != null ? orden.getFechaHoraFinalizacion().format(DATE_TIME_FORMATTER) : "N/A"
            );
        });
        colFechaFin.setPrefWidth(150);

        TableColumn<OrdenDeInspeccion, String> colObservacionCierre = new TableColumn<>("Obs. Cierre");
        colObservacionCierre.setCellValueFactory(new PropertyValueFactory<>("observacionCierre"));
        colObservacionCierre.setPrefWidth(370);

        tablaOrdenes.getColumns().addAll(colNumOrden, colEstacion, colSismografoId, colEstado, colFechaFin, colObservacionCierre);
    }

    // Método para cargar todos los empleados en el ComboBox
    private void cargarEmpleadosEnComboBox() {
        try {
            List<Empleado> todosLosEmpleados = repoEmpleados.findAll();

            cmbEmpleados.setCellFactory(lv -> new ListCell<Empleado>() {
                @Override
                protected void updateItem(Empleado empleado, boolean empty) {
                    super.updateItem(empleado, empty);
                    setText(empty ? "" : empleado.getNombre() + " " + empleado.getApellido());
                }
            });
            cmbEmpleados.setButtonCell(new ListCell<Empleado>() {
                @Override
                protected void updateItem(Empleado empleado, boolean empty) {
                    super.updateItem(empleado, empty);
                    setText(empty ? "Seleccione un Empleado" : empleado.getNombre() + " " + empleado.getApellido());
                }
            });

            cmbEmpleados.setItems(FXCollections.observableArrayList(todosLosEmpleados));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error al Cargar Empleados", "No se pudieron cargar los empleados: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Método de carga de órdenes
    private void cargarOrdenes(Empleado empleadoSeleccionado) {
        try {
            List<OrdenDeInspeccion> ordenes;
            if (empleadoSeleccionado == null) {
                // Si no hay un empleado seleccionado en el ComboBox, se muestran las del RI logueado
                ordenes = gestorInspeccion.buscarOrdenesInspeccionDeRI();
                //showAlert(Alert.AlertType.INFORMATION, "Información", "Mostrando órdenes para el Responsable de Inspección logueado: " + sesion.obtenerEmpleadoLogueado().getNombre() + " " + sesion.obtenerEmpleadoLogueado().getApellido());
            } else {
                // Si hay un empleado seleccionado, se buscan sus órdenes usando el nuevo método
                ordenes = gestorInspeccion.buscarOrdenesDeInspeccionDeRI(empleadoSeleccionado);
                //showAlert(Alert.AlertType.INFORMATION, "Información", "Mostrando órdenes para: " + empleadoSeleccionado.getNombre() + " " + empleadoSeleccionado.getApellido());
            }

            observableOrdenes = FXCollections.observableArrayList(ordenes);
            tablaOrdenes.setItems(observableOrdenes);

            if (ordenes.isEmpty()) {
                if (empleadoSeleccionado == null) {
                    showAlert(Alert.AlertType.INFORMATION, "Información", "No se encontraron órdenes de inspección 'Completamente Realizadas' para el Responsable de Inspección logueado.");
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Información", "No se encontraron órdenes de inspección 'Completamente Realizadas' para el empleado seleccionado: " + empleadoSeleccionado.getNombre() + " " + empleadoSeleccionado.getApellido());
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error al Cargar Órdenes", "No se pudieron cargar las órdenes de inspección: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void iniciarCierreOrdenInspeccion() {
        OrdenDeInspeccion ordenSeleccionada = tablaOrdenes.getSelectionModel().getSelectedItem();
        if (ordenSeleccionada == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione una orden de inspección de la tabla.");
            return;
        }
        // Solo se pueden cerrar órdenes que estén 'Completamente Realizadas'
        if (!ordenSeleccionada.sosCompletamenteRealizada()) {
            showAlert(Alert.AlertType.ERROR, "Error de Estado", "La orden seleccionada no está 'Completamente Realizada' y no puede ser cerrada.");
            return;
        }

        Dialog<String> dialogObservacion = new Dialog<>();
        dialogObservacion.setTitle("Cerrar Orden de Inspección");
        dialogObservacion.setHeaderText("Cierre de Orden Nº " + ordenSeleccionada.getNumOrden());

        ButtonType okButtonType = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        dialogObservacion.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        VBox content = new VBox(5);
        Label label = new Label("Ingrese la observación de cierre:");
        TextArea textArea = new TextArea();
        textArea.setPromptText("Observación");
        textArea.setWrapText(true);
        textArea.setPrefRowCount(5);
        textArea.setPrefColumnCount(30);

        content.getChildren().addAll(label, textArea);
        dialogObservacion.getDialogPane().setContent(content);

        Platform.runLater(textArea::requestFocus);

        dialogObservacion.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return textArea.getText();
            }
            return null;
        });

        Optional<String> resultObservacion = dialogObservacion.showAndWait();

        if (resultObservacion.isEmpty() || resultObservacion.get().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "La observación de cierre no puede estar vacía. Operación cancelada.");
            return;
        }
        String observacion = resultObservacion.get().trim();

        List<MotivoTipo> motivosDisponibles = gestorInspeccion.buscarTiposMotivosFueraDeServicios();
        List<MotivoFueraServicio> motivosParaSismografo = showMotivosDialog(motivosDisponibles);

        // Si el usuario cancela el diálogo de motivos
        if (motivosParaSismografo == null) {
            showAlert(Alert.AlertType.INFORMATION, "Información", "Operación de cierre de orden cancelada.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Cierre de Orden");
        confirmAlert.setHeaderText("¿Está seguro de cerrar la Orden Nº " + ordenSeleccionada.getNumOrden() + "?");
        String contentConfirmation = "Observación: " + observacion + "\n";
        if (!motivosParaSismografo.isEmpty()) {
            contentConfirmation += "El sismógrafo será puesto FUERA DE SERVICIO con los siguientes motivos:\n" +
                    motivosParaSismografo.stream()
                            .map(mf -> "- " + mf.getMotivoTipo().getDescripcion() + (mf.getComentario() != null && !mf.getComentario().isEmpty() ? " (" + mf.getComentario() + ")" : ""))
                            .collect(Collectors.joining("\n"));
        } else {
            contentConfirmation += "El sismógrafo NO será puesto FUERA DE SERVICIO.";
        }
        confirmAlert.setContentText(contentConfirmation);

        Optional<ButtonType> confirmResult = confirmAlert.showAndWait();
        if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
            try {
                gestorInspeccion.cerrarOrden(ordenSeleccionada, observacion, motivosParaSismografo);
                // Después de cerrar, recargar para el empleado que estaba seleccionado (o el logueado)
                cargarOrdenes(cmbEmpleados.getSelectionModel().getSelectedItem());
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Orden de Inspección Nº " + ordenSeleccionada.getNumOrden() + " cerrada exitosamente.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error al Cerrar Orden", "Ocurrió un error al intentar cerrar la orden: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Información", "Cierre de orden cancelado.");
        }
    }

    private List<MotivoFueraServicio> showMotivosDialog(List<MotivoTipo> motivosDisponibles) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Motivos Fuera de Servicio");

        VBox dialogVBox = new VBox(10);
        dialogVBox.setPadding(new Insets(20));

        Label titleLabel = new Label("¿Desea poner el sismógrafo fuera de servicio?");
        CheckBox chkPonerFueraServicio = new CheckBox("Sí, poner fuera de servicio");

        GridPane motivosGrid = new GridPane();
        motivosGrid.setHgap(10);
        motivosGrid.setVgap(5);
        motivosGrid.setPadding(new Insets(10, 0, 0, 0));

        List<MotivoTipoWrapper> motivoWrappers = new ArrayList<>();

        int row = 0;
        for (MotivoTipo motivo : motivosDisponibles) {
            CheckBox chkMotivo = new CheckBox(motivo.getDescripcion());
            TextField txtComentario = new TextField();
            txtComentario.setPromptText("Comentario");
            txtComentario.setDisable(true);

            chkMotivo.selectedProperty().addListener((obs, oldVal, newVal) -> txtComentario.setDisable(!newVal));

            motivosGrid.add(chkMotivo, 0, row);
            motivosGrid.add(txtComentario, 1, row);
            motivoWrappers.add(new MotivoTipoWrapper(motivo, chkMotivo, txtComentario));
            row++;
        }
        motivosGrid.setDisable(true);

        chkPonerFueraServicio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            motivosGrid.setDisable(!newVal);
            if (!newVal) {
                motivoWrappers.forEach(mw -> {
                    mw.getCheckBox().setSelected(false);
                    mw.getTextField().setText("");
                });
            }
        });

        Button btnAceptar = new Button("Aceptar");
        Button btnCancelar = new Button("Cancelar");

        AtomicReference<List<MotivoFueraServicio>> resultMotivos = new AtomicReference<>(new ArrayList<>());
        btnAceptar.setOnAction(e -> {
            if (chkPonerFueraServicio.isSelected()) {
                boolean alMenosUnMotivoSeleccionado = false;
                for (MotivoTipoWrapper mw : motivoWrappers) {
                    if (mw.getCheckBox().isSelected()) {
                        alMenosUnMotivoSeleccionado = true;
                        resultMotivos.get().add(new MotivoFueraServicio(mw.getTextField().getText().trim(), mw.getMotivoTipo()));
                    }
                }
                if (!alMenosUnMotivoSeleccionado) {
                    showAlert(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar al menos un motivo si desea poner el sismógrafo fuera de servicio.");
                    resultMotivos.get().clear();
                    return;
                }
            }
            dialogStage.close();
        });

        btnCancelar.setOnAction(e -> {
            resultMotivos.set(null);
            dialogStage.close();
        });

        HBox buttonBox = new HBox(10, btnAceptar, btnCancelar);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        dialogVBox.getChildren().addAll(titleLabel, chkPonerFueraServicio, motivosGrid, buttonBox);

        Scene dialogScene = new Scene(dialogVBox);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();

        return resultMotivos.get(); // Retorna la lista de motivos o null si se canceló
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class MotivoTipoWrapper {
        private final MotivoTipo motivoTipo;
        private final CheckBox checkBox;
        private final TextField textField;

        public MotivoTipoWrapper(MotivoTipo motivoTipo, CheckBox checkBox, TextField textField) {
            this.motivoTipo = motivoTipo;
            this.checkBox = checkBox;
            this.textField = textField;
        }

        public MotivoTipo getMotivoTipo() {
            return motivoTipo;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public TextField getTextField() {
            return textField;
        }
    }
}