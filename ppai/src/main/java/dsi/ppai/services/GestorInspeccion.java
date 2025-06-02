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
@Data
public class GestorInspeccion {

    private final RepositorioOrdenes repoOrdenes;
    private final RepositorioEstados repoEstados;
    private final Sesion sesion;
    private final RepositorioMotivoTipo repoMotivos;
/*
    //Buscar órdenes de inspección del RI logueado que están COMPLETAMENTE realizadas.
    public List<OrdenDeInspeccion> buscarOrdenesInspeccionDeRI() {
        Empleado empleado = sesion.obtenerEmpleadoLogueado();
        if (empleado == null) {
            System.out.println("Advertencia: No hay empleado logueado en la sesión para buscar órdenes de inspección.");
            return List.of();
        }

        return repoOrdenes
                .buscarOrdenesInspeccionDeRI(empleado.getLegajo())
                .stream()
                .filter(OrdenDeInspeccion::sosCompletamenteRealizada)
                .sorted(Comparator.comparing(OrdenDeInspeccion::getFechaHoraFinalizacion))
                .collect(Collectors.toList());
    }
*/
    public List<MotivoTipo> buscarTiposMotivosFueraDeServicios() {
        return repoMotivos.buscarTiposMotivosFueraDeServicios();
    }
    //Buscar órdenes de inspección del RI seleccionado que están COMPLETAMENTE realizadas
    public List<OrdenDeInspeccion> buscarOrdenesDeInspeccionDeRI(Empleado empleado) {
        if (empleado == null) {
            System.out.println("Advertencia: Se intentó buscar órdenes para un empleado nulo.");
            return List.of();
        }
        List<OrdenDeInspeccion> ordenes = repoOrdenes.findAll();
        ordenes = ordenes.stream()
                .filter(o -> o.sosDeEmpleado(empleado))
                .filter(OrdenDeInspeccion::sosCompletamenteRealizada)
                .collect(Collectors.toList());
        return ordenes;
    }
    public List<OrdenDeInspeccion> ordenarPorFechaDeFinalizacion(List<OrdenDeInspeccion> ordenes){
        return ordenes.stream()
                .sorted(Comparator.comparing(OrdenDeInspeccion::getFechaHoraFinalizacion))
                .collect(Collectors.toList());
    }
    public void cerrarOrden(OrdenDeInspeccion seleccionada,
                            String observacion,
                            List<MotivoFueraServicio> motivosSeleccionados) {
        // 1) Recupero el empleado logueado
        Empleado empleado = sesion.obtenerEmpleadoLogueado();
        if (empleado == null) {
            throw new IllegalStateException("No hay Responsable de Inspección logueado en la sesión.");
        }

        // 3) Validaciones
        if (!seleccionada.sosDeEmpleado(empleado)) {
            throw new IllegalStateException("La seleccionada  no pertenece al empleado logueado.");
        }
        if (!seleccionada.sosCompletamenteRealizada()) {
            throw new IllegalStateException("La seleccionada  no está totalmente realizada y no puede ser cerrada.");
        }
        if (observacion == null || observacion.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar una observación para el cierre.");
        }
        // 4) Completar datos de cierre de la ORDEN
        seleccionada.setFechaHoraCierre(LocalDateTime.now());
        seleccionada.setObservacionCierre(observacion);

        // 6) Cambiar el estado de la ORDEN a CERRADA y registrar el cambio en la ORDEN

        Estado estadoCerrada = repoEstados.sosCerrado("CERRADA");

        if (estadoCerrada == null) {
            throw new IllegalStateException("El estado 'CERRADA' no se encontró en el repositorio de estados.");
        }

        Estado estadoAnteriorOrden = seleccionada.getEstado();

        seleccionada.setEstado(estadoCerrada);

        CambioEstado cambioOrden = new CambioEstado(
                empleado,
                estadoAnteriorOrden,
                estadoCerrada,
                LocalDateTime.now(),
                null,
                null
        );
        seleccionada.registrarCambioEstado(cambioOrden);

        // 6) Guardar la seleccionada  actualizada
        repoOrdenes.insertar(seleccionada );
        // 7) Poner sismógrafo fuera de servicio
        if (motivosSeleccionados != null && !motivosSeleccionados.isEmpty()) {
            Estado estadoFueraDeServicio = repoEstados.sosFueraDeServicio("FUERA_DE_SERVICIO");
            if (estadoFueraDeServicio == null) {
                throw new IllegalStateException("El estado 'FUERA_DE_SERVICIO' no se encontró en el repositorio de estados.");
            }

            seleccionada.ponerFueraDeServicio(motivosSeleccionados, empleado, estadoFueraDeServicio);
        }



    }}