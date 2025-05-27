package dsi.ppai.services;

import dsi.ppai.entities.*;
import dsi.ppai.repositories.RepositorioEstados;
import dsi.ppai.repositories.RepositorioMotivoTipo;
import dsi.ppai.repositories.RepositorioOrdenes;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Data // Considera si es realmente necesario o si solo @Getter/@RequiredArgsConstructor es suficiente
public class GestorInspeccion {

    private final RepositorioOrdenes repoOrdenes;
    private final RepositorioEstados repoEstados;
    private final Sesion sesion;
    private final RepositorioMotivoTipo repoMotivos;

    /**
     * Busca las órdenes de inspección del RI que están COMPLETAMENTE realizadas.
     */
    public List<OrdenDeInspeccion> buscarOrdenesInspeccionDeRI() {
        Empleado empleado = sesion.obtenerEmpleadoLogueado();
        if (empleado == null) {
            System.out.println("Advertencia: No hay empleado logueado en la sesión para buscar órdenes de inspección.");
            return List.of();
        }

        return repoOrdenes
                .buscarOrdenesInspeccionDeRI(empleado.getLegajo()) // Este método ya filtra por legajo
                .stream()
                .filter(OrdenDeInspeccion::sosCompletamenteRealizada)
                .sorted(Comparator.comparing(OrdenDeInspeccion::getFechaHoraFinalizacion))
                .collect(Collectors.toList());
    }

    /**
     * @return una lista de MotivoTipo que corresponden a motivos para fuera de servicio.
     */
    public List<MotivoTipo> buscarTiposMotivosFueraDeServicios() {
        return repoMotivos.buscarTiposMotivosFueraDeServicios();
    }

    /**
     * **NUEVO MÉTODO**
     * Busca las órdenes de inspección 'Completamente Realizadas' para un empleado específico,
     * independientemente de quién esté logueado en la sesión.
     * @param empleado El objeto Empleado para el cual se buscarán las órdenes.
     * @return Una lista de OrdenDeInspeccion que están 'Completamente Realizadas' y pertenecen al empleado dado.
     */
    public List<OrdenDeInspeccion> buscarOrdenesDeInspeccionDeRI(Empleado empleado) {
        if (empleado == null) {
            System.out.println("Advertencia: Se intentó buscar órdenes para un empleado nulo.");
            return List.of();
        }

        // Usamos repoOrdenes.findAll() para obtener todas las órdenes y luego filtramos en memoria.
        // Esto es necesario ya que tus repositorios no son de base de datos relacional con consultas complejas.
        return repoOrdenes.findAll().stream() // Ahora el método se llama findAll()
                .filter(OrdenDeInspeccion::sosCompletamenteRealizada)
                .filter(orden -> orden.sosDeEmpleado(empleado))
                .sorted(Comparator.comparing(OrdenDeInspeccion::getFechaHoraFinalizacion))
                .collect(Collectors.toList());
    }


    /**
     * Cierra la orden con los datos provistos, marcando la estación y los sismógrafos
     * fuera de servicio (si aplica), cambiando el estado de la orden, registrando el cambio y notificando.
     */
    public void cerrarOrden(Long numeroOrden,
                            String observacion,
                            List<MotivoFueraServicio> motivosSeleccionados) {
        // 1) Recupero el empleado logueado
        Empleado empleado = sesion.obtenerEmpleadoLogueado();
        if (empleado == null) {
            throw new IllegalStateException("No hay Responsable de Inspección logueado en la sesión.");
        }

        // 2) Busco la orden
        OrdenDeInspeccion orden = repoOrdenes.buscarOrdenDeInspeccion(numeroOrden);
        if (orden == null) {
            throw new IllegalArgumentException("La orden no existe: " + numeroOrden);
        }

        // 3) Validaciones de pertenencia y estado
        if (!orden.sosDeEmpleado(empleado)) {
            throw new IllegalStateException("La orden no pertenece al empleado logueado.");
        }
        if (!orden.sosCompletamenteRealizada()) {
            throw new IllegalStateException("La orden no está totalmente realizada y no puede ser cerrada.");
        }
        if (observacion == null || observacion.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar una observación para el cierre.");
        }

        // 4) Completar datos de cierre de la ORDEN
        orden.setFechaHoraCierre(LocalDateTime.now());
        orden.setObservacionCierre(observacion);

        // 5) Poner sismógrafo fuera de servicio SI se indicaron motivos
        if (motivosSeleccionados != null && !motivosSeleccionados.isEmpty()) {
            Estado estadoFueraDeServicio = repoEstados.buscarEstado("FUERA_DE_SERVICIO");
            if (estadoFueraDeServicio == null) {
                throw new IllegalStateException("El estado 'FUERA_DE_SERVICIO' no se encontró en el repositorio de estados.");
            }
            orden.ponerFueraDeServicio(motivosSeleccionados, empleado, estadoFueraDeServicio);
        }

        // 6) Cambiar el estado de la ORDEN a CERRADA y registrar el cambio en la ORDEN
        Estado estadoCerrada = repoEstados.buscarEstado("CERRADA");
        if (estadoCerrada == null) {
            throw new IllegalStateException("El estado 'CERRADA' no se encontró en el repositorio de estados.");
        }
        Estado estadoAnteriorOrden = orden.getEstado();

        orden.setEstado(estadoCerrada);

        CambioEstado cambioOrden = new CambioEstado(
                empleado,
                estadoAnteriorOrden,
                estadoCerrada,
                LocalDateTime.now(),
                null,
                null
        );
        orden.registrarCambioEstado(cambioOrden);

        // 7) Guardar la orden actualizada (esto actualiza la referencia en el HashMap del repositorio)
        repoOrdenes.insertar(orden);
    }

    public Empleado obtenerEmpleadoLogueado() {
        if (sesion == null) {
            throw new IllegalStateException("No hay sesión activa.");
        }
        return sesion.obtenerEmpleadoLogueado();
    }
}