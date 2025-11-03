package dsi.ppai;

import dsi.ppai.entities.*;
import dsi.ppai.repositories.RepositorioEmpleados;
import dsi.ppai.repositories.RepositorioUsuarios;
import dsi.ppai.services.GestorInspeccion;
import dsi.ppai.entities.Sesion;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Clases internas necesarias para métodos auxiliares (las definimos al final de la clase)
// import java.util.stream.Collectors;

@Component
public class InterfazInspeccion {

    private final GestorInspeccion gestorInspeccion;
    private final Sesion sesion;
    private final RepositorioEmpleados repoEmpleados;
    private final RepositorioUsuarios repoUsuarios;

    private TableView<OrdenDeInspeccion> tablaOrdenes;
    private ComboBox<Empleado> cmbEmpleados;
    private Label labelUsuarioLogueado;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Autowired
    public InterfazInspeccion(GestorInspeccion gestorInspeccion, Sesion sesion,
                              RepositorioEmpleados repoEmpleados, RepositorioUsuarios repoUsuarios) {
        this.gestorInspeccion = gestorInspeccion;
        this.sesion = sesion;
        this.repoEmpleados = repoEmpleados;
        this.repoUsuarios = repoUsuarios;
    }

    public void start(Stage primaryStage) {
        // 1. INICIAR LOGIN INTERACTIVO.
        if (!solicitarYLoguearEmpleado()) {
            Platform.exit();
            return;
        }

        // 2. CONFIGURACIÓN DE LA VENTANA PRINCIPAL (SOLO SI EL LOGIN FUE EXITOSO)
        primaryStage.setTitle("Sistema de Cierre de Órdenes de Inspección");
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // 3. SECCIÓN SUPERIOR: USUARIO LOGUEADO
        labelUsuarioLogueado = new Label();
        actualizarInfoUsuario();

        HBox topBox = new HBox(labelUsuarioLogueado);
        topBox.setPadding(new Insets(5));
        topBox.setAlignment(Pos.CENTER_LEFT);

        // --- Filter Section: Employee Selection ---
        HBox filterBox = new HBox(10);
        cmbEmpleados = new ComboBox<>();
        // Listener para cuando se selecciona un empleado (IMPLEMENTACIÓN PENDIENTE DE LÓGICA DE FILTRO)
        cmbEmpleados.valueProperty().addListener((obs, oldVal, newVal) -> mostrarOrdCompRealizadas(newVal));

        filterBox.getChildren().addAll(new Label("Ver órdenes de:"), cmbEmpleados);
        VBox topCombinedBox = new VBox(5, topBox, filterBox);
        root.setTop(topCombinedBox);

        // 4. SECCIÓN CENTRAL: TABLA
        tablaOrdenes = new TableView<>();
        setupTablaOrdenes(); // <--- IMPLEMENTADO
        root.setCenter(tablaOrdenes);

        // 5. SECCIÓN INFERIOR: BOTONES
        Button btnCerrarOrden = new Button("Cerrar Orden Seleccionada");
        btnCerrarOrden.setOnAction(e -> iniciarCierreOrdenInspeccion()); // Lógica pendiente
        Button btnSalir = new Button("Salir");
        btnSalir.setOnAction(e -> Platform.exit());
        HBox bottomBox = new HBox(10, btnCerrarOrden, btnSalir);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        // 6. CARGAR DATOS INICIALES
        mostrarOrdCompRealizadas(null); // <--- IMPLEMENTADO
    }

    // --- MÉTODOS DE BÚSQUEDA Y TABLA ---

    private void setupTablaOrdenes() {
        tablaOrdenes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<OrdenDeInspeccion, Long> colNumOrden = new TableColumn<>("Nº Orden");
        colNumOrden.setCellValueFactory(new PropertyValueFactory<>("numOrden"));
        colNumOrden.setPrefWidth(80);

        // Columna para la Estación Sismológica (Accede a la relación anidada)
        TableColumn<OrdenDeInspeccion, String> colEstacion = new TableColumn<>("Estación Sismológica");
        colEstacion.setCellValueFactory(cellData -> {
            OrdenDeInspeccion orden = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    orden.getEstacionSismologica() != null ? orden.getEstacionSismologica().getNombre() : "N/A"
            );
        });
        colEstacion.setPrefWidth(150);

        // Columna para el Sismógrafo ID (Accede a la relación anidada)
        TableColumn<OrdenDeInspeccion, String> colSismografoId = new TableColumn<>("Sismógrafo ID");
        colSismografoId.setCellValueFactory(cellData -> {
            OrdenDeInspeccion orden = cellData.getValue();
            Sismografo sismografo = (orden.getEstacionSismologica() != null) ? orden.getEstacionSismologica().getSismografo() : null;
            return new javafx.beans.property.SimpleStringProperty(
                    sismografo != null ? sismografo.getIdentificadorSismografo() : "N/A"
            );
        });
        colSismografoId.setPrefWidth(120);

        // Columna para el Estado Actual (Accede a la relación)
        TableColumn<OrdenDeInspeccion, String> colEstado = new TableColumn<>("Estado Actual");
        colEstado.setCellValueFactory(cellData -> {
            OrdenDeInspeccion orden = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    orden.getEstado() != null ? orden.getEstado().getNombre() : "N/A"
            );
        });
        colEstado.setPrefWidth(120);

        // Columna para la Fecha de Finalización
        TableColumn<OrdenDeInspeccion, String> colFechaFin = new TableColumn<>("Fecha Finalización");
        colFechaFin.setCellValueFactory(cellData -> {
            OrdenDeInspeccion orden = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    orden.getFechaHoraFinalizacion() != null ? orden.getFechaHoraFinalizacion().format(DATE_TIME_FORMATTER) : "N/A"
            );
        });
        colFechaFin.setPrefWidth(150);

        tablaOrdenes.getColumns().addAll(colNumOrden, colEstacion, colSismografoId, colEstado, colFechaFin);
    }

    // En InterfazInspeccion.java

    private void mostrarOrdCompRealizadas(Empleado empleadoSeleccionado) {
        try {
            List<OrdenDeInspeccion> ordenes;
            Empleado empleadoLogueado = sesion.obtenerEmpleadoLogueado();

            // ... (lógica de obtención de órdenes y validación)

            // Determina qué empleado usar para el filtro
            Empleado empleadoFiltro = (empleadoSeleccionado != null) ? empleadoSeleccionado : empleadoLogueado;

            if (empleadoSeleccionado == null) {
                System.out.println("DEBUG: Buscando órdenes para RI Logueado (ID: " + empleadoFiltro.getId() + ")");
                ordenes = gestorInspeccion.buscarOrdenesInspeccionDeRI();
            } else {
                System.out.println("DEBUG: Buscando órdenes para Empleado Seleccionado (ID: " + empleadoFiltro.getId() + ")");
                ordenes = gestorInspeccion.buscarOrdenesDeInspeccionDeRI(empleadoFiltro);
            }

            if (ordenes == null) {
                ordenes = new ArrayList<>();
            }

            System.out.println("DEBUG: Gestor devolvió " + ordenes.size() + " órdenes.");

            // *** CÓDIGO DE DIAGNÓSTICO AÑADIDO ***
            if (!ordenes.isEmpty()) {
                System.out.println("--- DIAGNÓSTICO DE DATOS ---");
                for (OrdenDeInspeccion orden : ordenes) {
                    // Imprimimos solo los campos básicos, evitando getters de relaciones LAZY
                    System.out.println(
                            "| ORDEN N°: " + orden.getNumOrden() +
                                    " | ID: " + orden.getId() +
                                    " | FECHA FIN: " + orden.getFechaHoraFinalizacion() +
                                    " | ESTADO: " + (orden.getEstado() != null ? orden.getEstado().getNombre() : "ERROR: Estado NULL")
                            // No intentar acceder aquí a Sismografo/EstacionSismologica si son LAZY
                    );
                }
                System.out.println("---------------------------");
            }
            // **********************************

            ObservableList<OrdenDeInspeccion> observableOrdenes = FXCollections.observableArrayList(ordenes);
            tablaOrdenes.setItems(observableOrdenes);

            if (ordenes.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Información", "No se encontraron órdenes de inspección 'Completamente Realizadas' para este empleado.");
            }
        } catch (Exception e) {
            // ... (manejo de excepciones)
        }
    }


    // --- MÉTODOS DE LOGIN Y AUXILIARES (SE MANTIENEN IGUAL) ---

    private void actualizarInfoUsuario() {
        Empleado empleado = sesion.obtenerEmpleadoLogueado();
        if (empleado != null) {
            labelUsuarioLogueado.setText("Usuario: " + empleado.getNombre() + " " + empleado.getApellido() + " (Legajo: " + empleado.getLegajo() + ")");
        } else {
            labelUsuarioLogueado.setText("Usuario: No logueado");
        }
    }

    private boolean solicitarYLoguearEmpleado() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Inicio de Sesión");
        dialog.setHeaderText("Ingrese su nombre de usuario (ej: usuario1001) para iniciar sesión.");
        dialog.setContentText("Nombre de Usuario:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String nombreUsuarioIngresado = result.get().trim();
            System.out.println("--- LOG: Intentando LOGIN con Nombre de Usuario: " + nombreUsuarioIngresado + " ---");
            try {
                Optional<Usuario> usuarioOpt = gestorInspeccion.buscarUsuarioPorNombre(nombreUsuarioIngresado); // Usamos el Gestor

                if (usuarioOpt.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Error de Login", "El nombre de usuario no existe. Por favor, intente de nuevo.");
                    return solicitarYLoguearEmpleado();
                }

                Usuario usuario = usuarioOpt.get();
                Empleado empleado = usuario.getEmpleado();

                if (empleado == null) {
                    showAlert(Alert.AlertType.ERROR, "Error de Configuración", "El usuario existe, pero no tiene un empleado asociado.");
                    return false;
                }

                sesion.setUsuarioLogueado(usuario);
                System.out.println("--- LOG: Sesion creada. Empleado logueado: " + empleado.getNombre() + " ---");
                return true;

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error de Login", "Error durante la consulta de usuarios: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

// En InterfazInspeccion.java

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

        // --- Paso 1: Solicitar Observación de Cierre (Paso 4 & 5) ---
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
        textArea.setPrefColumnCount(30); // Añadido para mejor visualización
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

        // --- Paso 2: Preguntar por Motivos Fuera de Servicio (Paso 6 & 7) ---
        // Buscar tipos de motivos disponibles
        List<MotivoTipo> motivosDisponibles = gestorInspeccion.buscarTiposMotivosFueraDeServicios();
        List<MotivoFueraServicio> motivosParaSismografo = mostrarMotivosTiposFueraServicios(motivosDisponibles);

        // Si el usuario cancela el diálogo de motivos
        if (motivosParaSismografo == null) {
            showAlert(Alert.AlertType.INFORMATION, "Información", "Operación de cierre de orden cancelada.");
            return;
        }

        // --- Paso 3: Confirmación Final (Paso 8 & 9) ---
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Cierre de Orden");
        confirmAlert.setHeaderText("¿Está seguro de cerrar la Orden Nº " + ordenSeleccionada.getNumOrden() + "?");
        String contentConfirmation = "Observación: " + observacion + "\n";

        // (Se requiere java.util.stream.Collectors)
        if (!motivosParaSismografo.isEmpty()) {
            contentConfirmation += "El sismógrafo será puesto FUERA DE SERVICIO con los siguientes motivos:\n" +
                    motivosParaSismografo.stream()
                            .map(mf -> "- " + mf.getMotivoTipo().getDescripcion() + (mf.getComentario() != null && !mf.getComentario().isEmpty() ? " (" + mf.getComentario() + ")" : ""))
                            .collect(java.util.stream.Collectors.joining("\n"));
        } else {
            contentConfirmation += "El sismógrafo NO será puesto FUERA DE SERVICIO.";
        }
        confirmAlert.setContentText(contentConfirmation);

        Optional<ButtonType> confirmResult = confirmAlert.showAndWait();

        // --- Paso 4: Ejecutar Cierre y Recargar (Paso 11, 12, 13) ---
        if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
            try {
                // Llama al Gestor para ejecutar la lógica de negocio (Cerrar Orden, Actualizar Sismógrafo, Notificar)
                gestorInspeccion.cerrarOrden(ordenSeleccionada.getNumOrden(), observacion, motivosParaSismografo);

                // Recargar la tabla para el empleado que estaba seleccionado (o el logueado)
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
    // En InterfazInspeccion.java

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

        // Necesitas una lista para guardar los wrappers (MotivoTipo, Checkbox, Textfield)
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

        // Listener para habilitar/deshabilitar la grilla completa
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

        java.util.concurrent.atomic.AtomicReference<List<MotivoFueraServicio>> resultMotivos = new java.util.concurrent.atomic.AtomicReference<>(new ArrayList<>());
        btnAceptar.setOnAction(e -> {
            if (chkPonerFueraServicio.isSelected()) {
                boolean alMenosUnMotivoSeleccionado = false;
                for (MotivoTipoWrapper mw : motivoWrappers) {
                    if (mw.getCheckBox().isSelected()) {
                        alMenosUnMotivoSeleccionado = true;
                        // Asegúrate de que MotivoFueraServicio se pueda construir con (comentario, motivoTipo)
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
            resultMotivos.set(null); // Usamos null para indicar cancelación
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
    // Al final de InterfazInspeccion.java

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