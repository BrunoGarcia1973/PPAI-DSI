package dsi.ppai.services;

import dsi.ppai.entities.*;
import dsi.ppai.repositories.*;
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

        // --- OBTENCIÓN DE ESTADOS YA EXISTENTES ---
        Estado estadoCompletamenteRealizada = repoEstados.buscarEstado("COMPLETAMENTE_REALIZADA");
        Estado estadoFueraDeServicio = repoEstados.buscarEstado("FUERA_DE_SERVICIO");
        Estado estadoEnMantenimiento = repoEstados.buscarEstado("EN_MANTENIMIENTO");
        Estado estadoAbierta = repoEstados.buscarEstado("ABIERTA");
        Estado estadoCerrada = repoEstados.buscarEstado("CERRADA");

        if (estadoCompletamenteRealizada == null || estadoFueraDeServicio == null || estadoEnMantenimiento == null || estadoAbierta == null || estadoCerrada == null) {
            System.err.println("ERROR: Algunos estados no se encontraron en RepositorioEstados. Verifique los nombres y la precarga en su constructor.");
            throw new RuntimeException("Error fatal: No se pudieron cargar todos los estados base.");
        }
        System.out.println("Estados de referencia obtenidos.");

        // --- OBTENCIÓN DE MOTIVOS DE TIPO YA EXISTENTES ---
        MotivoTipo motivoMantenimiento = repoMotivos.buscarMotivoPorDescripcion("Mantenimiento");
        MotivoTipo motivoCalibracion = repoMotivos.buscarMotivoPorDescripcion("Calibracion");
        MotivoTipo motivoFallaSensor = repoMotivos.buscarMotivoPorDescripcion("Falla de Sensor");

        if (motivoMantenimiento == null || motivoCalibracion == null || motivoFallaSensor == null) {
            System.err.println("ERROR: Algunos motivos de tipo no se encontraron en RepositorioMotivoTipo. Verifique los nombres y la precarga en su constructor.");
            throw new RuntimeException("Error fatal: No se pudieron cargar todos los motivos de tipo base.");
        }
        System.out.println("Motivos de Tipo de referencia obtenidos.");

        // --- OBTENCIÓN DE EMPLEADOS YA EXISTENTES ---
        Empleado empleadoRI_Juan = repoEmpleados.buscarEmpleadoPorLegajo("1001");
        Empleado empleadoRI_Laura = repoEmpleados.buscarEmpleadoPorLegajo("2002");
        Empleado empleadoRI_Carlos = repoEmpleados.buscarEmpleadoPorLegajo("3003");
        Empleado empleadoSistema = repoEmpleados.buscarEmpleadoPorLegajo("SYS");

        if (empleadoRI_Juan == null || empleadoRI_Laura == null || empleadoRI_Carlos == null || empleadoSistema == null) {
            System.err.println("ERROR: No se encontraron todos los empleados esperados en RepositorioEmpleados. Verifique la precarga en RepositorioEmpleados.");
            throw new RuntimeException("Error fatal: No se pudieron cargar todos los empleados base.");
        }
        System.out.println("Empleados de referencia obtenidos de RepositorioEmpleados.");

        // --- OBTENCIÓN DE SISMOGRAFOS YA EXISTENTES ---
        Sismografo sismografoA = new Sismografo(32149, LocalDate.of(2022, 1, 15), 12345632);
        sismografoA.agregarCambioEstado(new CambioEstado(empleadoSistema, null, estadoAbierta, LocalDateTime.now().minusDays(30), null, null));
        repoSismografos.guardar(sismografoA);

        Sismografo sismografoB = new Sismografo(98765, LocalDate.of(2024, 10, 20), 78901234);
        sismografoB.agregarCambioEstado(new CambioEstado(empleadoSistema, null, estadoAbierta, LocalDateTime.now().minusDays(15), null, null));
        repoSismografos.guardar(sismografoB); //

        Sismografo sismografoC = new Sismografo(11223, LocalDate.of(2023, 5, 10), 55667788);
        sismografoC.agregarCambioEstado(new CambioEstado(empleadoSistema, null, estadoEnMantenimiento, LocalDateTime.now().minusDays(5), null, null));
        repoSismografos.guardar(sismografoC); // <-- Esto asume que RepoSismografos TIENE un método 'guardar'.
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

        // --- INICIALIZACIÓN DE ÓRDENES DE INSPECCIÓN ---
        Long numeroOrdenCounter = 1001L;

        // --- ÓRDENES PARA JUAN (Empleado con legajo 1001) ---
        LocalDateTime fechaCierreJuan1 = LocalDateTime.now().minusDays(4).minusHours(2);
        OrdenDeInspeccion ordenJuan1 = new OrdenDeInspeccion(
                numeroOrdenCounter++,
                LocalDateTime.now().minusDays(5).minusHours(10),
                empleadoRI_Juan,
                fechaCierreJuan1,
                "",
                null,
                estadoCompletamenteRealizada,
                estacionSismologicaB,
                LocalDateTime.now().minusDays(5).minusHours(3)
        );

        ordenJuan1.registrarCambioEstado(new CambioEstado(empleadoRI_Juan, estadoAbierta, estadoCompletamenteRealizada, fechaCierreJuan1.minusHours(1), fechaCierreJuan1, null));
        repoOrdenes.insertar(ordenJuan1);

        OrdenDeInspeccion ordenJuan2 = new OrdenDeInspeccion(
                numeroOrdenCounter++,
                LocalDateTime.now().minusDays(2).minusHours(8),
                empleadoRI_Juan,
                null,
                null,
                null,
                estadoAbierta,
                estacionSismologicaA,
                LocalDateTime.now().minusDays(1).minusHours(15)
        );

        ordenJuan2.registrarCambioEstado(new CambioEstado(empleadoRI_Juan, null, estadoAbierta, LocalDateTime.now().minusDays(2).minusHours(8), null, null));
        repoOrdenes.insertar(ordenJuan2);

        OrdenDeInspeccion ordenJuan3 = new OrdenDeInspeccion(
                numeroOrdenCounter++,
                LocalDateTime.now().minusDays(1).minusHours(3),
                empleadoRI_Juan,
                null,
                null,
                null,
                estadoAbierta,
                estacionSismologicaB,
                LocalDateTime.now().minusHours(2)
        );

        ordenJuan3.registrarCambioEstado(new CambioEstado(empleadoRI_Juan, estadoAbierta, estadoCompletamenteRealizada, LocalDateTime.now().minusHours(2).minusMinutes(5), LocalDateTime.now().minusHours(2), null));
        ordenJuan3.setEstado(estadoCompletamenteRealizada);
        ordenJuan3.setFechaHoraFinalizacion(LocalDateTime.now().minusHours(2));
        repoOrdenes.insertar(ordenJuan3);

        OrdenDeInspeccion ordenJuan4 = new OrdenDeInspeccion(
                numeroOrdenCounter++,
                LocalDateTime.now().minusDays(3).minusHours(7),
                empleadoRI_Juan,
                null,
                null,
                null,
                estadoAbierta,
                estacionSismologicaA,
                LocalDateTime.now().minusDays(2).minusHours(18)
        );

        ordenJuan4.registrarCambioEstado(new CambioEstado(empleadoRI_Juan, estadoAbierta, estadoCompletamenteRealizada, LocalDateTime.now().minusDays(2).minusHours(19), LocalDateTime.now().minusDays(2).minusHours(18), null));
        ordenJuan4.setEstado(estadoCompletamenteRealizada);
        ordenJuan4.setFechaHoraFinalizacion(LocalDateTime.now().minusDays(2).minusHours(18));
        repoOrdenes.insertar(ordenJuan4);

        // --- ÓRDENES PARA LAURA (Empleado con legajo 2002) ---
        OrdenDeInspeccion ordenLaura1 = new OrdenDeInspeccion(
                numeroOrdenCounter++,
                LocalDateTime.now().minusDays(6).minusHours(5),
                empleadoRI_Laura,
                null,
                null,
                null,
                estadoCompletamenteRealizada,
                estacionSismologicaA,
                LocalDateTime.now().minusDays(5).minusHours(2)
        );
        ordenLaura1.registrarCambioEstado(new CambioEstado(empleadoRI_Laura, estadoAbierta, estadoCompletamenteRealizada, LocalDateTime.now().minusDays(5).minusHours(3), LocalDateTime.now().minusDays(5).minusHours(2), null));
        repoOrdenes.insertar(ordenLaura1);


        // --- ÓRDENES PARA CARLOS (Empleado con legajo 3003) ---
        OrdenDeInspeccion ordenCarlos1 = new OrdenDeInspeccion(
                numeroOrdenCounter++,
                LocalDateTime.now().minusDays(7),
                empleadoRI_Carlos,
                null,
                null,
                null,
                estadoCompletamenteRealizada,
                estacionSismologicaA,
                LocalDateTime.now().minusDays(6).minusHours(10)
        );
        ordenCarlos1.registrarCambioEstado(new CambioEstado(empleadoRI_Carlos, null, estadoAbierta, LocalDateTime.now().minusDays(7), null, null));
        ordenCarlos1.setEstado(estadoCompletamenteRealizada);
        ordenCarlos1.setFechaHoraFinalizacion(LocalDateTime.now().minusDays(6).minusHours(10));
        repoOrdenes.insertar(ordenCarlos1);

        System.out.println("Órdenes de Inspección inicializadas.");
        System.out.println("Todos los datos de prueba han sido cargados exitosamente.");
    }
}