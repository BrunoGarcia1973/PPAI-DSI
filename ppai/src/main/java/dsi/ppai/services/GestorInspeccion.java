package dsi.ppai.services;

import dsi.ppai.entities.*;
import dsi.ppai.repositories.RepositorioEstados;
import dsi.ppai.repositories.RepositorioMotivoTipo;
import dsi.ppai.repositories.RepositorioOrdenes;
import dsi.ppai.repositories.RepositorioUsuarios;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Data
public class GestorInspeccion {

    private final RepositorioOrdenes repoOrdenes;
    private final RepositorioEstados repoEstados;
    private final Sesion sesion;
    private final RepositorioMotivoTipo repoMotivos;
    private final RepositorioUsuarios repoUsuarios;

    // --- MÉTODOS AUXILIARES DE BÚSQUEDA ---

    /**
     * Busca la Orden de Inspeccion por el número de orden (numOrden).
     * Nota: Requiere que RepositorioOrdenes tenga una implementación que use findByNumOrden.
     */
    private Optional<OrdenDeInspeccion> buscarOrdenDeInspeccion(Long numeroOrden) {
        // Asumo que el método default en RepositorioOrdenes mapea esto a findByNumOrden con Fetching
        return Optional.ofNullable(repoOrdenes.buscarOrdenDeInspeccion(numeroOrden));
    }

    /**
     * Busca el Usuario por nombre de usuario de forma transaccional (para Login).
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarUsuarioPorNombre(String nombreUsuario) {
        Optional<Usuario> usuarioOpt = repoUsuarios.findByNombreUsuario(nombreUsuario);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            Empleado empleado = usuario.getEmpleado();
            if (empleado != null) {
                empleado.getId(); // Fuerza la carga de Empleado (si fuera LAZY)
            }
        }
        return usuarioOpt;
    }


    // --- LÓGICA DEL CASO DE USO: CARGA Y CIERRE DE ÓRDENES ---

    /**
     * Busca órdenes de inspección del RI logueado que están COMPLETAMENTE realizadas.
     */
    @Transactional(readOnly = true)
    public List<OrdenDeInspeccion> buscarOrdenesInspeccionDeRI() {
        Empleado empleado = sesion.obtenerEmpleadoLogueado();

        if (empleado == null) {
            System.out.println("Advertencia: No hay empleado logueado en la sesión para buscar órdenes de inspección.");
            return List.of();
        }

        final String NOMBRE_ESTADO_REQUERIDO = "COMPLETAMENTE_REALIZADA";

        Estado estadoRequerido = repoEstados.buscarEstado(NOMBRE_ESTADO_REQUERIDO);

        if (estadoRequerido == null || estadoRequerido.getId() == null) {
            System.err.println("ERROR: No se encontró la entidad Estado con ID válido para: " + NOMBRE_ESTADO_REQUERIDO);
            return List.of();
        }

        Long empleadoId = empleado.getId();
        Long estadoId = estadoRequerido.getId();

        // Consulta corregida: findByEmpleado_IdAndEstado_Id
        List<OrdenDeInspeccion> ordenesFiltradas = repoOrdenes
                .findByEmpleado_IdAndEstado_Id(empleadoId, estadoId);

        Collections.sort(ordenesFiltradas, Comparator.comparing(OrdenDeInspeccion::getFechaHoraFinalizacion,
                Comparator.nullsLast(Comparator.naturalOrder())));

        System.out.println("DEBUG: El gestor encontró " + ordenesFiltradas.size() + " órdenes 'CR' para el empleado ID: " + empleadoId);

        return ordenesFiltradas;
    }

    /**
     * [Paso 1 - 13] Ejecuta la lógica para cerrar una Orden de Inspección.
     */
    @Transactional
    public void cerrarOrden(Long numeroOrden,
                            String observacion,
                            List<MotivoFueraServicio> motivosSeleccionados) {

        // 1) Recuperar el empleado logueado
        Empleado empleado = sesion.obtenerEmpleadoLogueado();
        if (empleado == null) {
            throw new IllegalStateException("No hay Responsable de Inspección logueado en la sesión.");
        }

        // 2) Busco la orden (Usando el método auxiliar)
        Optional<OrdenDeInspeccion> ordenOpt = buscarOrdenDeInspeccion(numeroOrden);

        if (ordenOpt.isEmpty()) {
            throw new IllegalArgumentException("La orden no existe: " + numeroOrden);
        }

        OrdenDeInspeccion orden = ordenOpt.get();

        // 3) Validaciones (SosDeEmpleado, SosCompletamenteRealizada, Observación)
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
        Estado estadoAnteriorOrden = orden.getEstado();

        orden.setFechaHoraCierre(OffsetDateTime.now().toLocalDateTime()); // [Paso 11, Parcial]
        orden.setObservacionCierre(observacion);

        // 5) Poner sismógrafo fuera de servicio (NUEVA LÓGICA: Gestor -> Sismografo) [Paso 12]
        if (motivosSeleccionados != null && !motivosSeleccionados.isEmpty()) {
            Estado estadoFueraDeServicio = repoEstados.buscarEstado("FUERA_DE_SERVICIO");
            if (estadoFueraDeServicio == null) {
                throw new IllegalStateException("El estado 'FUERA_DE_SERVICIO' no se encontró.");
            }

            EstacionSismologica estacion = orden.getEstacionSismologica();
            if (estacion == null) {
                throw new IllegalStateException("La orden no tiene Estación Sismológica asociada.");
            }

            // Asumo que EstacionSismologica.getSismografo() devuelve el sismógrafo correcto
            Sismografo sismografo = estacion.getSismografo();
            if (sismografo == null) {
                throw new IllegalStateException("La Estación Sismológica no tiene Sismógrafo asociado.");
            }

            // Llamada directa al Sismografo para marcar el FUERA DE SERVICIO
            sismografo.marcarFueraDeServicio(motivosSeleccionados, empleado, estadoFueraDeServicio);
        }

        // 6) Cambiar el estado de la ORDEN a CERRADA y registrar el cambio [Paso 11]
        Estado estadoCerrada = repoEstados.buscarEstado("CERRADA");
        if (estadoCerrada == null) {
            throw new IllegalStateException("El estado 'CERRADA' no se encontró.");
        }

        orden.setEstado(estadoCerrada);

        CambioEstado cambioOrden = new CambioEstado(
                empleado,
                estadoAnteriorOrden,
                estadoCerrada,
                OffsetDateTime.now(),
                null,
                null
        );
        orden.registrarCambioEstado(cambioOrden);

        // 7) Guardar la orden actualizada (Persistencia)
        repoOrdenes.insertar(orden);

        // 8) Envío de Notificaciones [Paso 13]
        enviarNotificaciones(empleado);
    }

    // --- MÉTODOS ADICIONALES ---

    public List<MotivoTipo> buscarTiposMotivosFueraDeServicios() {
        return repoMotivos.buscarTiposMotivosFueraDeServicios();
    }

    @Transactional(readOnly = true)
    public List<OrdenDeInspeccion> buscarOrdenesDeInspeccionDeRI(Empleado empleado) {
        if (empleado == null) return List.of();

        final String NOMBRE_ESTADO_REQUERIDO = "COMPLETAMENTE_REALIZADA";
        Estado estadoRequerido = repoEstados.buscarEstado(NOMBRE_ESTADO_REQUERIDO);
        if (estadoRequerido == null || estadoRequerido.getId() == null) return List.of();

        List<OrdenDeInspeccion> ordenesFiltradas = repoOrdenes
                .findByEmpleado_IdAndEstado_Id(empleado.getId(), estadoRequerido.getId());

        Collections.sort(ordenesFiltradas, Comparator.comparing(OrdenDeInspeccion::getFechaHoraFinalizacion,
                Comparator.nullsLast(Comparator.naturalOrder())));
        return ordenesFiltradas;
    }

    private void enviarNotificaciones(Empleado empleado) {
        // Implementación de simulación de notificaciones
        System.out.println("LOG: Enviando notificaciones de cierre de orden por el empleado: " + empleado.getNombre());
    }
}