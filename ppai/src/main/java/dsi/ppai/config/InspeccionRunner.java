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
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

//@Component
public class InspeccionRunner implements CommandLineRunner {

    private final GestorInspeccion gestor;
    private final RepositorioOrdenes repoOrdenes;
    private final RepositorioEstados repoEstados;
    private final RepositorioMotivoTipo repoMotivos;
    private final Sesion sesion;
    private final Scanner scanner = new Scanner(System.in);
    private Long numeroOrden = 1L; // Inicializa numeroOrden a nivel de clase

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
        // Instanciar sismografos
        /*
        Sismografo sismografoA = new Sismografo();
        sismografoA.setIdentificadorSismografo(32149);
        sismografoA.setNroSerie(12345632);
        sismografoA.setFechaAdquisicion(LocalDate.now());
        sismografoA.setCambiosDeEstados(new ArrayList<>());

        Sismografo sismografoB = new Sismografo();
        sismografoB.setIdentificadorSismografo(98765);
        sismografoB.setNroSerie(78901234);
        sismografoB.setFechaAdquisicion(LocalDate.of(2024, 10, 20));
        sismografoB.setCambiosDeEstados(new ArrayList<>());

        Sismografo sismografoC = new Sismografo();
        sismografoC.setIdentificadorSismografo(11223);
        sismografoC.setNroSerie(55667788);
        sismografoC.setFechaAdquisicion(LocalDate.of(2023, 5, 10));
        sismografoC.setCambiosDeEstados(new ArrayList<>());

        // Instanciar ES
        EstacionSismologica estacionSismologicaA = new EstacionSismologica();
        estacionSismologicaA.setEstacionId(424);
        estacionSismologicaA.setDocumentoCertificacionAdq("Certificado 1");
        estacionSismologicaA.setFechaSolicitudCertificacion(LocalDate.now());
        estacionSismologicaA.setLatitud(12.345);
        estacionSismologicaA.setLongitud(67.890);
        estacionSismologicaA.setNroCertificacionAdquisicion(123456789L);
        estacionSismologicaA.setNombre("Estacion villa maria");
        estacionSismologicaA.setSismografo(sismografoC);

        EstacionSismologica estacionSismologicaB = new EstacionSismologica();
        estacionSismologicaB.setEstacionId(789);
        estacionSismologicaB.setDocumentoCertificacionAdq("Certificado 2");
        estacionSismologicaB.setFechaSolicitudCertificacion(LocalDate.of(2025, 6, 1));
        estacionSismologicaB.setLatitud(-30.567);
        estacionSismologicaB.setLongitud(-65.123);
        estacionSismologicaB.setNroCertificacionAdquisicion(987654321L);
        estacionSismologicaB.setNombre("Estacion alta gracia");
        estacionSismologicaB.setSismografo(sismografoA);

        // 1. Crear Responsables de Inspección
        Rol rolRI = new Rol("RESPONSABLE_INSPECCION", "Responsable de inspección");
        List<Empleado> empleadosRI = new ArrayList<>();
        Empleado empleadoRI_Juan = new Empleado("1001", "Pérez", "perez@empresa.com", "Juan", "123456789", rolRI);
        Empleado empleadoRI_Laura = new Empleado("2002", "González", "gonzalez@empresa.com", "Laura", "987654321", rolRI);
        Empleado empleadoRI_federico = new Empleado("3002", "Girotti", "fgirotti@empresa.com", "federico", "351745678", rolRI);

        empleadosRI.add(empleadoRI_Juan);
        empleadosRI.add(empleadoRI_Laura);
        empleadosRI.add(empleadoRI_federico);

        // Simular selección de usuario
        System.out.println("--- Seleccione un Responsable de Inspección para iniciar sesión ---");
        for (int i = 0; i < empleadosRI.size(); i++) {
            System.out.println((i + 1) + ". " + empleadosRI.get(i).getNombre() + " " + empleadosRI.get(i).getApellido() + " (Legajo: " + empleadosRI.get(i).getLegajo() + ")");
        }
        System.out.print("Ingrese el número del empleado para loguearse o 'cancelar' para salir:");
        String seleccionEmpleadoInput = scanner.nextLine();

        Empleado empleadoLogueado = null;
        if (seleccionEmpleadoInput.equalsIgnoreCase("cancelar")) {
            System.out.println("Ejecución del caso de uso cancelada por el usuario.");
            scanner.close();
            return;
        }

        try {
            int seleccionEmpleado = Integer.parseInt(seleccionEmpleadoInput);
            if (seleccionEmpleado >= 1 && seleccionEmpleado <= empleadosRI.size()) {
                empleadoLogueado = empleadosRI.get(seleccionEmpleado - 1);
                Usuario usuario = new Usuario(empleadoLogueado.getNombre().toLowerCase(), empleadoLogueado);
                sesion.setUsuarioLogueado(usuario);
                System.out.println("Empleado logueado: " + empleadoLogueado.getNombre() + " " + empleadoLogueado.getApellido() + " (Legajo: " + empleadoLogueado.getLegajo() + ")");
            } else {
                System.out.println("Selección de empleado inválida. Saliendo.");
                scanner.close();
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor, ingrese un número o 'cancelar'. Saliendo.");
            scanner.close();
            return;
        }

        // 2. Crear algunas órdenes de inspección (inicialmente COMPLETAMENTE_REALIZADA)
        OrdenDeInspeccion orden1 = new OrdenDeInspeccion(numeroOrden++, LocalDate.now().minusDays(2).atTime(10, 0), empleadoRI_Juan, LocalDate.now().minusDays(1).atTime(15, 30), "Inspección completa Juan", LocalDate.now().minusDays(1).atTime(15, 0), repoEstados.buscarEstado("CompletamenteRealizada"), estacionSismologicaA);
        repoOrdenes.insertar(orden1);

        OrdenDeInspeccion orden2 = new OrdenDeInspeccion(numeroOrden++, LocalDate.now().minusDays(5).atTime(9, 0), empleadoRI_Laura, LocalDate.now().minusDays(4).atTime(12, 45), "Todo OK Laura", LocalDate.now().minusDays(4).atTime(12, 0), repoEstados.buscarEstado("CompletamenteRealizada"), estacionSismologicaB);
        repoOrdenes.insertar(orden2);

        OrdenDeInspeccion orden3 = new OrdenDeInspeccion(numeroOrden++, LocalDate.now().minusDays(1).atTime(14, 0), empleadoRI_Juan, LocalDate.now().atTime(11, 0), "Sin novedades Juan", LocalDate.now().atTime(10, 30), repoEstados.buscarEstado("CompletamenteRealizada"), estacionSismologicaA);
        repoOrdenes.insertar(orden3);

        // 3. Mostrar las órdenes COMPLETAMENTE_REALIZADAS del RI logueado
        List<OrdenDeInspeccion> ordenesCompletadasRI = repoOrdenes.buscarOrdenesInspeccionDeRI(empleadoLogueado.getLegajo()).stream()
                .filter(o -> o.getEstado().getNombre().equalsIgnoreCase("CompletamenteRealizada"))
                .sorted(Comparator.comparing(OrdenDeInspeccion::getFechaHoraFinalizacion)) // Ordenar por fecha de finalización
                .collect(Collectors.toList());

        if (ordenesCompletadasRI.isEmpty()) {
            System.out.println("No hay órdenes completadas para el responsable " + empleadoLogueado.getNombre() + ".");
            System.out.println("Ingrese 'cancelar' para salir.");
            if (scanner.nextLine().equalsIgnoreCase("cancelar")) {
                System.out.println("Ejecución del caso de uso cancelada por el usuario.");
                scanner.close();
                return;
            }
        }

        System.out.println("\nÓrdenes COMPLETAMENTE REALIZADAS del responsable " + empleadoLogueado.getNombre() + ":");
        System.out.println("Ingrese el número de la orden para seleccionar o 'cancelar' para salir:");
        for (int i = 0; i < ordenesCompletadasRI.size(); i++) {
            OrdenDeInspeccion orden = ordenesCompletadasRI.get(i);
            System.out.println((i + 1) + ". Orden #" + orden.getNumOrden() +
                    ", Finalización: " + (orden.getFechaHoraFinalizacion() != null ? orden.getFechaHoraFinalizacion().toString() : "N/A") +
                    ", Estación: " + (orden.getNombreES() != null ? orden.getNombreES().getNombre() : "N/A") +
                    ", Sismógrafo ID: " + orden.getIdentificadorSismografo());
        }

        System.out.print("Seleccione el número de orden o ingrese 'cancelar': ");
        String seleccionOrdenInput = scanner.nextLine();

        if (seleccionOrdenInput.equalsIgnoreCase("cancelar")) {
            System.out.println("Ejecución del caso de uso cancelada por el usuario.");
            scanner.close();
            return;
        }

        int seleccionOrden;
        try {
            seleccionOrden = Integer.parseInt(seleccionOrdenInput);
            if (seleccionOrden < 1 || seleccionOrden > ordenesCompletadasRI.size()) {
                System.out.println("Selección de orden inválida. Saliendo.");
                scanner.close();
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor, ingrese un número o 'cancelar'. Saliendo.");
            scanner.close();
            return;
        }

        System.out.println("Ingresa la observación de cierre de la orden (o 'cancelar'): ");
        String observacionCierre = scanner.nextLine();
        if (observacionCierre.equalsIgnoreCase("cancelar")) {
            System.out.println("Ejecución del caso de uso cancelada por el usuario.");
            scanner.close();
            return;
        }

        OrdenDeInspeccion ordenSeleccionada = ordenesCompletadasRI.get(seleccionOrden - 1);
        Long numeroOrdenCierre = ordenSeleccionada.getNumOrden();
        System.out.println("Orden seleccionada para cerrar: #" + numeroOrdenCierre);

        // 5. Mostrar motivos de fuera de servicio
        List<MotivoTipo> motivosDisponibles = repoMotivos.getMotivos();
        System.out.println("\nMotivos de fuera de servicio disponibles:");
        System.out.println("Ingrese el número del motivo para seleccionar, o 'cancelar' para salir:");
        for (int i = 0; i < motivosDisponibles.size(); i++) {
            System.out.println((i + 1) + ". " + motivosDisponibles.get(i).getDescripcion());
        }

        System.out.print("Seleccione los números (separados por coma) o ingrese 'cancelar': ");
        String motivosSeleccionadosInput = scanner.nextLine();

        if (motivosSeleccionadosInput.equalsIgnoreCase("cancelar")) {
            System.out.println("Ejecución del caso de uso cancelada por el usuario.");
            scanner.close();
            return;
        }

        List<Integer> indicesSeleccionados = new ArrayList<>();
        for (String s : motivosSeleccionadosInput.split(",")) {
            try {
                indicesSeleccionados.add(Integer.parseInt(s.trim()) - 1);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida para los motivos.");
                System.out.println("Ingrese 'cancelar' para salir.");
                if (scanner.nextLine().equalsIgnoreCase("cancelar")) {
                    System.out.println("Ejecución del caso de uso cancelada por el usuario.");
                    scanner.close();
                    return;
                }
            }
        }

        List<MotivoFueraServicio> motivosFueraServicioCierre = new ArrayList<>();
        for (int indice : indicesSeleccionados) {
            if (indice >= 0 && indice < motivosDisponibles.size()) {
                MotivoTipo motivoTipo = motivosDisponibles.get(indice);
                System.out.print("Ingrese la observación para el motivo '" + motivoTipo.getDescripcion() + "' (o 'cancelar'): ");
                String observacionMotivo = scanner.nextLine();
                if (observacionMotivo.equalsIgnoreCase("cancelar")) {
                    System.out.println("Ejecución del caso de uso cancelada por el usuario.");
                    scanner.close();
                    return;
                }
                motivosFueraServicioCierre.add(new MotivoFueraServicio(observacionMotivo, motivoTipo));
            } else {
                System.out.println("Índice de motivo inválido: " + (indice + 1));
                System.out.println("Ingrese 'cancelar' para salir.");
                if (scanner.nextLine().equalsIgnoreCase("cancelar")) {
                    System.out.println("Ejecución del caso de uso cancelada por el usuario.");
                    scanner.close();
                    return;
                }
            }
        }

        // 8. El sistema solicita confirmación para cerrar la orden
        System.out.print("\n¿Confirmar el cierre de la Orden #" + numeroOrdenCierre + "? (S/N o 'cancelar'): ");
        String confirmacion = scanner.nextLine();

        if (confirmacion.equalsIgnoreCase("cancelar")) {
            System.out.println("Ejecución del caso de uso cancelada por el usuario.");
            scanner.close();
            return;
        }

        if (confirmacion.equalsIgnoreCase("S")) {
            // 9. RI confirma el cierre

            // 10, 11, 12. El sistema cierra la orden y actualiza el sismógrafo
            gestor.cerrarOrden(numeroOrdenCierre, observacionCierre, motivosFueraServicioCierre);
            System.out.println("Orden #" + numeroOrdenCierre + " cerrada exitosamente.");
            System.out.println("Notificación de cierre enviada.");

            // Mostrar resultado final de la orden cerrada
            OrdenDeInspeccion cerrada = repoOrdenes.buscarOrdenDeInspeccion(numeroOrdenCierre);
            System.out.println("\n--- Detalles de la Orden Cerrada ---");
            System.out.println("Número de Orden: " + cerrada.getNumOrden());
            System.out.println("Estado Final: " + cerrada.getEstado().getNombre());
            System.out.println("Observación de Cierre: " + cerrada.getObservacionCierre());
            System.out.println("Fecha y Hora de Cierre: " + cerrada.getFechaHoraCierre());
            System.out.println("Motivos de Cierre:");
            if (cerrada.getCambios() != null && !cerrada.getCambios().isEmpty()) {
                CambioEstado ultimoCambio = cerrada.getCambios().get(cerrada.getCambios().size() - 1);
                if (ultimoCambio.getMotivosSeleccionados() != null && !ultimoCambio.getMotivosSeleccionados().isEmpty()) {
                    for (MotivoFueraServicio motivo : ultimoCambio.getMotivosSeleccionados()) {
                        System.out.println("  - " + motivo.getMotivoTipo().getDescripcion() + " (Observación: " + motivo.getComentario() + ")");
                    }
                } else {
                    System.out.println("  No se registraron motivos.");
                }
            } else {
                System.out.println("  No se encontraron cambios de estado.");
            }

            // Mostrar datos del sismógrafo fuera de servicio
            EstacionSismologica estacionDeOrdenCerrada = cerrada.getNombreES();
            if (estacionDeOrdenCerrada != null) {
                Sismografo sismografoFueraDeServicio = estacionDeOrdenCerrada.getSismografo();
                if (sismografoFueraDeServicio != null) {
                    System.out.println("\n--- Datos del Sismógrafo Fuera de Servicio ---");
                    System.out.println("Identificador: " + sismografoFueraDeServicio.getIdentificadorSismografo());
                    System.out.println("Número de Serie: " + sismografoFueraDeServicio.getNroSerie());
                    System.out.println("Fecha de Adquisición: " + sismografoFueraDeServicio.getFechaAdquisicion());
                    // Puedes mostrar otros datos relevantes del sismógrafo aquí
                } else {
                    System.out.println("\nNo se pudo obtener la información del sismógrafo.");
                }
            } else {
                System.out.println("\nNo se pudo obtener la información de la estación sismológica.");
            }

        } else {
            System.out.println("Cierre de orden cancelado.");
        }

        scanner.close();

         */
    }

}

