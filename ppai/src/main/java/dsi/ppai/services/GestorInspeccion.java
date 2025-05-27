package dsi.ppai.services;

import dsi.ppai.entities.*;
import dsi.ppai.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
// import java.util.Optional; // Ya no es estrictamente necesario importar Optional si los métodos devuelven directamente la entidad o null
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GestorInspeccion {

    private final RepositorioOrdenes repoOrdenes;
    private final RepositorioEstados repoEstados;
    private final Sesion sesion; // Inyecta la Sesion para obtener el empleado logueado
    private final RepositorioMotivoTipo repositorioMotivoTipo;
    private final RepositorioEmpleado repositorioEmpleado; // Se mantiene por si se necesita para otros métodos

    /**
     * CU: "Cerrar Orden de Inspección" - Paso 3: "El sistema busca todas las órdenes de inspección que tiene ese RI en estado 'Completamente Realizada'."
     *
     * Busca las órdenes de inspección del Responsable de Inspección logueado
     * que están en estado "Completamente Realizada".
     * Este método NO necesita argumentos, ya que obtiene el empleado
     * logueado desde la sesión internamente.
     *
     * @return Una lista de objetos OrdenDeInspeccion que cumplen los criterios.
     * @throws IllegalStateException si no hay un empleado logueado en la sesión.
     */
    public List<OrdenDeInspeccion> buscarOrdenesInspeccionDeRI() {
        System.out.println("DEBUG (GestorInspeccion): Iniciando búsqueda de órdenes de inspección.");

        // CU - Paso 3a: Obtengo el empleado logueado desde la sesión
        Empleado empleado = sesion.obtenerEmpleado();

        if (empleado == null) {
            System.err.println("ERROR (GestorInspeccion): No hay un empleado logueado en la sesión.");
            throw new IllegalStateException("No hay un empleado logueado en la sesión. No se pueden buscar órdenes.");
        }
        System.out.println("DEBUG (GestorInspeccion): Empleado logueado: " + empleado.getNombre() + " " + empleado.getApellido() + " (Legajo: " + empleado.getLegajo() + ")");

        // CU - Paso 3b: Pido al repositorio las órdenes asociadas a ese RI.
        // Utiliza repoOrdenes.buscarOrdenesInspeccionDeRI(legajo) como lo tienes definido.
        List<OrdenDeInspeccion> ordenesAsignadasAlRI = repoOrdenes.buscarOrdenesInspeccionDeRI(empleado.getLegajo());

        System.out.println("DEBUG (GestorInspeccion): Órdenes encontradas en el repositorio para legajo " + empleado.getLegajo() + ": " + ordenesAsignadasAlRI.size());

        // CU - Paso 3c: Filtrar las órdenes que tienen el estado "Completamente Realizada".
        List<OrdenDeInspeccion> ordenesCompletamenteRealizadas = ordenesAsignadasAlRI.stream()
                .filter(OrdenDeInspeccion::sosCompletamenteRealizada)
                .collect(Collectors.toList());

        System.out.println("DEBUG (GestorInspeccion): Órdenes 'Completamente Realizada' filtradas: " + ordenesCompletamenteRealizadas.size());
        return ordenesCompletamenteRealizadas;
    }

    /**
     * CU: "Cerrar Orden de Inspección" - Pasos 7 al 13 (principalmente la lógica central de cierre).
     *
     * Cierra la orden con los datos provistos, marcando la estación y los sismógrafos
     * fuera de servicio, cambiando el estado, registrando el cambio y notificando.
     *
     * @param numeroOrden El número de la orden a cerrar.
     * @param observacion La observación de cierre de la orden.
     * @param motivosSeleccionados Una lista de MotivoFueraServicio seleccionados para el sismógrafo.
     * @throws IllegalArgumentException si la orden no existe, la observación es nula/vacía, o no hay motivos.
     * @throws IllegalStateException si no hay un empleado logueado o la orden no cumple las condiciones.
     */
    public void cerrarOrden(Long numeroOrden,
                            String observacion,
                            List<MotivoFueraServicio> motivosSeleccionados) {
        System.out.println("DEBUG (GestorInspeccion): Iniciando cierre de orden " + numeroOrden);

        // 1) Recupero el empleado logueado
        Empleado empleado = sesion.obtenerEmpleado();
        if (empleado == null) {
            throw new IllegalStateException("No hay un empleado logueado para cerrar la orden.");
        }
        System.out.println("DEBUG (GestorInspeccion): Empleado logueado para cerrar orden: " + empleado.getNombre());


        // 2) Busco la orden por su número
        // Utiliza repoOrdenes.buscarOrdenDeInspeccion(numeroOrden) como lo tienes definido.
        OrdenDeInspeccion orden = repoOrdenes.buscarOrdenDeInspeccion(numeroOrden);
        if (orden == null) { // Tu método devuelve null si no la encuentra
            System.err.println("ERROR (GestorInspeccion): Orden de inspección no encontrada con número: " + numeroOrden);
            throw new IllegalArgumentException("La orden de inspección no existe: " + numeroOrden);
        }
        System.out.println("DEBUG (GestorInspeccion): Orden " + numeroOrden + " encontrada.");


        // 3) Validaciones de pertenencia y estado
        if (!orden.sosDeEmpleado(empleado)) {
            throw new IllegalStateException("La orden " + numeroOrden + " no pertenece al empleado logueado.");
        }
        if (!orden.sosCompletamenteRealizada()) {
            throw new IllegalStateException("La orden " + numeroOrden + " no está en estado 'Completamente Realizada'. Estado actual: " + orden.getEstado().getNombre());
        }
        if (observacion == null || observacion.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar una observación para el cierre.");
        }
        if (motivosSeleccionados == null || motivosSeleccionados.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un motivo para poner el sismógrafo fuera de servicio.");
        }
        System.out.println("DEBUG (GestorInspeccion): Validaciones de orden " + numeroOrden + " exitosas.");


        // 4) Completar datos de cierre de la orden
        orden.setFechaHoraCierre(LocalDateTime.now());
        orden.setObservacionCierre(observacion);
        System.out.println("DEBUG (GestorInspeccion): Datos de cierre para orden " + numeroOrden + " actualizados.");

        // 5) Poner el sismógrafo asociado a la estación fuera de servicio (delegación)
        orden.ponerSismografoFueraDeServicio(empleado, motivosSeleccionados);
        System.out.println("DEBUG (GestorInspeccion): Sismógrafo asociado a orden " + numeroOrden + " puesto fuera de servicio.");


        // 6) Obtener el estado "Cerrada" para la orden
        // *** ESTA ES LA LÍNEA QUE SE AJUSTÓ ***
        // Ahora usa repoEstados.buscarEstado(nombre)
        Estado estadoCerrada = repoEstados.buscarEstado("Cerrada"); // <--- CORRECTO: Usa tu método 'buscarEstado'
        if (estadoCerrada == null) { // Tu método devuelve null si no lo encuentra
            throw new IllegalStateException("Estado 'Cerrada' no encontrado en el repositorio de estados. Asegúrese de inicializarlo.");
        }
        System.out.println("DEBUG (GestorInspeccion): Estado 'Cerrada' obtenido.");


        // 7) Registrar el cambio de estado para la ORDEN
        Estado estadoAnteriorOrden = orden.getEstado();
        CambioEstado cambioOrden = new CambioEstado(
                empleado,
                estadoAnteriorOrden,
                estadoCerrada,
                LocalDateTime.now(),
                null, // Fecha de fin nula para el cambio actual (es el estado activo)
                null // Los motivos de fuera de servicio son para el Sismógrafo, no directamente para el CambioEstado de la Orden
        );
        orden.registrarCambioEstado(cambioOrden); // Este método en OrdenDeInspeccion también actualiza `orden.estado`
        System.out.println("DEBUG (GestorInspeccion): Cambio de estado de orden " + numeroOrden + " a 'Cerrada' registrado.");


        // 8) Guardar la orden actualizada en el repositorio
        // Utiliza repoOrdenes.guardar(orden) como lo tienes definido.
        repoOrdenes.guardar(orden);
        System.out.println("DEBUG (GestorInspeccion): Orden " + numeroOrden + " guardada exitosamente.");

        // CU - Paso 15: "El sistema notifica al área de mantenimiento la necesidad de retirar el sismógrafo."
        System.out.println("DEBUG (GestorInspeccion): Notificación al área de mantenimiento (conceptual).");
        System.out.println("DEBUG (GestorInspeccion): Proceso de cierre de orden " + numeroOrden + " completado.");
    }

    /**
     * Devuelve el Empleado asociado al usuario logueado.
     * CU: Implícito en varios pasos donde el sistema necesita saber quién está realizando la operación.
     * @return El Empleado logueado.
     * @throws IllegalStateException si no hay sesión activa o usuario logueado.
     */
    public Empleado obtenerEmpleadoLogueado() {
        if (sesion == null) {
            throw new IllegalStateException("No hay sesión activa.");
        }
        Empleado empleado = sesion.obtenerEmpleado();
        if (empleado == null) {
            throw new IllegalStateException("No hay un empleado asociado al usuario logueado.");
        }
        return empleado;
    }

    /**
     * CU: "Cerrar Orden de Inspección" - Paso 6: "El sistema presenta los posibles motivos para poner el sismógrafo fuera de servicio."
     * Obtiene y retorna todos los tipos de motivos de fuera de servicio disponibles.
     * @return Una lista de objetos MotivoTipo.
     */
    public List<MotivoTipo> obtenerMotivosFueraServicio() {
        System.out.println("DEBUG (GestorInspeccion): Obteniendo motivos de fuera de servicio.");
        // Asumo que RepositorioMotivoTipo tiene un método findAll()
        return repositorioMotivoTipo.findAll();
    }
}