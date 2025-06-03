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

import java.util.stream.Collectors;

@Component
public class InterfazInspeccion {

    private final GestorInspeccion gestorInspeccion;
    private final Sesion sesion;
    private final RepositorioEmpleados repoEmpleados;
    private ObservableList<OrdenDeInspeccion> observableOrdenes;
    private TableView<OrdenDeInspeccion> tablaOrdenes;
    private Label labelUsuarioLogueado;
    private ComboBox<Empleado> cmbEmpleados;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Autowired
    public InterfazInspeccion(GestorInspeccion gestorInspeccion, Sesion sesion, RepositorioEmpleados repoEmpleados) {
        this.gestorInspeccion = gestorInspeccion;
        this.sesion = sesion;
        this.repoEmpleados = repoEmpleados;
    }

    public void start(Stage primaryStage) {
        simularLogin("1001");

        // Menu Principal
        primaryStage.setTitle("Menú Principal - Sistema de Inspección");

        VBox mainMenuLayout = new VBox(20);
        mainMenuLayout.setAlignment(Pos.CENTER);
        mainMenuLayout.setPadding(new Insets(50));

        Label welcomeLabel = new Label("SalesForce Red Sísmica");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button btnCerrarOrdenMenu = new Button("Cerrar Orden de Inspección");
        btnCerrarOrdenMenu.setPrefSize(250, 40);
        btnCerrarOrdenMenu.setStyle("-fx-font-size: 14px;");

        Button btnExitMenu = new Button("Salir del Sistema");
        btnExitMenu.setPrefSize(250, 40);
        btnExitMenu.setStyle("-fx-font-size: 14px;");

        mainMenuLayout.getChildren().addAll(welcomeLabel, btnCerrarOrdenMenu, btnExitMenu);

        Scene mainMenuScene = new Scene(mainMenuLayout, 400, 300);
        primaryStage.setScene(mainMenuScene);
        primaryStage.show();

        // botón Cerrar Orden de Inspección"
        btnCerrarOrdenMenu.setOnAction(e -> {
            primaryStage.hide();
            mostrarInterfazInspeccion(primaryStage);
        });

        // botón Salir del menú
        btnExitMenu.setOnAction(e -> Platform.exit());
    }

    // mostrar la interfaz de inspección
    private void mostrarInterfazInspeccion(Stage primaryStage) {
        Stage inspeccionStage = new Stage();
        inspeccionStage.setTitle("Sistema de Cierre de Órdenes de Inspección");
        inspeccionStage.initModality(Modality.APPLICATION_MODAL);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        labelUsuarioLogueado = new Label("Usuario: No logueado");
        if (sesion.obtenerEmpleadoLogueado() != null) {
            labelUsuarioLogueado.setText("Usuario: " + sesion.obtenerEmpleadoLogueado().getNombre() + " (Legajo: " + sesion.obtenerEmpleadoLogueado().getLegajo() + ")");
        }
        HBox topBox = new HBox(labelUsuarioLogueado);
        topBox.setPadding(new Insets(5));
        topBox.setAlignment(Pos.CENTER_LEFT);

        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.setPadding(new Insets(0, 0, 10, 0));
        Label lblSelectEmployee = new Label("Ver órdenes de:");
        cmbEmpleados = new ComboBox<>();
        cargarEmpleadosEnComboBox();
        cmbEmpleados.setPromptText("Seleccione un Empleado");
        cmbEmpleados.valueProperty().addListener((obs, oldVal, newVal) -> {
            mostrarOrdCompRealizadas(newVal);
        });

        filterBox.getChildren().addAll(lblSelectEmployee, cmbEmpleados);

        VBox topCombinedBox = new VBox(5, topBox, filterBox);
        root.setTop(topCombinedBox);
        tablaOrdenes = new TableView<>();
        setupTablaOrdenes();
        root.setCenter(tablaOrdenes);

        Button btnCerrarOrden = new Button("Cerrar Orden Seleccionada");
        btnCerrarOrden.setOnAction(e -> iniciarCierreOrdenInspeccion());
        Button btnSalir = new Button("Salir");
        btnSalir.setOnAction(e -> {
            inspeccionStage.close();
            primaryStage.show();
        });
        HBox bottomBox = new HBox(10, btnCerrarOrden, btnSalir);
        bottomBox.setPadding(new Insets(10, 0, 0, 0));
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        root.setBottom(bottomBox);
        Scene scene = new Scene(root, 900, 600);
        inspeccionStage.setScene(scene);
        inspeccionStage.show();
        mostrarOrdCompRealizadas(null);
    }

    private <T, S> TableCell<T, S> createCenteredCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(S item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
                setAlignment(Pos.CENTER);
            }
        };
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
        tablaOrdenes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<OrdenDeInspeccion, Long> colNumOrden = new TableColumn<>("Nº Orden");
        colNumOrden.setCellValueFactory(new PropertyValueFactory<>("numOrden"));
        colNumOrden.setPrefWidth(80);
        colNumOrden.setCellFactory(tc -> createCenteredCell());


        TableColumn<OrdenDeInspeccion, String> colEstacion = new TableColumn<>("Estación Sismológica");
        colEstacion.setCellValueFactory(cellData -> {
            OrdenDeInspeccion orden = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(orden.getEstacionSismologica().getNombre());
        });
        colEstacion.setPrefWidth(150);
        colEstacion.setCellFactory(tc -> createCenteredCell());


        TableColumn<OrdenDeInspeccion, Integer> colSismografoId = new TableColumn<>("Sismógrafo ID");
        colSismografoId.setCellValueFactory(cellData -> {
            OrdenDeInspeccion orden = cellData.getValue();
            return new javafx.beans.property.SimpleObjectProperty<>(orden.getEstacionSismologica().getSismografo().getIdentificadorSismografo());
        });
        colSismografoId.setPrefWidth(120);
        colSismografoId.setCellFactory(tc -> createCenteredCell());


        TableColumn<OrdenDeInspeccion, String> colEstado = new TableColumn<>("Estado Actual");
        colEstado.setCellValueFactory(cellData -> {
            OrdenDeInspeccion orden = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(orden.getEstado().getNombre());
        });
        colEstado.setPrefWidth(120);
        colEstado.setCellFactory(tc -> createCenteredCell());


        TableColumn<OrdenDeInspeccion, String> colFechaFin = new TableColumn<>("Fecha Finalización");
        colFechaFin.setCellValueFactory(cellData -> {
            OrdenDeInspeccion orden = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    orden.getFechaHoraFinalizacion() != null ? orden.getFechaHoraFinalizacion().format(DATE_TIME_FORMATTER) : "N/A"
            );
        });
        colFechaFin.setPrefWidth(150);
        colFechaFin.setCellFactory(tc -> createCenteredCell());


        tablaOrdenes.getColumns().addAll(colNumOrden, colEstacion, colSismografoId, colEstado, colFechaFin);
    }

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

    private void mostrarOrdCompRealizadas(Empleado empleadoSeleccionado) {
        try {
            List<OrdenDeInspeccion> ordenes;
            ordenes = gestorInspeccion.ordenarPorFechaDeFinalizacion(gestorInspeccion.buscarOrdenesDeInspeccionDeRI(empleadoSeleccionado));
            observableOrdenes = FXCollections.observableArrayList(ordenes);
            tablaOrdenes.setItems(observableOrdenes);
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
        List<MotivoFueraServicio> motivosParaSismografo = mostrarMotivosTiposFueraServicios(motivosDisponibles);

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
                mostrarOrdCompRealizadas(cmbEmpleados.getSelectionModel().getSelectedItem());
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Orden de Inspección Nº " + ordenSeleccionada.getNumOrden() + " cerrada exitosamente.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error al Cerrar Orden", "Ocurrió un error al intentar cerrar la orden: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Información", "Cierre de orden cancelado.");
        }
    }

    private List<MotivoFueraServicio> mostrarMotivosTiposFueraServicios(List<MotivoTipo> motivosDisponibles) {
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

        return resultMotivos.get();
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