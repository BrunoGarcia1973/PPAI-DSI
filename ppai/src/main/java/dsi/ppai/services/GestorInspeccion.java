package dsi.ppai.services;
import dsi.ppai.entities.Empleado;
import dsi.ppai.entities.EstacionSismologica;
import dsi.ppai.entities.Estado;
import dsi.ppai.entities.MotivoTipo;
import dsi.ppai.entities.OrdenDeInspeccion;
import dsi.ppai.entities.Sesion;
import dsi.ppai.repositories.RepositorioEstados;
import dsi.ppai.repositories.RepositorioOrdenes;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;


@Service
public class GestorInspeccion {

    private RepositorioOrdenes repoOrdenes;
    private RepositorioEstados repoEstados;
    private Sesion sesionActual;
    private Empleado empleadoLogueado;
    private List<OrdenDeInspeccion> ordenesDeInspeccion;

    ////////////////////////////CONSTRUCTOR/////////////////////////////////////
    public GestorInspeccion(RepositorioOrdenes repoOrdenes, RepositorioEstados repoEstados) {
        this.repoOrdenes = repoOrdenes;
        this.repoEstados = repoEstados;
    }

    ///////OBTENER EL USUARIO
    // Método privado porque solo se llama una vez internamente
    private Empleado obtenerEmpleadoLogueado() {
        return sesionActual.obtenerEmpleadoLogueado();
    }

    // Método público para acceder al empleado si hiciera falta
    public Empleado getEmpleadoLogueado() {
        return empleadoLogueado;
    }
    ////////////////////////////////////////////////////////
    
    ///BUSCAR las ordenes de inspeccion del RI
    public void buscarOrdenesInspeccionDeRI() {
        for (OrdenDeInspeccion orden : ordenesDeInspeccion) {
        if (orden.sosDeEmpleado(empleadoLogueado)) {
            if (orden.sosCompletamenteRealizado()) { //Si es Completamente realizada pido los datos
                Long numero = orden.getNumOrden(); // self
                LocalDateTime fecha = orden.getFechaFinalizacion(); // self
                EstacionSismologica estacion = orden.getNombreES(); // self  //Esto lo que tiene que ver con dependencia
                String nombreEstacion = estacion.getNombre(); // mensaje a EstacionSismologica
                String identificadorSismografo = orden.getIdentificadorSismografo(); // self → mensaje a EstacionSismologica → mensaje a Sismografo
                //Esto lo que tiene que ver con dependencia, lo de arriba
            } else {
                System.out.println("Orden INCOMPLETA encontrada: " + orden);
            }
        }
    }
}

    ///SIGUIENTE PASO ORDENAR
    
    



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
