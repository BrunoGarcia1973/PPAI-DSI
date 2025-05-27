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

        // Los repositorios (Estados, Motivos, Empleados, Sismografos)
        // ya precargan sus datos en sus propios constructores.
        // NO necesitamos llamar a ningún método 'guardar' aquí para ellos.
        // Solo necesitamos OBTENER los datos que ya están precargados.

        // --- OBTENCIÓN DE ESTADOS YA EXISTENTES ---
        // Asumo que tu RepositorioEstados tiene un método buscarEstado(String nombre)
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
        // Asumo que tu RepositorioMotivoTipo tiene un método buscarMotivoPorDescripcion(String descripcion)
        MotivoTipo motivoMantenimiento = repoMotivos.buscarMotivoPorDescripcion("Mantenimiento");
        MotivoTipo motivoCalibracion = repoMotivos.buscarMotivoPorDescripcion("Calibracion");
        MotivoTipo motivoFallaSensor = repoMotivos.buscarMotivoPorDescripcion("Falla de Sensor");

        if (motivoMantenimiento == null || motivoCalibracion == null || motivoFallaSensor == null) {
            System.err.println("ERROR: Algunos motivos de tipo no se encontraron en RepositorioMotivoTipo. Verifique los nombres y la precarga en su constructor.");
            throw new RuntimeException("Error fatal: No se pudieron cargar todos los motivos de tipo base.");
        }
        System.out.println("Motivos de Tipo de referencia obtenidos.");

        // Obtenemos los empleados que ya están precargados en RepositorioEmpleados
        Empleado empleadoRI_Juan = repoEmpleados.buscarEmpleadoPorLegajo("1001");
        Empleado empleadoRI_Laura = repoEmpleados.buscarEmpleadoPorLegajo("2002");
        Empleado empleadoRI_Carlos = repoEmpleados.buscarEmpleadoPorLegajo("3003");
        Empleado empleadoSistema = repoEmpleados.buscarEmpleadoPorLegajo("SYS");

        if (empleadoRI_Juan == null || empleadoRI_Laura == null || empleadoRI_Carlos == null || empleadoSistema == null) {
            System.err.println("ERROR: No se encontraron todos los empleados esperados en RepositorioEmpleados. Verifique la precarga en RepositorioEmpleados.");
            throw new RuntimeException("Error fatal: No se pudieron cargar todos los empleados base.");
        }
        System.out.println("Empleados de referencia obtenidos de RepositorioEmpleados.");

        // --- OBTENCIÓN DE SISMOGRAFOS YA EXISTENTES (o creación si tu RepoSismografos no los precarga) ---
        // NOTA IMPORTANTE: Si RepoSismografos NO precarga sismógrafos en su constructor,
        // ESTAS LÍNEAS ABAJO SON LAS QUE CREAN LOS SISMOGRAFOS.
        // Si tu RepoSismografos ya tiene sismógrafos en su constructor,
        // entonces estas líneas deberías cambiarlas a `repoSismografos.buscarSismografoPorId(...)`
        // Para este ejemplo, ASUMO que RepoSismografos NO precarga, así que los creo aquí y los inserto.
        Sismografo sismografoA = new Sismografo(32149, LocalDate.of(2022, 1, 15), 12345632);
        sismografoA.agregarCambioEstado(new CambioEstado(empleadoSistema, null, estadoAbierta, LocalDateTime.now().minusDays(30), null, null));
        repoSismografos.guardar(sismografoA); // <-- Esto asume que RepoSismografos TIENE un método 'guardar'.

        Sismografo sismografoB = new Sismografo(98765, LocalDate.of(2024, 10, 20), 78901234);
        sismografoB.agregarCambioEstado(new CambioEstado(empleadoSistema, null, estadoAbierta, LocalDateTime.now().minusDays(15), null, null));
        repoSismografos.guardar(sismografoB); // <-- Esto asume que RepoSismografos TIENE un método 'guardar'.

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
        estacionSismologicaA.setSismografo(sismografoC); // Sismógrafo C está en MANTENIMIENTO

        EstacionSismologica estacionSismologicaB = new EstacionSismologica();
        estacionSismologicaB.setEstacionId(789L);
        estacionSismologicaB.setDocumentoCertificacionAdq("Certificado-ES-B");
        estacionSismologicaB.setFechaSolicitudCertificacion(LocalDate.of(2024, 6, 1));
        estacionSismologicaB.setLatitud(-30.567);
        estacionSismologicaB.setLongitud(-65.123);
        estacionSismologicaB.setNroCertificacionAdquisicion(987654321L);
        estacionSismologicaB.setNombre("Estacion Alta Gracia");
        estacionSismologicaB.setSismografo(sismografoA); // Sismógrafo A está ABIERTO
        System.out.println("Estaciones Sismológicas inicializadas.");

        // --- INICIALIZACIÓN DE ÓRDENES DE INSPECCIÓN ---
        Long numeroOrdenCounter = 1001L;

        // --- ÓRDENES PARA JUAN (Empleado con legajo 1001) ---
        // Orden 1 para Juan (CompletamenteRealizada, ya cerrada)
        LocalDateTime fechaCierreJuan1 = LocalDateTime.now().minusDays(4).minusHours(2);
        OrdenDeInspeccion ordenJuan1 = new OrdenDeInspeccion(
                numeroOrdenCounter++,
                LocalDateTime.now().minusDays(5).minusHours(10),
                empleadoRI_Juan, // Asignada a empleadoRI_Juan
                fechaCierreJuan1, // Fecha de cierre ya establecida
                "Inspección rutinaria completa sin incidencias mayores.",
                null,
                estadoCompletamenteRealizada, // Estado final
                estacionSismologicaB,
                LocalDateTime.now().minusDays(5).minusHours(3) // Fecha de finalización
        );
        // Registrar el cambio de estado a COMPLETAMENTE_REALIZADA
        ordenJuan1.registrarCambioEstado(new CambioEstado(empleadoRI_Juan, estadoAbierta, estadoCompletamenteRealizada, fechaCierreJuan1.minusHours(1), fechaCierreJuan1, null));
        repoOrdenes.insertar(ordenJuan1); // Insertar la orden en el repositorio

        // Orden 2 para Juan (Abierta)
        OrdenDeInspeccion ordenJuan2 = new OrdenDeInspeccion(
                numeroOrdenCounter++,
                LocalDateTime.now().minusDays(2).minusHours(8),
                empleadoRI_Juan, // Asignada a empleadoRI_Juan
                null, // No cerrada aún
                null, // No tiene observación de cierre
                null,
                estadoAbierta, // Estado inicial
                estacionSismologicaA,
                LocalDateTime.now().minusDays(1).minusHours(15) // Fecha de finalización (diferente)
        );
        // Registrar el cambio de estado a ABIERTA
        ordenJuan2.registrarCambioEstado(new CambioEstado(empleadoRI_Juan, null, estadoAbierta, LocalDateTime.now().minusDays(2).minusHours(8), null, null));
        repoOrdenes.insertar(ordenJuan2); // Insertar la orden en el repositorio

        // Orden 3 para Juan (Completamente Realizada y lista para ser cerrada desde la UI)
        OrdenDeInspeccion ordenJuan3 = new OrdenDeInspeccion(
                numeroOrdenCounter++,
                LocalDateTime.now().minusDays(1).minusHours(3),
                empleadoRI_Juan, // Asignada a empleadoRI_Juan
                null, // No cerrada aún
                null, // No tiene observación de cierre
                null,
                estadoAbierta, // Inicialmente Abierta
                estacionSismologicaB,
                LocalDateTime.now().minusHours(2) // Fecha de finalización
        );
        // Primero, registrar el cambio de ABIERTA a COMPLETAMENTE_REALIZADA
        ordenJuan3.registrarCambioEstado(new CambioEstado(empleadoRI_Juan, estadoAbierta, estadoCompletamenteRealizada, LocalDateTime.now().minusHours(2).minusMinutes(5), LocalDateTime.now().minusHours(2), null));
        // Luego, actualizar el estado actual de la orden directamente (se hace en tu gestor, pero para inicializar así lo hago aquí)
        ordenJuan3.setEstado(estadoCompletamenteRealizada);
        ordenJuan3.setFechaHoraFinalizacion(LocalDateTime.now().minusHours(2)); // Establece fecha de finalización
        repoOrdenes.insertar(ordenJuan3); // Insertar la orden en el repositorio

        // --- NUEVA ORDEN PARA JUAN (Completamente Realizada con otra fecha de finalización) ---
        OrdenDeInspeccion ordenJuan4 = new OrdenDeInspeccion(
                numeroOrdenCounter++,
                LocalDateTime.now().minusDays(3).minusHours(7),
                empleadoRI_Juan, // Asignada a empleadoRI_Juan
                null, // No cerrada aún
                null, // No tiene observación de cierre
                null,
                estadoAbierta, // Inicialmente Abierta
                estacionSismologicaA,
                LocalDateTime.now().minusDays(2).minusHours(18) // Fecha de finalización (diferente a las otras)
        );
        // Registrar el cambio de ABIERTA a COMPLETAMENTE_REALIZADA
        ordenJuan4.registrarCambioEstado(new CambioEstado(empleadoRI_Juan, estadoAbierta, estadoCompletamenteRealizada, LocalDateTime.now().minusDays(2).minusHours(19), LocalDateTime.now().minusDays(2).minusHours(18), null));
        ordenJuan4.setEstado(estadoCompletamenteRealizada);
        ordenJuan4.setFechaHoraFinalizacion(LocalDateTime.now().minusDays(2).minusHours(18));
        repoOrdenes.insertar(ordenJuan4);


        // --- ÓRDENES PARA LAURA (Empleado con legajo 2002) ---
        // Orden 1 para Laura (CompletamenteRealizada)
        OrdenDeInspeccion ordenLaura1 = new OrdenDeInspeccion(
                numeroOrdenCounter++,
                LocalDateTime.now().minusDays(6).minusHours(5),
                empleadoRI_Laura, // Asignada a empleadoRI_Laura
                null,
                null,
                null,
                estadoCompletamenteRealizada,
                estacionSismologicaA,
                LocalDateTime.now().minusDays(5).minusHours(2) // Fecha de finalización
        );
        ordenLaura1.registrarCambioEstado(new CambioEstado(empleadoRI_Laura, estadoAbierta, estadoCompletamenteRealizada, LocalDateTime.now().minusDays(5).minusHours(3), LocalDateTime.now().minusDays(5).minusHours(2), null));
        repoOrdenes.insertar(ordenLaura1);


        // --- ÓRDENES PARA CARLOS (Empleado con legajo 3003) ---
        OrdenDeInspeccion ordenCarlos1 = new OrdenDeInspeccion(
                numeroOrdenCounter++,
                LocalDateTime.now().minusDays(7),
                empleadoRI_Carlos, // Asignada a empleadoRI_Carlos
                null,
                null,
                null,
                estadoCompletamenteRealizada, // Cambiado a COMPLETAMENTE_REALIZADA para que aparezca en el filtro
                estacionSismologicaA,
                LocalDateTime.now().minusDays(6).minusHours(10) // Fecha de finalización
        );
        ordenCarlos1.registrarCambioEstado(new CambioEstado(empleadoRI_Carlos, null, estadoAbierta, LocalDateTime.now().minusDays(7), null, null));
        // Aseguramos que el estado esté correctamente seteado si lo queremos COMPLETAMENTE_REALIZADA
        ordenCarlos1.setEstado(estadoCompletamenteRealizada);
        ordenCarlos1.setFechaHoraFinalizacion(LocalDateTime.now().minusDays(6).minusHours(10));
        repoOrdenes.insertar(ordenCarlos1);


        System.out.println("Órdenes de Inspección inicializadas.");
        System.out.println("Todos los datos de prueba han sido cargados exitosamente.");
    }
}