package dsi.ppai;

import dsi.ppai.entities.Empleado; // <-- IMPORTAR EMPLEADO
import dsi.ppai.entities.MotivoFueraServicio;
import dsi.ppai.entities.MotivoTipo;
import dsi.ppai.entities.OrdenDeInspeccion;
import dsi.ppai.repositories.RepositorioEmpleados; // <-- NUEVA IMPORTACIÓN
import dsi.ppai.repositories.RepositorioMotivoTipo;
import dsi.ppai.services.GestorInspeccion;
import dsi.ppai.services.Sesion;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ApplicationUI extends Application {

    private GestorInspeccion gestorInspeccion;
    private Sesion sesion;
    private RepositorioMotivoTipo repositorioMotivoTipo;
    private RepositorioEmpleados repositorioEmpleados; // <-- NUEVO REPOSITORIO

    private Stage primaryStage;

    private ComboBox<Empleado> empleadoComboBox; // <-- NUEVO ComboBox para empleados
    private ComboBox<OrdenDeInspeccion> ordenesComboBox;

    @Autowired
    public ApplicationUI(GestorInspeccion gestorInspeccion, Sesion sesion,
                         RepositorioMotivoTipo repositorioMotivoTipo,
                         RepositorioEmpleados repositorioEmpleados) { // <-- INYECTAR REPOSITORIO DE EMPLEADOS
        this.gestorInspeccion = gestorInspeccion;
        this.sesion = sesion;
        this.repositorioMotivoTipo = repositorioMotivoTipo;
        this.repositorioEmpleados = repositorioEmpleados; // <-- ASIGNAR
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Sistema de Gestión de Inspecciones");

        // El login simulado del InspeccionRunner ahora es opcional o solo para fines de depuración
        // La UI ahora permitirá elegir al empleado.

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label titleLabel = new Label("Cerrar Orden de Inspección y Marcar Sismógrafo Fuera de Servicio");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // --- NUEVO: ComboBox para seleccionar el Responsable de Inspección ---
        Label selectEmpleadoLabel = new Label("Seleccionar Responsable de Inspección:");
        empleadoComboBox = new ComboBox<>();
        empleadoComboBox.setPromptText("Seleccione un RI...");
        empleadoComboBox.setPrefWidth(300);

        cargarResponsablesDeInspeccion(); // Cargar los RI disponibles

        // Listener para cuando se selecciona un empleado
        empleadoComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newEmpleado) -> {
            if (newEmpleado != null) {
                sesion.setEmpleadoLogueado(newEmpleado); // Asigna el empleado seleccionado a la sesión
                System.out.println("Responsable de Inspección seleccionado: " + newEmpleado.getNombre() + " (Legajo: " + newEmpleado.getLegajo() + ")");
                cargarOrdenesDisponibles(); // Carga las órdenes para este nuevo RI
            } else {
                sesion.setEmpleadoLogueado(null); // Si no hay RI seleccionado
                ordenesComboBox.setItems(FXCollections.emptyObservableList()); // Limpia las órdenes
                ordenesComboBox.setPromptText("Seleccione un RI primero.");
            }
        });
        // --- FIN NUEVO ---


        // --- ComboBox existente para órdenes de inspección ---
        Label selectOrdenLabel = new Label("Seleccionar Orden de Inspección:");
        ordenesComboBox = new ComboBox<>();
        ordenesComboBox.setPromptText("Seleccione un RI primero.");
        ordenesComboBox.setPrefWidth(300);

        // No cargamos órdenes aquí al inicio, esperamos la selección de un empleado.
        // cargarOrdenesDisponibles(); // Esta llamada se mueve al listener del empleadoComboBox

        ordenesComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newOrden) -> {
            if (newOrden != null) {
                System.out.println("Orden seleccionada: #" + newOrden.getNumeroOrden() + " - Estación: " + (newOrden.getNombreES() != null ? newOrden.getNombreES().getNombre() : "N/A"));
            }
        });

        TextArea observacionCierreArea = new TextArea();
        observacionCierreArea.setPromptText("Observaciones de Cierre (Opcional)");
        observacionCierreArea.setPrefHeight(80);

        CheckBox marcarFueraServicioCheckbox = new CheckBox("Marcar Sismógrafo Fuera de Servicio");

        VBox motivosBox = new VBox(5);
        motivosBox.setDisable(true);
        Label motivosLabel = new Label("Seleccione Motivos (si aplica):");
        List<CheckBox> motivoCheckBoxes = new ArrayList<>();

        repositorioMotivoTipo.buscarTodos().forEach(motivoTipo -> {
            CheckBox cb = new CheckBox(motivoTipo.getDescripcion());
            motivoCheckBoxes.add(cb);
            motivosBox.getChildren().add(cb);
        });

        marcarFueraServicioCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            motivosBox.setDisable(!newVal);
        });

        Button cerrarOrdenButton = new Button("Cerrar Orden");
        cerrarOrdenButton.setOnAction(e -> {
            try {
                OrdenDeInspeccion ordenSeleccionada = ordenesComboBox.getSelectionModel().getSelectedItem();
                if (ordenSeleccionada == null) {
                    mostrarAlerta("Error", "Por favor, seleccione una orden de inspección.");
                    return;
                }
                Long numeroOrden = ordenSeleccionada.getNumeroOrden();

                // Asegúrate de que haya un empleado logueado en la sesión
                if (sesion.getEmpleadoLogueado() == null) {
                    mostrarAlerta("Error", "No hay Responsable de Inspección logueado. Seleccione uno primero.");
                    return;
                }

                String observacionCierre = observacionCierreArea.getText();

                List<MotivoFueraServicio> motivosSeleccionados = new ArrayList<>();
                if (marcarFueraServicioCheckbox.isSelected()) {
                    motivoCheckBoxes.stream()
                            .filter(CheckBox::isSelected)
                            .forEach(cb -> {
                                MotivoTipo tipo = repositorioMotivoTipo.buscarMotivoPorDescripcion(cb.getText());
                                MotivoFueraServicio mfs = new MotivoFueraServicio(cb.getText(), tipo);
                                motivosSeleccionados.add(mfs);
                            });
                }

                String legajoRI = sesion.getLegajoEmpleadoLogueado();

                gestorInspeccion.cerrarOrden(numeroOrden, observacionCierre, motivosSeleccionados);
                mostrarAlerta("Éxito", "Orden " + numeroOrden + " cerrada exitosamente y sismógrafo actualizado (si aplica).");

                // Limpiar la UI y actualizar las listas
                observacionCierreArea.clear();
                marcarFueraServicioCheckbox.setSelected(false);
                motivoCheckBoxes.forEach(cb -> cb.setSelected(false));
                ordenesComboBox.getSelectionModel().clearSelection(); // Deseleccionar orden

                // Recargar órdenes disponibles para el RI actualmente seleccionado
                cargarOrdenesDisponibles();

                List<OrdenDeInspeccion> ordenesCompletadas = gestorInspeccion.obtenerOrdenesCompletadasDelRI(legajoRI);
                mostrarOrdenesCompletadas(ordenesCompletadas);

            } catch (IllegalArgumentException | IllegalStateException ex) {
                mostrarAlerta("Error de Operación", ex.getMessage());
            } catch (Exception ex) {
                mostrarAlerta("Error Inesperado", "Ocurrió un error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // --- Añadir los nuevos componentes a la UI ---
        root.getChildren().addAll(titleLabel, selectEmpleadoLabel, empleadoComboBox,
                selectOrdenLabel, ordenesComboBox, observacionCierreArea,
                marcarFueraServicioCheckbox, motivosBox, cerrarOrdenButton);

        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- NUEVO MÉTODO AUXILIAR para cargar Responsables de Inspección ---
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
        if (!responsables.isEmpty()) {
            empleadoComboBox.getSelectionModel().selectFirst(); // Seleccionar el primero por defecto
        } else {
            empleadoComboBox.setPromptText("No se encontraron Responsables de Inspección.");
        }
    }

    // --- Método auxiliar para cargar las órdenes (modificado para usar el RI de la sesión) ---
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
            ordenesComboBox.setItems(FXCollections.emptyObservableList()); // Limpiar si no hay RI
            ordenesComboBox.setPromptText("No hay Responsable de Inspección logueado.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
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
            System.out.println("----------------------------------------");
        }
    }
}