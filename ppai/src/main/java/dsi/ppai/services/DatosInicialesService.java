package dsi.ppai.services;

import dsi.ppai.entities.*;
import dsi.ppai.repositories.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatosInicialesService {

    private final RepositorioOrdenes repoOrdenes;
    private final RepositorioEstados repoEstados;
    private final RepositorioMotivoTipo repoMotivos;
    private final RepositorioEmpleados repoEmpleados;
    private final RepositorioSismografos repoSismografos;
    private final RepositorioEstaciones repoEstaciones;

    public DatosInicialesService(RepositorioOrdenes repoOrdenes,
                                 RepositorioEstados repoEstados,
                                 RepositorioMotivoTipo repoMotivos,
                                 RepositorioEmpleados repoEmpleados,
                                 RepositorioSismografos repoSismografos,
                                 RepositorioEstaciones repoEstaciones) {
        this.repoOrdenes = repoOrdenes;
        this.repoEstados = repoEstados;
        this.repoMotivos = repoMotivos;
        this.repoEmpleados = repoEmpleados;
        this.repoSismografos = repoSismografos;
        this.repoEstaciones = repoEstaciones;
    }

    @PostConstruct
    @org.springframework.transaction.annotation.Transactional
    public void inicializarDatosDePrueba() {
        try {
            System.out.println("Inicializando datos de prueba desde DatosInicialesService...");

        // --- CREACIÓN DE ESTADOS SI NO EXISTEN ---
        // NOTA: Los datos maestros (estados, roles, motivos) deberían cargarse desde el DDL SQL
        // Este código solo asegura que existan si no se cargaron desde el script
        Estado estadoCompletamenteRealizada = repoEstados.buscarEstado("COMPLETAMENTE_REALIZADA");
        if (estadoCompletamenteRealizada == null) {
            estadoCompletamenteRealizada = new Estado();
            estadoCompletamenteRealizada.setNombre("COMPLETAMENTE_REALIZADA");
            estadoCompletamenteRealizada.setAmbito(Estado.AmbitoEstado.ORDEN_INSPECCION);
            repoEstados.save(estadoCompletamenteRealizada);
        }
        
        Estado estadoFueraDeServicio = repoEstados.buscarEstado("FUERA_DE_SERVICIO");
        if (estadoFueraDeServicio == null) {
            estadoFueraDeServicio = new Estado();
            estadoFueraDeServicio.setNombre("FUERA_DE_SERVICIO");
            estadoFueraDeServicio.setAmbito(Estado.AmbitoEstado.SISMOGRAFO);
            repoEstados.save(estadoFueraDeServicio);
        }
        
        Estado estadoEnMantenimiento = repoEstados.buscarEstado("EN_MANTENIMIENTO");
        if (estadoEnMantenimiento == null) {
            estadoEnMantenimiento = new Estado();
            estadoEnMantenimiento.setNombre("EN_MANTENIMIENTO");
            estadoEnMantenimiento.setAmbito(Estado.AmbitoEstado.SISMOGRAFO);
            repoEstados.save(estadoEnMantenimiento);
        }
        
        Estado estadoAbierta = repoEstados.buscarEstado("ABIERTA");
        if (estadoAbierta == null) {
            estadoAbierta = new Estado();
            estadoAbierta.setNombre("ABIERTA");
            estadoAbierta.setAmbito(Estado.AmbitoEstado.ORDEN_INSPECCION);
            repoEstados.save(estadoAbierta);
        }
        
        Estado estadoCerrada = repoEstados.buscarEstado("CERRADA");
        if (estadoCerrada == null) {
            estadoCerrada = new Estado();
            estadoCerrada.setNombre("CERRADA");
            estadoCerrada.setAmbito(Estado.AmbitoEstado.ORDEN_INSPECCION);
            repoEstados.save(estadoCerrada);
        }
        System.out.println("Estados de referencia creados/obtenidos.");

        // --- CREACIÓN DE MOTIVOS DE TIPO SI NO EXISTEN ---
        MotivoTipo motivoMantenimiento = repoMotivos.buscarMotivoPorDescripcion("Mantenimiento");
        if (motivoMantenimiento == null) {
            motivoMantenimiento = new MotivoTipo("Mantenimiento");
            repoMotivos.save(motivoMantenimiento);
        }
        
        MotivoTipo motivoCalibracion = repoMotivos.buscarMotivoPorDescripcion("Calibracion");
        if (motivoCalibracion == null) {
            motivoCalibracion = new MotivoTipo("Calibracion");
            repoMotivos.save(motivoCalibracion);
        }
        
        MotivoTipo motivoFallaSensor = repoMotivos.buscarMotivoPorDescripcion("Falla de Sensor");
        if (motivoFallaSensor == null) {
            motivoFallaSensor = new MotivoTipo("Falla de Sensor");
            repoMotivos.save(motivoFallaSensor);
        }
        System.out.println("Motivos de Tipo de referencia creados/obtenidos.");

        // --- CREACIÓN DE EMPLEADOS SI NO EXISTEN ---
        Empleado empleadoRI_Juan = repoEmpleados.buscarEmpleadoPorLegajo("1001");
        if (empleadoRI_Juan == null) {
            empleadoRI_Juan = new Empleado("Juan", "Pérez", "juan.perez@example.com", "3511112222", null);
            empleadoRI_Juan = repoEmpleados.save(empleadoRI_Juan);
        }
        
        Empleado empleadoRI_Laura = repoEmpleados.buscarEmpleadoPorLegajo("2002");
        if (empleadoRI_Laura == null) {
            empleadoRI_Laura = new Empleado("Laura", "Gómez", "laura.gomez@example.com", "3513334444", null);
            empleadoRI_Laura = repoEmpleados.save(empleadoRI_Laura);
        }
        
        Empleado empleadoRI_Carlos = repoEmpleados.buscarEmpleadoPorLegajo("3003");
        if (empleadoRI_Carlos == null) {
            empleadoRI_Carlos = new Empleado("Carlos", "Rodríguez", "carlos.r@example.com", "3515556666", null);
            empleadoRI_Carlos = repoEmpleados.save(empleadoRI_Carlos);
        }
        
        Empleado empleadoSistema = repoEmpleados.buscarEmpleadoPorLegajo("SYS");
        if (empleadoSistema == null) {
            empleadoSistema = new Empleado("Sistema", "Automatizado", "sistema@example.com", "0000000000", null);
            empleadoSistema = repoEmpleados.save(empleadoSistema);
        }
        
        // Asegurar que empleadoRI_Juan tenga ID 1 o el legajo "1001" funcione correctamente
        // Si el ID generado no es 1, ajustar para que el legajo coincida
        if (!"1001".equals(empleadoRI_Juan.getLegajo())) {
            System.out.println("Advertencia: El empleado Juan no tiene legajo '1001'. ID actual: " + empleadoRI_Juan.getId());
        }
        System.out.println("Empleados de referencia creados/obtenidos.");

        // --- CREACIÓN DE ESTACIONES SISMOLOGICAS PRIMERO ---
        // Necesitamos crear las estaciones primero porque los sismógrafos tienen foreign key hacia ellas
        EstacionSismologica estacionSismologicaA = new EstacionSismologica();
        estacionSismologicaA.setCodigoEstacion("ES-424");
        estacionSismologicaA.setDocumentoCertificacionAdq("Certificado-ES-A");
        estacionSismologicaA.setFechaSolicitudCertificacion(LocalDate.of(2023, 3, 10));
        estacionSismologicaA.setLatitud(BigDecimal.valueOf(12.345));
        estacionSismologicaA.setLongitud(BigDecimal.valueOf(67.890));
        estacionSismologicaA.setNroCertificacionAdquisicion("123456789");
        estacionSismologicaA.setNombre("Estacion Villa Maria");
        estacionSismologicaA = repoEstaciones.save(estacionSismologicaA); // Guardar primero para obtener ID

        EstacionSismologica estacionSismologicaB = new EstacionSismologica();
        estacionSismologicaB.setCodigoEstacion("ES-789");
        estacionSismologicaB.setDocumentoCertificacionAdq("Certificado-ES-B");
        estacionSismologicaB.setFechaSolicitudCertificacion(LocalDate.of(2024, 6, 1));
        estacionSismologicaB.setLatitud(BigDecimal.valueOf(-30.567));
        estacionSismologicaB.setLongitud(BigDecimal.valueOf(-65.123));
        estacionSismologicaB.setNroCertificacionAdquisicion("987654321");
        estacionSismologicaB.setNombre("Estacion Alta Gracia");
        estacionSismologicaB = repoEstaciones.save(estacionSismologicaB); // Guardar primero para obtener ID
        System.out.println("Estaciones Sismológicas creadas.");

        // --- CREACIÓN DE SISMOGRAFOS CON SUS ESTACIONES ASIGNADAS ---
        Sismografo sismografoA = new Sismografo("32149", LocalDate.of(2022, 1, 15), "12345632");
        sismografoA.setEstacionSismologica(estacionSismologicaB); // Asignar estación antes de guardar
        sismografoA.agregarCambioEstado(new CambioEstado(empleadoSistema, null, estadoAbierta, OffsetDateTime.now().minusDays(30), null, null));
        repoSismografos.guardar(sismografoA);

        Sismografo sismografoB = new Sismografo("98765", LocalDate.of(2024, 10, 20), "78901234");
        sismografoB.setEstacionSismologica(estacionSismologicaB); // Asignar estación antes de guardar
        sismografoB.agregarCambioEstado(new CambioEstado(empleadoSistema, null, estadoAbierta, OffsetDateTime.now().minusDays(15), null, null));
        repoSismografos.guardar(sismografoB);

        Sismografo sismografoC = new Sismografo("11223", LocalDate.of(2023, 5, 10), "55667788");
        sismografoC.setEstacionSismologica(estacionSismologicaA); // Asignar estación antes de guardar
        sismografoC.agregarCambioEstado(new CambioEstado(empleadoSistema, null, estadoEnMantenimiento, OffsetDateTime.now().minusDays(5), null, null));
        repoSismografos.guardar(sismografoC);
        System.out.println("Sismógrafos inicializados y registrados.");

        // --- INICIALIZACIÓN DE ÓRDENES DE INSPECCIÓN ---
        Long numeroOrdenCounter = 1001L;

        // --- ÓRDENES PARA JUAN (Empleado con legajo 1001) ---
        OffsetDateTime fechaCierreJuan1 = OffsetDateTime.now().minusDays(4).minusHours(2);
        OrdenDeInspeccion ordenJuan1 = new OrdenDeInspeccion(
                String.valueOf(numeroOrdenCounter++),
                OffsetDateTime.now().minusDays(5).minusHours(10),
                empleadoRI_Juan,
                fechaCierreJuan1,
                "",
                null,
                estadoCompletamenteRealizada,
                estacionSismologicaB,
                OffsetDateTime.now().minusDays(5).minusHours(3)
        );

        ordenJuan1.registrarCambioEstado(new CambioEstado(empleadoRI_Juan, estadoAbierta, estadoCompletamenteRealizada, fechaCierreJuan1.minusHours(1), fechaCierreJuan1, null));
        repoOrdenes.insertar(ordenJuan1);

        OrdenDeInspeccion ordenJuan2 = new OrdenDeInspeccion(
                String.valueOf(numeroOrdenCounter++),
                OffsetDateTime.now().minusDays(2).minusHours(8),
                empleadoRI_Juan,
                null,
                null,
                null,
                estadoAbierta,
                estacionSismologicaA,
                OffsetDateTime.now().minusDays(1).minusHours(15)
        );

        ordenJuan2.registrarCambioEstado(new CambioEstado(empleadoRI_Juan, null, estadoAbierta, OffsetDateTime.now().minusDays(2).minusHours(8), null, null));
        repoOrdenes.insertar(ordenJuan2);

        OrdenDeInspeccion ordenJuan3 = new OrdenDeInspeccion(
                String.valueOf(numeroOrdenCounter++),
                OffsetDateTime.now().minusDays(1).minusHours(3),
                empleadoRI_Juan,
                null,
                null,
                null,
                estadoAbierta,
                estacionSismologicaB,
                OffsetDateTime.now().minusHours(2)
        );

        ordenJuan3.registrarCambioEstado(new CambioEstado(empleadoRI_Juan, estadoAbierta, estadoCompletamenteRealizada, OffsetDateTime.now().minusHours(2).minusMinutes(5), OffsetDateTime.now().minusHours(2), null));
        ordenJuan3.setEstado(estadoCompletamenteRealizada);
        ordenJuan3.setFechaHoraFinalizacion(OffsetDateTime.now().minusHours(2));
        repoOrdenes.insertar(ordenJuan3);

        OrdenDeInspeccion ordenJuan4 = new OrdenDeInspeccion(
                String.valueOf(numeroOrdenCounter++),
                OffsetDateTime.now().minusDays(3).minusHours(7),
                empleadoRI_Juan,
                null,
                null,
                null,
                estadoAbierta,
                estacionSismologicaA,
                OffsetDateTime.now().minusDays(2).minusHours(18)
        );

        ordenJuan4.registrarCambioEstado(new CambioEstado(empleadoRI_Juan, estadoAbierta, estadoCompletamenteRealizada, OffsetDateTime.now().minusDays(2).minusHours(19), OffsetDateTime.now().minusDays(2).minusHours(18), null));
        ordenJuan4.setEstado(estadoCompletamenteRealizada);
        ordenJuan4.setFechaHoraFinalizacion(OffsetDateTime.now().minusDays(2).minusHours(18));
        repoOrdenes.insertar(ordenJuan4);

        // --- ÓRDENES PARA LAURA (Empleado con legajo 2002) ---
        OrdenDeInspeccion ordenLaura1 = new OrdenDeInspeccion(
                String.valueOf(numeroOrdenCounter++),
                OffsetDateTime.now().minusDays(6).minusHours(5),
                empleadoRI_Laura,
                null,
                null,
                null,
                estadoCompletamenteRealizada,
                estacionSismologicaA,
                OffsetDateTime.now().minusDays(5).minusHours(2)
        );
        ordenLaura1.registrarCambioEstado(new CambioEstado(empleadoRI_Laura, estadoAbierta, estadoCompletamenteRealizada, OffsetDateTime.now().minusDays(5).minusHours(3), OffsetDateTime.now().minusDays(5).minusHours(2), null));
        repoOrdenes.insertar(ordenLaura1);


        // --- ÓRDENES PARA CARLOS (Empleado con legajo 3003) ---
        OrdenDeInspeccion ordenCarlos1 = new OrdenDeInspeccion(
                String.valueOf(numeroOrdenCounter++),
                OffsetDateTime.now().minusDays(7),
                empleadoRI_Carlos,
                null,
                null,
                null,
                estadoCompletamenteRealizada,
                estacionSismologicaA,
                OffsetDateTime.now().minusDays(6).minusHours(10)
        );
        ordenCarlos1.registrarCambioEstado(new CambioEstado(empleadoRI_Carlos, null, estadoAbierta, OffsetDateTime.now().minusDays(7), null, null));
        ordenCarlos1.setEstado(estadoCompletamenteRealizada);
        ordenCarlos1.setFechaHoraFinalizacion(OffsetDateTime.now().minusDays(6).minusHours(10));
        repoOrdenes.insertar(ordenCarlos1);

        System.out.println("Órdenes de Inspección inicializadas.");
        System.out.println("Todos los datos de prueba han sido cargados exitosamente.");
        } catch (Exception e) {
            System.err.println("Error al inicializar datos de prueba: " + e.getMessage());
            System.err.println("Asegúrate de que PostgreSQL esté corriendo y que la base de datos 'red_sismica' exista.");
            e.printStackTrace();
            // No lanzamos la excepción para permitir que la aplicación inicie
        }
    }
}