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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional; // Necesario para manejar el resultado del Alert
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

        marcarFueraServicioCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            motivosBox.setDisable(!newVal);
            if (!newVal) {
                motivoCheckBoxes.forEach(cb -> {
                    cb.setSelected(false);
                });
            }
            actualizarEstadoBotonCerrarOrden();
        });

        cerrarOrdenButton = new Button("Cerrar Orden");
        cerrarOrdenButton.setDisable(true);

        // Modificación AQUÍ: El botón ahora llama a mostrarConfirmacionCierreOrden()
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
                    return object != null ? "Orden #" + object.getNumeroOrden() + " (Estación: " + (object.getNombreES() != null ? object.getNombreES().getNombre() : "N/A") + ")" : "";
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

        boolean motivosYComentariosValidos = true;

        if (observacionNoVacia) {
            if (!marcarFueraServicioCheckbox.isSelected()) {
                motivosYComentariosValidos = false;
            } else {
                boolean alMenosUnMotivoSeleccionadoYComentado = false;
                for (CheckBox cb : motivoCheckBoxes) {
                    if (cb.isSelected()) {
                        TextField comentarioField = comentarioFieldsPorMotivo.get(cb);
                        if (comentarioField == null || comentarioField.getText().trim().isEmpty()) {
                            motivosYComentariosValidos = false;
                            break;
                        }
                        alMenosUnMotivoSeleccionadoYComentado = true;
                    }
                }
                if (!alMenosUnMotivoSeleccionadoYComentado) {
                    motivosYComentariosValidos = false;
                }
            }
        } else {
            if (marcarFueraServicioCheckbox.isSelected()) {
                motivosYComentariosValidos = false;
            }
            if (motivoCheckBoxes.stream().anyMatch(CheckBox::isSelected)) {
                motivosYComentariosValidos = false;
            }
        }

        cerrarOrdenButton.setDisable(!(empleadoSeleccionado && ordenSeleccionada && observacionNoVacia && motivosYComentariosValidos));
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // NUEVO MÉTODO: Mostrar la ventana de confirmación
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
            // Si el usuario confirma, entonces se procede con la lógica de cierre
            ejecutarCierreDeOrden();
        } else {
            System.out.println("Cierre de orden cancelado por el usuario.");
        }
    }

    // NUEVO MÉTODO: Contiene la lógica real de cierre de la orden
    private void ejecutarCierreDeOrden() {
        try {
            OrdenDeInspeccion ordenSeleccionada = ordenesComboBox.getSelectionModel().getSelectedItem();
            if (ordenSeleccionada == null) {
                // Esto no debería ocurrir si el botón está deshabilitado correctamente,
                // pero se mantiene como validación de respaldo.
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

            // Re-validaciones (aunque el botón ya está deshabilitado si no se cumplen)
            // Se mantienen para robustez y para capturar posibles desincronizaciones de UI/lógica
            if (observacionCierre != null && !observacionCierre.trim().isEmpty()) {
                if (!marcarFueraServicioCheckbox.isSelected()) {
                    mostrarAlerta("Error de Validación", "Si ingresa Observaciones de Cierre, debe marcar 'Marcar Sismógrafo Fuera de Servicio'.");
                    return;
                }

                boolean alMenosUnMotivoSeleccionadoYComentado = false;
                for (CheckBox cb : motivoCheckBoxes) {
                    if (cb.isSelected()) {
                        TextField comentarioField = comentarioFieldsPorMotivo.get(cb);
                        String comentarioParaMotivo = (comentarioField != null) ? comentarioField.getText().trim() : "";

                        if (comentarioParaMotivo.isEmpty()) {
                            mostrarAlerta("Error de Validación", "El comentario es obligatorio para el motivo '" + cb.getText() + "' si está seleccionado.");
                            return;
                        }
                        alMenosUnMotivoSeleccionadoYComentado = true;

                        MotivoTipo tipo = repositorioMotivoTipo.buscarMotivoPorDescripcion(cb.getText());
                        if (tipo == null) {
                            System.err.println("Advertencia: MotivoTipo '" + cb.getText() + "' no encontrado en el repositorio.");
                            continue;
                        }
                        MotivoFueraServicio mfs = new MotivoFueraServicio(comentarioParaMotivo, tipo);
                        motivosSeleccionados.add(mfs);
                    }
                }

                if (!alMenosUnMotivoSeleccionadoYComentado) {
                    mostrarAlerta("Error de Validación", "Debe seleccionar y comentar al menos un motivo si marca el sismógrafo como 'Fuera de Servicio' y tiene Observaciones de Cierre.");
                    return;
                }
            }

            String legajoRI = sesion.getLegajoEmpleadoLogueado();

            gestorInspeccion.cerrarOrden(numeroOrden, observacionCierre, motivosSeleccionados);
            mostrarAlerta("Éxito", "Orden " + numeroOrden + " cerrada exitosamente y sismógrafo actualizado (si aplica).");

            // Limpiar la UI después de la operación exitosa
            observacionCierreArea.clear();
            marcarFueraServicioCheckbox.setSelected(false);
            ordenesComboBox.getSelectionModel().clearSelection();

            cargarOrdenesDisponibles();
            actualizarEstadoBotonCerrarOrden();

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
            System.out.println("Fecha de Creación: " + orden.getFechaHoraCreacion());
            System.out.println("Fecha de Cierre: " + orden.getFechaHoraCierre());
            System.out.println("Observación de Cierre: " + orden.getObservacionCierre());
            System.out.println("Sismógrafo: " + (orden.getNombreES() != null && orden.getNombreES().getSismografo() != null ? orden.getNombreES().getSismografo().getIdentificadorSismografo() : "N/A"));
            System.out.println("Estado Actual de Sismógrafo: " + (orden.getNombreES() != null && orden.getNombreES().getSismografo() != null && orden.getNombreES().getSismografo().getEstadoActual() != null ? orden.getNombreES().getSismografo().getEstadoActual().getNombre() : "N/A"));

            if (orden.getHistorialCambioEstado() != null && !orden.getHistorialCambioEstado().isEmpty()) {
                System.out.println("Historial de Cambios de Estado:");
                for (CambioEstado cambio : orden.getHistorialCambioEstado()) {
                    System.out.println("  - Cambio de Estado a: " + cambio.getNuevoEstado().getNombre() + " (Fecha: " + cambio.getFechaHoraInicio() + ")");
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