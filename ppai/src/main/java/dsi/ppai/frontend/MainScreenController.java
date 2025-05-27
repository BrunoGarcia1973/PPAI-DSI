// MainScreenController.java

package dsi.ppai.frontend;

import dsi.ppai.entities.Empleado;
import dsi.ppai.entities.MotivoFueraServicio; // Asegúrate de importar MotivoFueraServicio
import dsi.ppai.entities.MotivoTipo; // Asegúrate de importar MotivoTipo
import dsi.ppai.entities.OrdenDeInspeccion;
import dsi.ppai.services.GestorInspeccion;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory; // Importar esto
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; // Para formatear fechas
import java.util.ArrayList; // Para la lista de motivos seleccionados
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MainScreenController {

    private final GestorInspeccion gestorInspeccion;

    @FXML
    private ComboBox<Empleado> cmbResponsableInspeccion;
    @FXML
    private TableView<OrdenDeInspeccion> tblOrdenes;
    @FXML
    private TableColumn<OrdenDeInspeccion, Long> colNumeroOrden;
    @FXML
    private TableColumn<OrdenDeInspeccion, String> colFechaInicio; // Cambiado a String para el formato
    @FXML
    private TableColumn<OrdenDeInspeccion, String> colEstado;
    @FXML
    private TableColumn<OrdenDeInspeccion, String> colResponsableInspeccion;
    @FXML
    private TextArea txtObservacionCierre; // Cambiado a TextArea para más texto
    @FXML
    private Button btnCerrarOrden;

    // --- NUEVOS COMPONENTES PARA MOTIVOS FUERA DE SERVICIO ---
    @FXML
    private VBox vboxMotivosFueraServicio; // Contenedor para los CheckBox dinámicos
    private List<MotivoTipo> motivosDisponibles; // Lista de motivos cargados desde el gestor
    private List<MotivoFueraServicio> motivosSeleccionados; // Lista de motivos seleccionados por el usuario

    @FXML
    public void initialize() {
        // Inicializa la tabla
        colNumeroOrden.setCellValueFactory(new PropertyValueFactory<>("numOrden"));
        // Para la fecha de inicio, usa una CellValueFactory personalizada para formatear
        colFechaInicio.setCellValueFactory(cellData -> {
            LocalDateTime fecha = cellData.getValue().getFechaHoraInicio();
            return new SimpleStringProperty(fecha != null ? fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "");
        });
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado.nombre"));
        colResponsableInspeccion.setCellValueFactory(cellData -> {
            Empleado empleado = cellData.getValue().getEmpleado();
            return new SimpleStringProperty(empleado != null ? empleado.getNombre() + " " + empleado.getApellido() : "");
        });

        // Carga los responsables de inspección al iniciar la interfaz
        // El GestorInspeccion ahora tiene el método obtenerEmpleadoLogueado()
        Empleado empleadoLogueado = gestorInspeccion.obtenerEmpleadoLogueado();
        if (empleadoLogueado != null) {
            // Asumo que el ComboBox solo mostrará al empleado logueado
            // Si quieres mostrar más, necesitarías un servicio para obtener todos los empleados
            cmbResponsableInspeccion.setItems(FXCollections.observableArrayList(empleadoLogueado));
            cmbResponsableInspeccion.getSelectionModel().selectFirst(); // Selecciona automáticamente al empleado
            cargarOrdenes(); // Carga las órdenes tan pronto como se selecciona el responsable
        } else {
            // Manejar caso donde no hay empleado logueado (ej. mostrar mensaje, deshabilitar UI)
            System.err.println("ERROR: No hay empleado logueado para cargar responsables.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Sesión");
            alert.setHeaderText("No se pudo obtener el empleado logueado.");
            alert.setContentText("Por favor, asegúrese de que la sesión esté correctamente inicializada.");
            alert.showAndWait();
            btnCerrarOrden.setDisable(true); // Deshabilita el botón si no hay empleado
        }

        // Cargar y mostrar los motivos de fuera de servicio
        cargarMotivosFueraServicio();

        // Listener para el cambio de selección en el ComboBox
        cmbResponsableInspeccion.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Cuando el responsable cambia, carga las órdenes para ese responsable
                cargarOrdenes();
            } else {
                // Limpia la tabla si no hay responsable seleccionado
                tblOrdenes.getItems().clear();
            }
        });

        // Habilita/deshabilita el botón de cerrar orden basándose en la selección de la tabla
        tblOrdenes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btnCerrarOrden.setDisable(newSelection == null);
        });
    }

    /**
     * Carga las órdenes de inspección del responsable seleccionado en la tabla.
     */
    private void cargarOrdenes() {
        System.out.println("DEBUG (MainScreenController): Cargando órdenes...");
        try {
            List<OrdenDeInspeccion> ordenes = gestorInspeccion.buscarOrdenesInspeccionDeRI();
            tblOrdenes.setItems(FXCollections.observableArrayList(ordenes));
            System.out.println("DEBUG (MainScreenController): Órdenes cargadas: " + ordenes.size());
        } catch (Exception e) {
            System.err.println("Error al cargar órdenes: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Carga");
            alert.setHeaderText("No se pudieron cargar las órdenes de inspección.");
            alert.setContentText("Detalle: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Carga los motivos de fuera de servicio desde el gestor y los muestra como CheckBox.
     */
    private void cargarMotivosFueraServicio() {
        try {
            motivosDisponibles = gestorInspeccion.obtenerMotivosFueraServicio();
            motivosSeleccionados = new ArrayList<>(); // Inicializa la lista de seleccionados

            vboxMotivosFueraServicio.getChildren().clear(); // Limpia cualquier CheckBox anterior

            if (motivosDisponibles != null && !motivosDisponibles.isEmpty()) {
                for (MotivoTipo motivoTipo : motivosDisponibles) {
                    CheckBox checkBox = new CheckBox(motivoTipo.getDescripcion());
                    checkBox.setUserData(motivoTipo); // Guarda el objeto MotivoTipo en el CheckBox
                    checkBox.setOnAction(event -> handleMotivoSelection(checkBox)); // Asigna el handler
                    vboxMotivosFueraServicio.getChildren().add(checkBox);
                }
            } else {
                vboxMotivosFueraServicio.getChildren().add(new Label("No hay motivos de fuera de servicio disponibles."));
            }
        } catch (Exception e) {
            System.err.println("Error al cargar motivos de fuera de servicio: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Carga");
            alert.setHeaderText("No se pudieron cargar los motivos de fuera de servicio.");
            alert.setContentText("Detalle: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Maneja la selección/deselección de los CheckBox de motivos.
     * @param checkBox El CheckBox que fue clicado.
     */
    private void handleMotivoSelection(CheckBox checkBox) {
        MotivoTipo motivoTipo = (MotivoTipo) checkBox.getUserData();
        if (checkBox.isSelected()) {
            // Agrega un nuevo MotivoFueraServicio (con comentario basado en el MotivoTipo)
            motivosSeleccionados.add(new MotivoFueraServicio(motivoTipo.getDescripcion(), motivoTipo));
        } else {
            // Remueve el MotivoFueraServicio correspondiente
            motivosSeleccionados.removeIf(mf -> mf.getMotivoTipo().equals(motivoTipo));
        }
        System.out.println("Motivos seleccionados: " + motivosSeleccionados.stream().map(mf -> mf.getMotivoTipo().getDescripcion()).collect(Collectors.joining(", ")));
    }


    @FXML
    private void cerrarOrdenSeleccionada() {
        OrdenDeInspeccion ordenSeleccionada = tblOrdenes.getSelectionModel().getSelectedItem();

        if (ordenSeleccionada == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText("Ninguna orden seleccionada");
            alert.setContentText("Por favor, seleccione una orden de la tabla para cerrar.");
            alert.showAndWait();
            return;
        }

        String observacion = txtObservacionCierre.getText();
        if (observacion.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText("Observación de Cierre Vacía");
            alert.setContentText("Por favor, ingrese una observación de cierre.");
            alert.showAndWait();
            return;
        }

        if (motivosSeleccionados.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText("Motivos Fuera de Servicio Vacíos");
            alert.setContentText("Debe seleccionar al menos un motivo para poner el sismógrafo fuera de servicio.");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Cierre de Orden");
        confirmAlert.setHeaderText("¿Está seguro de que desea cerrar la orden " + ordenSeleccionada.getNumOrden() + "?");
        confirmAlert.setContentText("La estación sismológica asociada será marcada fuera de servicio.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    gestorInspeccion.cerrarOrden(ordenSeleccionada.getNumOrden(), observacion, motivosSeleccionados);
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Éxito");
                    successAlert.setHeaderText("Orden Cerrada Exitosamente");
                    successAlert.setContentText("La orden " + ordenSeleccionada.getNumOrden() + " ha sido cerrada y el sismógrafo puesto fuera de servicio.");
                    successAlert.showAndWait();

                    // Después de cerrar, recargar las órdenes para actualizar la tabla
                    cargarOrdenes();
                    txtObservacionCierre.clear(); // Limpiar la observación
                    // Deseleccionar todos los checkboxes de motivos y limpiar la lista
                    vboxMotivosFueraServicio.getChildren().forEach(node -> {
                        if (node instanceof CheckBox) {
                            ((CheckBox) node).setSelected(false);
                        }
                    });
                    motivosSeleccionados.clear();


                } catch (Exception e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error al Cerrar Orden");
                    errorAlert.setHeaderText("No se pudo cerrar la orden.");
                    errorAlert.setContentText("Detalle: " + e.getMessage());
                    errorAlert.showAndWait();
                    System.err.println("Error al cerrar orden: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}