package dsi.ppai.services;
import dsi.ppai.entities.Empleado;
import dsi.ppai.entities.MotivoTipo;
import dsi.ppai.entities.OrdenDeInspeccion;
import dsi.ppai.entities.Sesion;
import dsi.ppai.repositories.RepositorioEstados;
import dsi.ppai.repositories.RepositorioOrdenes;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;


@Service
public class GestorInspeccion {

    private RepositorioOrdenes repoOrdenes;
    private RepositorioEstados repoEstados;

    public GestorInspeccion(RepositorioOrdenes repoOrdenes, RepositorioEstados repoEstados) {
        this.repoOrdenes = repoOrdenes;
        this.repoEstados = repoEstados;
    }

    public void cerrarOrden(Long numeroOrden, String observacion, List<MotivoTipo> motivosSeleccionados) {
        Empleado empleado = Sesion.getInstancia().obtenerEmpleado();

        // Buscar la orden
        OrdenDeInspeccion orden = repoOrdenes.buscarOrdenesInspeccion(numeroOrden);
        if (orden == null) {
            throw new IllegalArgumentException("La orden no existe.");
        }

        // Validar que pertenezca al empleado
        if (!orden.sosDeEmpleado(empleado)) {
            throw new IllegalStateException("La orden no pertenece al empleado logueado.");
        }

        // Validar que esté realizada
        if (!orden.sosCompletamenteRealizada()) {
            throw new IllegalStateException("La orden no fue completamente realizada.");
        }

        // Validar que haya observación
        if (observacion == null || observacion.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar una observación.");
        }

        // Establecer datos de cierre
        orden.setFechaHoraCierre(LocalDateTime.now());
        orden.setObservacionCierre(observacion);

        // Marcar estación y sismógrafos como fuera de servicio
        orden.ponerFueraDeServicio(motivosSeleccionados);

        // Cambiar estado de la orden
        Estado estadoCerrada = repoEstados.buscarEstado("CERRADA");
        orden.setEstado(estadoCerrada);

        // Registrar cambio de estado
        CambioEstadoOrden cambio = new CambioEstadoOrden(estadoCerrada, LocalDateTime.now(), motivosSeleccionados);
        orden.registrarCambioEstado(cambio);

        // Notificar responsables
        List<Empleado> responsables = orden.buscarResponsablesDeReparacion();
        for (Empleado e : responsables) {
            Notificador.enviarCorreo(e.getEmail(), "Se cerró una orden con intervención requerida.");
        }

        // Guardar cambios (si aplica)
        repoOrdenes.guardar(orden);
    }

    public static String seleccionOI(){
        Scanner sc = new Scanner(System.in);
        Long seleccion = sc.nextLong();
        String response = String.valueOf(seleccion);
        return response;
    }


}
