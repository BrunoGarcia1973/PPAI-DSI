package dsi.ppai.config;

import dsi.ppai.entities.*;
import dsi.ppai.repositories.RepositorioEstados;
import dsi.ppai.repositories.RepositorioMotivoTipo;
import dsi.ppai.repositories.RepositorioOrdenes;
import dsi.ppai.services.GestorInspeccion;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class InspeccionRunner implements CommandLineRunner {

    private final GestorInspeccion gestor;
    private final RepositorioOrdenes repoOrdenes;
    private final RepositorioEstados repoEstados;
    private final RepositorioMotivoTipo repoMotivos;
    private final Sesion sesion;

    public InspeccionRunner(GestorInspeccion gestor,
                            RepositorioOrdenes repoOrdenes,
                            RepositorioEstados repoEstados,
                            RepositorioMotivoTipo repoMotivos,
                            Sesion sesion) {
        this.gestor = gestor;
        this.repoOrdenes = repoOrdenes;
        this.repoEstados = repoEstados;
        this.repoMotivos = repoMotivos;
        this.sesion = sesion;
    }

    @Override
    public void run(String... args) throws Exception {


        //Instaciar sismografo
        Sismografo sismografo = new Sismografo();
        sismografo.setIdentificadorSismografo(14532);
        sismografo.setNroSerie(123456);
        sismografo.setFechaAdquisicion(LocalDate.now());
        sismografo.setCambiosDeEstados(new ArrayList<>());


        //Instanciar ES

        EstacionSismologica estacionSismologica = new EstacionSismologica();
        estacionSismologica.setEstacionId(424);
        estacionSismologica.setDocumentoCertificacionAdq("Certificado 1");
        estacionSismologica.setFechaSolicitudCertificacion(LocalDate.now());
        estacionSismologica.setLatitud(12.345);
        estacionSismologica.setLongitud(67.890);
        estacionSismologica.setNroCertificacionAdquisicion(123456789L);
        estacionSismologica.setNombre("Estacion villa maria");
        estacionSismologica.setSismografo(sismografo);

        // 1. Iniciar sesión del Responsable de Inspección
        Rol rolRI = new Rol("RESPONSABLE_INSPECCION", "Responsable de inspección");
        Empleado empleadoRI = new Empleado("1001", "Pérez", "perez@empresa.com", "Juan", "123456789", rolRI);
        Usuario usuario = new Usuario("jperez", empleadoRI);
        sesion.setUsuarioLogueado(usuario); // ✅ Usamos la sesión inyectada

        // 2. Crear orden de inspección asignada al RI
        Long numeroOrden = 1L;
        OrdenDeInspeccion orden = new OrdenDeInspeccion(numeroOrden, empleadoRI);
        Estado estadoAbierta = repoEstados.buscarEstado("CompletamenteRealizada"); // ahora debería existir
        orden.setEstado(estadoAbierta);
        orden.setEstacionSismologica(estacionSismologica);

        // Insertar orden en el repositorio
        repoOrdenes.insertar(orden);

        // 3. Simular que el RI selecciona una orden a cerrar
        System.out.println("Empleado logueado: " + empleadoRI.getNombre() + " " + empleadoRI.getApellido());
        System.out.println("Orden seleccionada: #" + numeroOrden);

        // 4. Mostrar motivos (simulamos que elige el primero)
        List<MotivoTipo> motivosDisponibles = repoMotivos.getMotivos();
        MotivoTipo motivoElegido = motivosDisponibles.get(0); // simulamos elección
        String observacion = "Se verificó y completó la inspección.";

        // 5. Cerrar orden con los datos ingresados
        gestor.cerrarOrden(numeroOrden, observacion, List.of(motivoElegido));

        // 6. Mostrar resultado final
        OrdenDeInspeccion cerrada = repoOrdenes.buscarOrdenDeInspeccion(numeroOrden);
        System.out.println("Orden cerrada. Estado final: " + cerrada.getEstado().getNombre());
        System.out.println("Observación: " + cerrada.getObservacionCierre());
        System.out.println("Motivos:");
        cerrada.getMotivos().forEach(m -> System.out.println(" - " + m.getDescripcion()));
        System.out.println("Fecha cierre: " + cerrada.getFechaHoraCierre());
    }
}
