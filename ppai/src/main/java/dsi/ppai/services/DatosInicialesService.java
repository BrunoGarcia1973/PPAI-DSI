package dsi.ppai.services;

import dsi.ppai.entities.*;
import dsi.ppai.repositories.RepositorioEstados;
import dsi.ppai.repositories.RepositorioMotivoTipo;
import dsi.ppai.repositories.RepositorioOrdenes;
import dsi.ppai.repositories.RepositorioEmpleados;
import dsi.ppai.repositories.RepositorioSismografos;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatosInicialesService {

    private final RepositorioOrdenes repoOrdenes;
    private final RepositorioEstados repoEstados;
    private final RepositorioMotivoTipo repoMotivos;
    private final RepositorioEmpleados repoEmpleados;
    private final RepositorioSismografos repoSismografos;

    public DatosInicialesService(RepositorioOrdenes repoOrdenes,
                                 RepositorioEstados repoEstados,
                                 RepositorioMotivoTipo repoMotivos,
                                 RepositorioEmpleados repoEmpleados,
                                 RepositorioSismografos repoSismografos) {
        this.repoOrdenes = repoOrdenes;
        this.repoEstados = repoEstados;
        this.repoMotivos = repoMotivos;
        this.repoEmpleados = repoEmpleados;
        this.repoSismografos = repoSismografos;
    }

    @PostConstruct
    public void inicializarDatosDePrueba() {
        System.out.println("Inicializando datos de prueba desde DatosInicialesService...");

        Estado estadoCompletamenteRealizada = repoEstados.buscarEstado("COMPLETAMENTE_REALIZADA");
        Estado estadoFueraDeServicio = repoEstados.buscarEstado("FUERA DE SERVICIO");
        Estado estadoEnMantenimiento = repoEstados.buscarEstado("EN_MANTENIMIENTO");
        Estado estadoAbierta = repoEstados.buscarEstado("ABIERTA");
        Estado estadoCerrada = repoEstados.buscarEstado("CERRADA");

        if (estadoCompletamenteRealizada == null || estadoFueraDeServicio == null || estadoEnMantenimiento == null || estadoAbierta == null || estadoCerrada == null) {
            System.err.println("ERROR: Algunos estados no se encontraron en RepositorioEstados. Verifique los nombres y la precarga.");
            throw new RuntimeException("Error fatal: No se pudieron cargar todos los estados base.");
        }
        System.out.println("Estados de referencia obtenidos.");

        MotivoTipo motivoMantenimiento = repoMotivos.buscarMotivoPorDescripcion("Mantenimiento");
        MotivoTipo motivoCalibracion = repoMotivos.buscarMotivoPorDescripcion("Calibracion");
        MotivoTipo motivoFallaSensor = repoMotivos.buscarMotivoPorDescripcion("Falla de Sensor");

        if (motivoMantenimiento == null || motivoCalibracion == null || motivoFallaSensor == null) {
            System.err.println("ERROR: Algunos motivos de tipo no se encontraron en RepositorioMotivoTipo. Verifique los nombres y la precarga.");
            throw new RuntimeException("Error fatal: No se pudieron cargar todos los motivos de tipo base.");
        }
        System.out.println("Motivos de Tipo de referencia obtenidos.");

        // Crear un empleado de sistema genérico si no hay RIs para la inicialización de sismógrafos
        Empleado empleadoSistema = repoEmpleados.buscarResponsablesDeInspeccion().stream()
                .findFirst()
                .orElse(null);

        if (empleadoSistema == null) {
            System.out.println("No se encontraron RIs existentes. Creando un empleado de sistema para la inicialización...");
            empleadoSistema = new Empleado("SYS", "Sistema", "sys@example.com", "Sistema", "000000000", new Rol("SISTEMA", "Usuario del sistema"));
        }
        System.out.println("Empleado para inicialización: " + empleadoSistema.getNombre());


        // --- INICIALIZACIÓN DE SISMOGRAFOS ---
        Sismografo sismografoA = new Sismografo(32149L, LocalDate.of(2022, 1, 15), 12345632);
        sismografoA.cambiarEstado(empleadoSistema, estadoAbierta);
        repoSismografos.insertar(sismografoA);
        System.out.println("Sismógrafo A estado inicial: " + sismografoA.getEstadoActual().getNombre());

        Sismografo sismografoB = new Sismografo(98765L, LocalDate.of(2024, 10, 20), 78901234);
        sismografoB.cambiarEstado(empleadoSistema, estadoAbierta);
        repoSismografos.insertar(sismografoB);
        System.out.println("Sismógrafo B estado inicial: " + sismografoB.getEstadoActual().getNombre());

        Sismografo sismografoC = new Sismografo(11223L, LocalDate.of(2023, 5, 10), 55667788);
        sismografoC.cambiarEstado(empleadoSistema, estadoEnMantenimiento);
        repoSismografos.insertar(sismografoC);
        System.out.println("Sismógrafo C estado inicial: " + sismografoC.getEstadoActual().getNombre());
        System.out.println("Sismógrafos inicializados y registrados en RepositorioSismografos.");


        // --- INICIALIZACIÓN DE ESTACIONES SISMOLOGICAS ---
        EstacionSismologica estacionSismologicaA = new EstacionSismologica();
        estacionSismologicaA.setEstacionId(424L);
        estacionSismologicaA.setDocumentoCertificacionAdq("Certificado-ES-A");
        estacionSismologicaA.setFechaSolicitudCertificacion(LocalDate.of(2023, 3, 10));
        estacionSismologicaA.setLatitud(12.345);
        estacionSismologicaA.setLongitud(67.890);
        estacionSismologicaA.setNroCertificacionAdquisicion(123456789L);
        estacionSismologicaA.setNombre("Estacion Villa Maria");
        estacionSismologicaA.setSismografo(sismografoC);

        EstacionSismologica estacionSismologicaB = new EstacionSismologica();
        estacionSismologicaB.setEstacionId(789L);
        estacionSismologicaB.setDocumentoCertificacionAdq("Certificado-ES-B");
        estacionSismologicaB.setFechaSolicitudCertificacion(LocalDate.of(2024, 6, 1));
        estacionSismologicaB.setLatitud(-30.567);
        estacionSismologicaB.setLongitud(-65.123);
        estacionSismologicaB.setNroCertificacionAdquisicion(987654321L);
        estacionSismologicaB.setNombre("Estacion Alta Gracia");
        estacionSismologicaB.setSismografo(sismografoA);
        System.out.println("Estaciones Sismológicas inicializadas.");

        // OBTENER EMPLEADOS YA PRECARGADOS DESDE REPOSITORIO
        Empleado empleadoRI_Juan = repoEmpleados.buscarEmpleadoPorLegajo("1001");
        Empleado empleadoRI_Laura = repoEmpleados.buscarEmpleadoPorLegajo("2002");
        Empleado empleadoRI_Carlos = repoEmpleados.buscarEmpleadoPorLegajo("3003");

        if (empleadoRI_Juan == null || empleadoRI_Laura == null || empleadoRI_Carlos == null) {
            System.err.println("ERROR: Algunos empleados Responsables de Inspección no se encontraron en RepositorioEmpleados. Esto podría afectar la creación de órdenes.");
            throw new RuntimeException("Error fatal: No se pudieron cargar todos los empleados responsables de inspección.");
        }
        System.out.println("Empleados de referencia obtenidos.");


        // --- INICIALIZACIÓN DE ÓRDENES DE INSPECCIÓN ---
        Long numeroOrdenCounter = 1001L;

        // Fecha y hora actual menos 10 horas para la fechaHoraFinalizacion como tú pediste
        LocalDateTime fechaFinalizacionPorDefecto = LocalDateTime.now().minusHours(10);


        // ***** ASIGNACIÓN DE ÓRDENES SEGÚN REQUERIMIENTO *****

        // --- ÓRDENES PARA JUAN (3 órdenes) ---
        // Orden 1 para Juan (Cerrada)
        LocalDateTime fechaCierreJuan1 = LocalDateTime.now().minusDays(4).minusHours(2);
        OrdenDeInspeccion ordenJuan1 = new OrdenDeInspeccion(
                numeroOrdenCounter++,
                LocalDateTime.now().minusDays(5).minusHours(10),
                empleadoRI_Juan,
                fechaCierreJuan1, // Fecha de Cierre
                "Inspección rutinaria completa sin incidencias mayores.",
                null,
                estadoCompletamenteRealizada,
                estacionSismologicaB,
                fechaFinalizacionPorDefecto // <-- ¡Aquí está la fechaHoraFinalizacion como pediste!
        );
        ordenJuan1.agregarCambioEstado(new CambioEstado(empleadoRI_Juan, null, estadoCompletamenteRealizada, fechaCierreJuan1, null, null));
        repoOrdenes.insertar(ordenJuan1);

        // Orden 2 para Juan (Abierta)
        OrdenDeInspeccion ordenJuan2 = new OrdenDeInspeccion(
                numeroOrdenCounter++,
                LocalDateTime.now().minusDays(2).minusHours(8),
                empleadoRI_Juan,
                null, // Es nulo porque la orden está abierta
                null,
                null,
                estadoAbierta,
                estacionSismologicaA,
                fechaFinalizacionPorDefecto // <-- ¡Aquí está la fechaHoraFinalizacion como pediste!
        );
        ordenJuan2.agregarCambioEstado(new CambioEstado(empleadoRI_Juan, null, estadoAbierta, LocalDateTime.now().minusDays(2).minusHours(8), null, null));
        repoOrdenes.insertar(ordenJuan2);

        // Orden 3 para Juan (Abierta)
        OrdenDeInspeccion ordenJuan3 = new OrdenDeInspeccion(
                numeroOrdenCounter++,
                LocalDateTime.now().minusDays(1).minusHours(3),
                empleadoRI_Juan,
                null, // Es nulo porque la orden está abierta
                null,
                null,
                estadoAbierta,
                estacionSismologicaB,
                fechaFinalizacionPorDefecto // <-- ¡Aquí está la fechaHoraFinalizacion como pediste!
        );
        ordenJuan3.agregarCambioEstado(new CambioEstado(empleadoRI_Juan, null, estadoAbierta, LocalDateTime.now().minusDays(1).minusHours(3), null, null));
        repoOrdenes.insertar(ordenJuan3);


        // --- ÓRDENES PARA LAURA (0 órdenes) ---
        // No se insertan órdenes para Laura aquí.


        // --- ÓRDENES PARA CARLOS (1 orden) ---
        // Orden 1 para Carlos (Abierta)
        MotivoFueraServicio mfsOrdenCarlos1 = new MotivoFueraServicio("Sensor de temperatura dañado", motivoFallaSensor);
        List<MotivoFueraServicio> motivosOrdenCarlos1 = new ArrayList<>();
        motivosOrdenCarlos1.add(mfsOrdenCarlos1);

        OrdenDeInspeccion ordenCarlos1 = new OrdenDeInspeccion(
                numeroOrdenCounter++,
                LocalDateTime.now().minusDays(7),
                empleadoRI_Carlos,
                null, // Se mantiene abierta por ahora, no se cierra en la inicialización
                null,
                null,
                estadoAbierta,
                estacionSismologicaA,
                fechaFinalizacionPorDefecto // <-- ¡Aquí está la fechaHoraFinalizacion como pediste!
        );
        ordenCarlos1.agregarCambioEstado(new CambioEstado(empleadoRI_Carlos, null, estadoAbierta, LocalDateTime.now().minusDays(7), null, null));
        repoOrdenes.insertar(ordenCarlos1);


        System.out.println("Órdenes de Inspección inicializadas.");
        System.out.println("Todos los datos de prueba han sido cargados exitosamente.");
    }
}