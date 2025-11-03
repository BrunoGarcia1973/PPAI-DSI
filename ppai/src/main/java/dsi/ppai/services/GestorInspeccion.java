package dsi.ppai.services;

import dsi.ppai.entities.*;
import dsi.ppai.repositories.RepositorioEstados;
import dsi.ppai.repositories.RepositorioMotivoTipo;
import dsi.ppai.repositories.RepositorioOrdenes;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Data
public class GestorInspeccion {

    private final RepositorioOrdenes repoOrdenes;
    private final RepositorioEstados repoEstados;
    private final Sesion sesion;
    private final RepositorioMotivoTipo repoMotivos;

    //Buscar órdenes de inspección del RI que están COMPLETAMENTE realizadas.
    // Clase: GestorInspeccion (asumiendo)

    @Transactional(readOnly = true)
    public List<OrdenDeInspeccion> buscarOrdenesInspeccionDeRI() {
        Empleado empleado = sesion.obtenerEmpleadoLogueado();

        if (empleado == null) {
            System.out.println("Advertencia: No hay empleado logueado en la sesión para buscar órdenes de inspección.");
            return List.of();
        }

        // 1. Obtener la lista de órdenes del repositorio (sin filtrar ni ordenar)
        List<OrdenDeInspeccion> ordenesIniciales = repoOrdenes
                .buscarOrdenesInspeccionDeRI(empleado.getLegajo());

        // 2. Usar un ciclo for para filtrar las órdenes (reemplazando .stream().filter())
        List<OrdenDeInspeccion> ordenesFiltradas = new ArrayList<>();

        for (OrdenDeInspeccion orden : ordenesIniciales) {
            // Aplica la condición de filtrado
            if (orden.sosCompletamenteRealizada()) {
                ordenesFiltradas.add(orden);
            }
        }

        // 3. Ordenar la lista filtrada (reemplazando .sorted().collect())
        // Nota: Collections.sort() ordena la lista 'in place' (modifica la lista original).
        Collections.sort(ordenesFiltradas, Comparator.comparing(OrdenDeInspeccion::getFechaHoraFinalizacion));

        return ordenesFiltradas;
    }

    public List<MotivoTipo> buscarTiposMotivosFueraDeServicios() {
        return repoMotivos.buscarTiposMotivosFueraDeServicios();
    }
    //Buscar órdenes de inspección del RI seleccionado
    @Transactional(readOnly = true)
    public List<OrdenDeInspeccion> buscarOrdenesDeInspeccionDeRI(Empleado empleado) {
        if (empleado == null) {
            System.out.println("Advertencia: Se intentó buscar órdenes para un empleado nulo.");
            return List.of();
        }

        return repoOrdenes.findAll().stream()
                .filter(OrdenDeInspeccion::sosCompletamenteRealizada)
                .filter(orden -> orden.sosDeEmpleado(empleado))
               // .sorted(Comparator.comparing(o -> o.getFechaHoraFinalizacion() != null ? o.getFechaHoraFinalizacion() : OffsetDateTime.MIN))
                .collect(Collectors.toList());
    }

    @Transactional
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
        // 3) Validaciones
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
        orden.setFechaHoraCierre(OffsetDateTime.now().toLocalDateTime());
        orden.setObservacionCierre(observacion);
        // 5) Poner sismógrafo fuera de servicio
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
                OffsetDateTime.now(),
                null,
                null
        );
        orden.registrarCambioEstado(cambioOrden);

        // 7) Guardar la orden actualizada
        repoOrdenes.insertar(orden);

        //public void enviarCorreos(Empleado empleado){

        //}
    }

   /* public Empleado obtenerEmpleadoLogueado() {
        if (sesion == null) {
            throw new IllegalStateException("No hay sesión activa.");
        }
        return sesion.obtenerEmpleadoLogueado();
    }*/
}