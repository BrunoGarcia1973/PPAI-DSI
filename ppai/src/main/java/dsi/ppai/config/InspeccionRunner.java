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
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class InspeccionRunner implements CommandLineRunner {

    private final GestorInspeccion gestor;
    private final RepositorioOrdenes repoOrdenes;
    private final RepositorioEstados repoEstados;
    private final RepositorioMotivoTipo repoMotivos;
    private final Sesion sesion;
    private final Scanner scanner = new Scanner(System.in);

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
        // Instanciar sismografo
        Sismografo sismografo = new Sismografo();
        sismografo.setIdentificadorSismografo(14532);
        sismografo.setNroSerie(123456);
        sismografo.setFechaAdquisicion(LocalDate.now());
        sismografo.setCambiosDeEstados(new ArrayList<>());

        // Instanciar ES
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
        sesion.setUsuarioLogueado(usuario);

        // 2. Crear orden de inspección asignada al RI (inicialmente COMPLETAMENTE_REALIZADA)
        Long numeroOrden = 1L;
        OrdenDeInspeccion orden = new OrdenDeInspeccion(numeroOrden, empleadoRI);
        Estado estadoCompletamenteRealizada = repoEstados.buscarEstado("CompletamenteRealizada");
        orden.setEstado(estadoCompletamenteRealizada);
        orden.setEstacionSismologica(estacionSismologica);
        repoOrdenes.insertar(orden);
        System.out.println("Orden #" + numeroOrden + " creada en estado: " + orden.getEstado().getNombre());

        // Simular el paso a COMPLETAMENTE_REALIZADA
        // gestor.cambiarEstadoACompletamenteRealizada(numeroOrden);
        OrdenDeInspeccion ordenCompletada = repoOrdenes.buscarOrdenDeInspeccion(numeroOrden);
        System.out.println("Orden #" + numeroOrden + " ahora en estado: " + ordenCompletada.getEstado().getNombre());

        // 3. RI selecciona una orden a cerrar
        List<OrdenDeInspeccion> ordenesCompletadas = repoOrdenes.buscarOrdenesInspeccionDeRI(empleadoRI.getLegajo()).stream()
                .filter(o -> o.getEstado().getNombre().equalsIgnoreCase("CompletamenteRealizada"))
                .collect(Collectors.toList());

        if (ordenesCompletadas.isEmpty()) {
            System.out.println("No hay órdenes completadas para cerrar.");
            scanner.close();
            return;
        }

        OrdenDeInspeccion ordenSeleccionada = ordenesCompletadas.get(0);
        System.out.println("\nÓrdenes completadas del RI:");
        System.out.println("1. Orden #" + ordenSeleccionada.getNumOrden());
        System.out.print("Seleccione el número de orden a cerrar: ");
        long numeroOrdenCierre = scanner.nextLong();
        scanner.nextLine();

        if (numeroOrdenCierre != ordenSeleccionada.getNumOrden()) {
            System.out.println("Número de orden inválido.");
            scanner.close();
            return;
        }

        // 4. Mostrar motivos de fuera de servicio
        List<MotivoTipo> motivosDisponibles = repoMotivos.getMotivos();
        System.out.println("\nMotivos de fuera de servicio disponibles:");
        for (int i = 0; i < motivosDisponibles.size(); i++) {
            System.out.println((i + 1) + ". " + motivosDisponibles.get(i).getDescripcion());
        }

        // 5. RI ingresa la observación de cierre
        System.out.print("Ingrese la observación de cierre: ");
        String observacion = scanner.nextLine();

        // 6. RI selecciona uno o varios motivos de fuera de servicio
        System.out.print("Seleccione los números de los motivos (separados por coma): ");
        String motivosSeleccionadosInput = scanner.nextLine();
        List<Integer> indicesSeleccionados = new ArrayList<>();
        for (String s : motivosSeleccionadosInput.split(",")) {
            try {
                indicesSeleccionados.add(Integer.parseInt(s.trim()) - 1);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida para los motivos.");
                scanner.close();
                return;
            }
        }

        List<MotivoTipo> motivosCierre = new ArrayList<>();
        for (int indice : indicesSeleccionados) {
            if (indice >= 0 && indice < motivosDisponibles.size()) {
                motivosCierre.add(motivosDisponibles.get(indice));
            } else {
                System.out.println("Índice de motivo inválido: " + (indice + 1));
                scanner.close();
                return;
            }
        }

        // Crear lista de MotivoFueraServicio
        List<MotivoFueraServicio> motivosFueraServicioCierre = motivosCierre.stream()
                .map(motivoTipo -> new MotivoFueraServicio(observacion, motivoTipo))
                .collect(Collectors.toList());

        // 7. El sistema solicita confirmación para cerrar la orden
        System.out.print("\n¿Confirmar el cierre de la Orden #" + numeroOrdenCierre + "? (S/N): ");
        String confirmacion = scanner.nextLine();

        if (confirmacion.equalsIgnoreCase("S")) {
            // 8. RI confirma el cierre

            // 9, 10, 11. El sistema cierra la orden y actualiza el sismógrafo
            gestor.cerrarOrden(numeroOrdenCierre, observacion, motivosFueraServicioCierre);
            System.out.println("Orden #" + numeroOrdenCierre + " cerrada exitosamente.");

            // 12. El sistema notifica el cierre
            System.out.println("Notificación de cierre enviada.");

            // Mostrar resultado final
            OrdenDeInspeccion cerrada = repoOrdenes.buscarOrdenDeInspeccion(numeroOrdenCierre);
            System.out.println(cerrada);
            System.out.println("\nDetalles de la orden cerrada:");
            System.out.println("Estado final: " + cerrada.getEstado().getNombre());
            System.out.println("Observación: " + cerrada.getObservacionCierre());
            System.out.println("Motivos:");
            if (cerrada.getCambios() != null) {
                for (CambioEstado cambio : cerrada.getCambios()) {
                    if (cambio.getMotivos() != null) {
                        for (MotivoTipo motivoFueraServicio : cambio.getMotivos()) {
                            if (motivoFueraServicio.getMotivos() != null) {
                                System.out.println(" - " + motivoFueraServicio.getMotivos());
                            }
                        }
                    }
                }
            }
            System.out.println("Fecha cierre: " + cerrada.getFechaHoraCierre());

        } else {
            System.out.println("Cierre de orden cancelado.");

        }
        scanner.close();
    }
}