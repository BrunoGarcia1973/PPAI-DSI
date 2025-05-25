package dsi.ppai;

import dsi.ppai.entities.CambioEstado;
import dsi.ppai.entities.Empleado;
import dsi.ppai.entities.MotivoFueraServicio;
import dsi.ppai.entities.MotivoTipo;
import dsi.ppai.entities.OrdenDeInspeccion;
import dsi.ppai.repositories.RepositorioEmpleados;
import dsi.ppai.repositories.RepositorioMotivoTipo;
import dsi.ppai.services.GestorInspeccion;
import dsi.ppai.services.Sesion;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ApplicationUI extends Application {

    private GestorInspeccion gestorInspeccion;
    private Sesion sesion;
    private RepositorioMotivoTipo repositorioMotivoTipo;
    private RepositorioEmpleados repositorioEmpleados;

    private Stage primaryStage;

    private ComboBox<Empleado> empleadoComboBox;
    private ComboBox<OrdenDeInspeccion> ordenesComboBox;
    private TextArea observacionCierreArea;
    private Button cerrarOrdenButton;
    private CheckBox marcarFueraServicioCheckbox;

    private Map<CheckBox, TextField> comentarioFieldsPorMotivo = new HashMap<>();
    private List<CheckBox> motivoCheckBoxes = new ArrayList<>();

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Autowired
    public ApplicationUI(GestorInspeccion gestorInspeccion, Sesion sesion,
                         RepositorioMotivoTipo repositorioMotivoTipo,
                         RepositorioEmpleados repositorioEmpleados) {
        this.gestorInspeccion = gestorInspeccion;
        this.sesion = sesion;
        this.repositorioMotivoTipo = repositorioMotivoTipo;
        this.repositorioEmpleados = repositorioEmpleados;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Sistema de Gestión de Inspecciones");

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label titleLabel = new Label("Cerrar Orden de Inspección y Marcar Sismógrafo Fuera de Servicio");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label selectEmpleadoLabel = new Label("Seleccionar Responsable de Inspección:");
        empleadoComboBox = new ComboBox<>();
        empleadoComboBox.setPromptText("Seleccione un RI...");
        empleadoComboBox.setPrefWidth(300);

        cargarResponsablesDeInspeccion();

        empleadoComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newEmpleado) -> {
            if (newEmpleado != null) {
                sesion.setEmpleadoLogueado(newEmpleado);
                System.out.println("Responsable de Inspección seleccionado: " + newEmpleado.getNombre() + " (Legajo: " + newEmpleado.getLegajo() + ")");
                cargarOrdenesDisponibles();
            } else {
                sesion.setEmpleadoLogueado(null);
                ordenesComboBox.setItems(FXCollections.emptyObservableList());
                ordenesComboBox.setPromptText("Seleccione un RI primero.");
            }
            actualizarEstadoBotonCerrarOrden();
        });

        Label selectOrdenLabel = new Label("Seleccionar Orden de Inspección:");
        ordenesComboBox = new ComboBox<>();
        ordenesComboBox.setPromptText("Seleccione un RI primero.");
        ordenesComboBox.setPrefWidth(300);

        ordenesComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newOrden) -> {
            if (newOrden != null) {
                System.out.println("Orden seleccionada: #" + newOrden.getNumeroOrden() + " - Estación: " + (newOrden.getNombreES() != null ? newOrden.getNombreES().getNombre() : "N/A"));
            }
            actualizarEstadoBotonCerrarOrden();
        });

        observacionCierreArea = new TextArea();
        observacionCierreArea.setPromptText("Observaciones de Cierre (OBLIGATORIO)");
        observacionCierreArea.setPrefHeight(80);
        observacionCierreArea.textProperty().addListener((obs, oldVal, newVal) -> {
            actualizarEstadoBotonCerrarOrden();
        });

        marcarFueraServicioCheckbox = new CheckBox("Marcar Sismógrafo Fuera de Servicio");

        VBox motivosBox = new VBox(5);
        motivosBox.setDisable(true);
        Label motivosLabel = new Label("Seleccione Motivos (si aplica):");

        repositorioMotivoTipo.buscarTodos().forEach(motivoTipo -> {
            CheckBox cb = new CheckBox(motivoTipo.getDescripcion());
            motivoCheckBoxes.add(cb);

            TextField comentarioField = new TextField();
            comentarioField.setPromptText("Comentario para " + motivoTipo.getDescripcion() + " (OBLIGATORIO si seleccionado)");
            comentarioField.setPrefWidth(250);
            comentarioField.setDisable(true);

            comentarioFieldsPorMotivo.put(cb, comentarioField);

            cb.selectedProperty().addListener((obs, oldVal, newVal) -> {
                comentarioField.setDisable(!newVal);
                if (!newVal) {
                    comentarioField.clear();
                }
                actualizarEstadoBotonCerrarOrden();
            });

            comentarioField.textProperty().addListener((obs, oldVal, newVal) -> {
                actualizarEstadoBotonCerrarOrden();
            });

            HBox motivoEntry = new HBox(5, cb, comentarioField);
            motivosBox.getChildren().add(motivoEntry);
        });

        // *** CAMBIO CLAVE AQUÍ: Listener del checkbox principal ***
        marcarFueraServicioCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            motivosBox.setDisable(!newVal);
            if (!newVal) {
                // Al desmarcar "Marcar Sismógrafo Fuera de Servicio",
                // deseleccionar todos los motivos y limpiar sus comentarios.
                motivoCheckBoxes.forEach(cb -> {
                    cb.setSelected(false);
                    TextField comentarioField = comentarioFieldsPorMotivo.get(cb);
                    if (comentarioField != null) {
                        comentarioField.clear();
                    }
                });
            }
            // Después de limpiar o habilitar/deshabilitar los motivos,
            // asegurar que se reevalúe el estado del botón.
            actualizarEstadoBotonCerrarOrden();
        });

        cerrarOrdenButton = new Button("Cerrar Orden");
        cerrarOrdenButton.setDisable(true);

        cerrarOrdenButton.setOnAction(e -> {
            mostrarConfirmacionCierreOrden();
        });

        root.getChildren().addAll(titleLabel, selectEmpleadoLabel, empleadoComboBox,
                selectOrdenLabel, ordenesComboBox, observacionCierreArea,
                marcarFueraServicioCheckbox, motivosBox, cerrarOrdenButton);

        Scene scene = new Scene(root, 700, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        actualizarEstadoBotonCerrarOrden();
    }

    private void cargarResponsablesDeInspeccion() {
        List<Empleado> responsables = repositorioEmpleados.buscarResponsablesDeInspeccion();
        ObservableList<Empleado> items = FXCollections.observableArrayList(responsables);
        empleadoComboBox.setItems(items);
        empleadoComboBox.setConverter(new javafx.util.StringConverter<Empleado>() {
            @Override
            public String toString(Empleado object) {
                return object != null ? object.getNombre() + " " + object.getApellido() + " (Legajo: " + object.getLegajo() + ")" : "";
            }

            @Override
            public Empleado fromString(String string) {
                return null;
            }
        });
    }

    private void cargarOrdenesDisponibles() {
        String legajoRI = sesion.getLegajoEmpleadoLogueado();
        if (legajoRI != null) {
            List<OrdenDeInspeccion> ordenesAbiertas = gestorInspeccion.obtenerOrdenesAbiertasDelRI(legajoRI);
            ObservableList<OrdenDeInspeccion> items = FXCollections.observableArrayList(ordenesAbiertas);
            ordenesComboBox.setItems(items);
            ordenesComboBox.setConverter(new javafx.util.StringConverter<OrdenDeInspeccion>() {
                @Override
                public String toString(OrdenDeInspeccion object) {
                    if (object != null) {
                        String sismografoId = "N/A";
                        if (object.getNombreES() != null && object.getNombreES().getSismografo() != null) {
                            sismografoId = String.valueOf(object.getNombreES().getSismografo().getIdentificadorSismografo());
                        }
                        String fechaFinalizacion = (object.getFechaHoraFinalizacion() != null) ?
                                object.getFechaHoraFinalizacion().format(DATE_TIME_FORMATTER) : "N/A";

                        return "Orden #" + object.getNumeroOrden() +
                                " (Estación: " + (object.getNombreES() != null ? object.getNombreES().getNombre() : "N/A") +
                                ", Sismógrafo ID: " + sismografoId +
                                ", Finalización: " + fechaFinalizacion + ")";
                    }
                    return "";
                }

                @Override
                public OrdenDeInspeccion fromString(String string) {
                    return null;
                }
            });
            if (!ordenesAbiertas.isEmpty()) {
                ordenesComboBox.getSelectionModel().selectFirst();
            } else {
                ordenesComboBox.setPromptText("No hay órdenes de inspección abiertas para este RI.");
            }
        } else {
            ordenesComboBox.setItems(FXCollections.emptyObservableList());
            ordenesComboBox.setPromptText("No hay Responsable de Inspección logueado.");
        }
    }

    private void actualizarEstadoBotonCerrarOrden() {
        boolean empleadoSeleccionado = empleadoComboBox.getSelectionModel().getSelectedItem() != null;
        boolean ordenSeleccionada = ordenesComboBox.getSelectionModel().getSelectedItem() != null;
        boolean observacionNoVacia = observacionCierreArea.getText() != null && !observacionCierreArea.getText().trim().isEmpty();

        boolean motivosValidos = true; // Valor predeterminado optimista

        if (marcarFueraServicioCheckbox.isSelected()) {
            boolean alMenosUnMotivoSeleccionadoYComentado = false;
            for (CheckBox cb : motivoCheckBoxes) {
                if (cb.isSelected()) {
                    TextField comentarioField = comentarioFieldsPorMotivo.get(cb);
                    if (comentarioField != null && !comentarioField.getText().trim().isEmpty()) {
                        alMenosUnMotivoSeleccionadoYComentado = true;
                    } else {
                        // Si un motivo está seleccionado pero su comentario está vacío, es inválido.
                        motivosValidos = false;
                        break; // No es necesario seguir revisando
                    }
                }
            }
            // Si el checkbox "Fuera de Servicio" está marcado, pero no se seleccionó al menos un motivo con comentario,
            // entonces los motivos no son válidos.
            if (!alMenosUnMotivoSeleccionadoYComentado) {
                motivosValidos = false;
            }
        } else {
            // *** CAMBIO CLAVE AQUÍ: Lógica de validación cuando "Marcar Sismógrafo Fuera de Servicio" NO está seleccionado ***
            // Si el checkbox principal NO está marcado, entonces NINGÚN motivo individual debe estar seleccionado.
            // Si hay CUALQUIERA seleccionado, los motivos son inválidos.
            if (motivoCheckBoxes.stream().anyMatch(CheckBox::isSelected)) {
                motivosValidos = false; // Hay motivos seleccionados cuando no debería haberlos
            } else {
                motivosValidos = true; // No hay motivos seleccionados y no se marcó el checkbox principal, por lo tanto, es válido.
            }
        }

        // El botón se habilita solo si todas las condiciones son verdaderas
        cerrarOrdenButton.setDisable(!(empleadoSeleccionado && ordenSeleccionada && observacionNoVacia && motivosValidos));
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarConfirmacionCierreOrden() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Cierre de Orden");
        alert.setHeaderText("¿Está seguro de que desea cerrar esta orden de inspección?");
        alert.setContentText("Esta acción actualizará el estado de la orden de inspección y del sismógrafo.");

        ButtonType buttonTypeConfirmar = new ButtonType("Confirmar", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeConfirmar, buttonTypeCancelar);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == buttonTypeConfirmar) {
            ejecutarCierreDeOrden();
        } else {
            System.out.println("Cierre de orden cancelado por el usuario.");
        }
    }

    private void ejecutarCierreDeOrden() {
        try {
            OrdenDeInspeccion ordenSeleccionada = ordenesComboBox.getSelectionModel().getSelectedItem();
            if (ordenSeleccionada == null) {
                mostrarAlerta("Error", "Por favor, seleccione una orden de inspección.");
                return;
            }
            Long numeroOrden = ordenSeleccionada.getNumeroOrden();

            if (sesion.getEmpleadoLogueado() == null) {
                mostrarAlerta("Error", "No hay Responsable de Inspección logueado. Seleccione uno primero.");
                return;
            }

            String observacionCierre = observacionCierreArea.getText();
            if (observacionCierre == null || observacionCierre.trim().isEmpty()) {
                mostrarAlerta("Error de Validación", "Las observaciones de cierre son obligatorias.");
                return;
            }

            List<MotivoFueraServicio> motivosSeleccionados = new ArrayList<>();

            if (marcarFueraServicioCheckbox.isSelected()) {
                boolean alMenosUnMotivoSeleccionado = false;
                for (CheckBox cb : motivoCheckBoxes) {
                    if (cb.isSelected()) {
                        alMenosUnMotivoSeleccionado = true;
                        TextField comentarioField = comentarioFieldsPorMotivo.get(cb);
                        String comentarioParaMotivo = (comentarioField != null) ? comentarioField.getText().trim() : "";

                        if (comentarioParaMotivo.isEmpty()) {
                            mostrarAlerta("Error de Validación", "El comentario es obligatorio para el motivo '" + cb.getText() + "' si está seleccionado.");
                            return;
                        }

                        MotivoTipo tipo = repositorioMotivoTipo.buscarMotivoPorDescripcion(cb.getText());
                        if (tipo == null) {
                            System.err.println("Advertencia: MotivoTipo '" + cb.getText() + "' no encontrado en el repositorio.");
                            continue;
                        }
                        MotivoFueraServicio mfs = new MotivoFueraServicio(comentarioParaMotivo, tipo);
                        motivosSeleccionados.add(mfs);
                    }
                }

                if (!alMenosUnMotivoSeleccionado) {
                    mostrarAlerta("Error de Validación", "Debe seleccionar al menos un motivo si marca el sismógrafo como 'Fuera de Servicio'.");
                    return;
                }
            } else {
                // Esta es la validación final antes de cerrar la orden.
                // Si el checkbox principal está desmarcado, no debería haber motivos seleccionados.
                if (motivoCheckBoxes.stream().anyMatch(CheckBox::isSelected)) {
                    mostrarAlerta("Error de Validación", "No puede seleccionar motivos si no marca 'Marcar Sismógrafo Fuera de Servicio'. Se han desmarcado los motivos.");
                    // Forzar el reseteo de motivos aquí también, como medida de seguridad.
                    motivoCheckBoxes.forEach(cb -> {
                        cb.setSelected(false);
                        TextField comentarioField = comentarioFieldsPorMotivo.get(cb);
                        if (comentarioField != null) {
                            comentarioField.clear();
                        }
                    });
                    actualizarEstadoBotonCerrarOrden(); // Reevaluar el estado del botón después de limpiar
                    return;
                }
            }

            String legajoRI = sesion.getEmpleadoLogueado().getLegajo();

            gestorInspeccion.cerrarOrden(numeroOrden, observacionCierre, motivosSeleccionados);
            mostrarAlerta("Éxito", "Orden " + numeroOrden + " cerrada exitosamente y sismógrafo actualizado (si aplica).");

            // Limpiar la interfaz después de un cierre exitoso
            observacionCierreArea.clear();
            marcarFueraServicioCheckbox.setSelected(false); // Esto disparará el listener y limpiará los motivos
            ordenesComboBox.getSelectionModel().clearSelection();
            ordenesComboBox.setPromptText("Seleccione un RI primero.");

            cargarOrdenesDisponibles();
            actualizarEstadoBotonCerrarOrden(); // Asegurar la reevaluación final

            List<OrdenDeInspeccion> ordenesCompletadas = gestorInspeccion.obtenerOrdenesCompletadasDelRI(legajoRI);
            mostrarOrdenesCompletadas(ordenesCompletadas);

        } catch (IllegalArgumentException | IllegalStateException ex) {
            mostrarAlerta("Error de Operación", ex.getMessage());
        } catch (Exception ex) {
            mostrarAlerta("Error Inesperado", "Ocurrió un error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void mostrarOrdenesCompletadas(List<OrdenDeInspeccion> ordenes) {
        if (ordenes.isEmpty()) {
            System.out.println("No se encontraron órdenes completadas para este Responsable de Inspección.");
            return;
        }

        System.out.println("\n--- Órdenes de Inspección Completadas ---");
        for (OrdenDeInspeccion orden : ordenes) {
            System.out.println("Número de Orden: " + orden.getNumeroOrden());
            System.out.println("Fecha de Creación: " + orden.getFechaHoraCreacion().format(DATE_TIME_FORMATTER));
            System.out.println("Fecha de Cierre: " + (orden.getFechaHoraCierre() != null ? orden.getFechaHoraCierre().format(DATE_TIME_FORMATTER) : "N/A"));
            System.out.println("Fecha de Finalización: " + (orden.getFechaHoraFinalizacion() != null ? orden.getFechaHoraFinalizacion().format(DATE_TIME_FORMATTER) : "N/A"));
            System.out.println("Observación de Cierre: " + orden.getObservacionCierre());
            System.out.println("Estación Sismológica: " + (orden.getNombreES() != null ? orden.getNombreES().getNombre() : "N/A"));

            String sismografoId = "N/A";
            String sismografoEstado = "N/A";
            if (orden.getNombreES() != null && orden.getNombreES().getSismografo() != null) {
                sismografoId = String.valueOf(orden.getNombreES().getSismografo().getIdentificadorSismografo());
                if (orden.getNombreES().getSismografo().getEstadoActual() != null) {
                    sismografoEstado = orden.getNombreES().getSismografo().getEstadoActual().getNombre();
                }
            }
            System.out.println("Sismógrafo ID: " + sismografoId);
            System.out.println("Estado Actual de Sismógrafo: " + sismografoEstado);


            if (orden.getHistorialCambioEstado() != null && !orden.getHistorialCambioEstado().isEmpty()) {
                System.out.println("Historial de Cambios de Estado:");
                for (CambioEstado cambio : orden.getHistorialCambioEstado()) {
                    System.out.println("  - Cambio de Estado a: " + cambio.getNuevoEstado().getNombre() + " (Fecha: " + cambio.getFechaHoraInicio().format(DATE_TIME_FORMATTER) + ")");
                    if (cambio.getMotivos() != null && !cambio.getMotivos().isEmpty()) {
                        System.out.println("    Motivos Asociados:");
                        for (MotivoFueraServicio mfs : cambio.getMotivos()) {
                            System.out.println("      - Tipo: " + mfs.getMotivo().getDescripcion() +
                                    (mfs.getObservacion() != null && !mfs.getObservacion().isEmpty() ? " (Comentario: " + mfs.getObservacion() + ")" : ""));
                        }
                    }
                }
            }
            System.out.println("----------------------------------------");
        }
    }
}