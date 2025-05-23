package dsi.ppai.services;

import dsi.ppai.entities.*;
import dsi.ppai.repositories.RepositorioEstados;
import dsi.ppai.repositories.RepositorioOrdenes;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GestorInspeccion {

    private final RepositorioOrdenes repoOrdenes;
    private final RepositorioEstados repoEstados;
    private final Sesion sesion;

    public GestorInspeccion(RepositorioOrdenes repoOrdenes, RepositorioEstados repoEstados, Sesion sesion) {
        this.repoOrdenes = repoOrdenes;
        this.repoEstados = repoEstados;
        this.sesion = sesion;
    }

    public void cerrarOrden(Long numeroOrden, String observacionCierre, List<MotivoFueraServicio> motivos) {
        OrdenDeInspeccion orden = repoOrdenes.buscarOrdenDeInspeccion(numeroOrden);

        if (orden == null) {
            throw new IllegalArgumentException("Orden de inspección no encontrada: " + numeroOrden);
        }

        if (orden.getEstadoActual().getNombre().equals("CERRADA") || orden.getEstadoActual().getNombre().equals("COMPLETAMENTE_REALIZADA")) {
            throw new IllegalStateException("La orden #" + numeroOrden + " ya se encuentra " + orden.getEstadoActual().getNombre() + " y no puede ser cerrada de nuevo.");
        }


        Estado estadoCerrada = repoEstados.buscarEstado("CERRADA");
        if (estadoCerrada == null) {
            throw new IllegalStateException("Estado 'CERRADA' no encontrado en el repositorio de estados. Verifique que está precargado con ese nombre.");
        }

        CambioEstado cambioOrdenCerrada = new CambioEstado(
                sesion.getEmpleadoLogueado(),
                orden.getEstadoActual(),
                estadoCerrada,
                LocalDateTime.now(),
                null,
                motivos
        );
        orden.agregarCambioEstado(cambioOrdenCerrada);
        orden.setEstadoActual(estadoCerrada);

        orden.setFechaHoraCierre(LocalDateTime.now());
        orden.setObservacionCierre(observacionCierre);

        EstacionSismologica estacion = orden.getNombreES();
        if (estacion != null && estacion.getSismografo() != null) {
            Sismografo sismografo = estacion.getSismografo();

            if (motivos != null && !motivos.isEmpty()) {
                Estado estadoFueraDeServicio = repoEstados.buscarEstado("FUERA DE SERVICIO");
                if (estadoFueraDeServicio == null) {
                    throw new IllegalStateException("Estado 'FUERA DE SERVICIO' no encontrado en el repositorio de estados. Verifique el nombre exacto.");
                }

                sismografo.marcarFueraDeServicio(sesion.getEmpleadoLogueado(), estadoFueraDeServicio, motivos);
                System.out.println("Sismógrafo " + sismografo.getIdentificadorSismografo() + " puesto en estado: " + sismografo.getEstadoActual().getNombre());
            } else {
                System.out.println("Sismógrafo " + sismografo.getIdentificadorSismografo() + " se mantiene en estado: " + (sismografo.getEstadoActual() != null ? sismografo.getEstadoActual().getNombre() : "N/A (sin cambio)"));
            }
        }

        // repoOrdenes.insertar(orden); // No es necesario si la referencia ya se actualizó

        System.out.println("Orden #" + numeroOrden + " cerrada y sismógrafo actualizado (si aplica).");
    }

    public List<OrdenDeInspeccion> obtenerOrdenesCompletadasDelRI(String legajoRI) {
        return repoOrdenes.buscarOrdenesInspeccionDeRI(legajoRI).stream()
                .filter(orden -> orden.getFechaHoraCierre() != null)
                .collect(Collectors.toList());
    }

    // ¡¡ESTE ES EL MÉTODO QUE DEBE ESTAR EN TU ARCHIVO!!
    public List<OrdenDeInspeccion> obtenerOrdenesAbiertasDelRI(String legajoRI) {
        Estado estadoAbierta = repoEstados.buscarEstado("ABIERTA");
        if (estadoAbierta == null) {
            System.err.println("Advertencia: Estado 'ABIERTA' no encontrado. Verifique configuración de estados.");
            return new ArrayList<>();
        }
        return repoOrdenes.buscarOrdenesInspeccionDeRI(legajoRI).stream()
                .filter(orden -> orden.getEstadoActual().equals(estadoAbierta))
                .collect(Collectors.toList());
    }
}