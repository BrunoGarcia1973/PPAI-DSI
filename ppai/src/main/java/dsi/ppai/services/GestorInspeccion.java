package dsi.ppai.services;

import dsi.ppai.entities.*;

import dsi.ppai.repositories.RepositorioEstados;

import dsi.ppai.repositories.RepositorioOrdenes;

import lombok.Data;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;



import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;


@Service

@RequiredArgsConstructor

@Data

public class GestorInspeccion {



    private final RepositorioOrdenes repoOrdenes;

    private final RepositorioEstados repoEstados;

    private final Sesion sesion;

    /**

     * Busca las órdenes de inspección del RI que están COMPLETAMENTE realizadas.

     */

    public List<OrdenDeInspeccion> buscarOrdenesInspeccionDeRI() {

// 1) Obtengo el empleado logueado desde la sesión

        Empleado empleado = sesion.obtenerEmpleado();



// 2) Pido al repositorio las órdenes de ese RI y filtro las completadas

        return repoOrdenes

                .buscarOrdenesInspeccionDeRI(empleado.getLegajo())

                .stream()

                .filter(OrdenDeInspeccion::sosCompletamenteRealizada)

                .collect(Collectors.toList());

    }



    /**

     * Cierra la orden con los datos provistos, marcando la estación y los sismógrafos

     * fuera de servicio, cambiando el estado, registrando el cambio y notificando.

     */

    public void cerrarOrden(Long numeroOrden,

                            String observacion,

                            List<MotivoFueraServicio> motivosSeleccionados) {

// 1) Recupero el empleado logueado

        Empleado empleado = sesion.obtenerEmpleado();



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

            throw new IllegalStateException("La orden no está totalmente realizada.");

        }

        if (observacion == null || observacion.isBlank()) {

            throw new IllegalArgumentException("Debe ingresar una observación para el cierre.");

        }



// 4) Completar datos de cierre

        orden.setFechaHoraCierre(LocalDateTime.now());

        orden.setObservacionCierre(observacion);

        orden.ponerFueraDeServicio(motivosSeleccionados);



// 5) Cambiar el estado a CERRADA y registrar el cambio

        Estado estadoCerrada = repoEstados.buscarEstado("CERRADA");

        Estado estadoAnterior = orden.getEstado();

        orden.setEstado(estadoCerrada);



        CambioEstado cambio = new CambioEstado(

                empleado,

                estadoAnterior,

                estadoCerrada,

                LocalDateTime.now(),

                null, // La fechaHoraFin se asigna al cerrar el cambio

                motivosSeleccionados

        );

        orden.registrarCambioEstado(cambio);





        repoOrdenes.insertar(orden);

    }



    public Empleado obtenerEmpleadoLogueado() {

        if (sesion == null) {

            throw new IllegalStateException("No hay sesión activa.");

        }

        return sesion.obtenerEmpleado();

    }

}