package dsi.ppai.services;

import dsi.ppai.entities.*; // Asegúrate de que todas tus entidades estén importadas
import dsi.ppai.repositories.*; // Asegúrate de que todos tus repositorios estén importados
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataInitializerService {

    private final RepositorioEstados repoEstados;
    private final RepositorioMotivoTipo repoMotivoTipo;
    private final RepositorioEmpleado repoEmpleado;
    private final RepositorioOrdenes repoOrdenes;
    private final RepositorioRol repoRol; // <--- ¡INJECTA EL REPOSITORIO DE ROLES!
    private final Sesion sesion;

    @PostConstruct
    public void init() {
        System.out.println("DEBUG: Inicializando datos de prueba en memoria...");

        // 1. Inicialización de Estados
        Estado estadoCompletamenteRealizada = new Estado("Completamente Realizada");
        Estado estadoEnCurso = new Estado("En Curso");
        Estado estadoPendiente = new Estado("Pendiente");
        Estado estadoCerrada = new Estado("CERRADA");
        Estado estadoFueraDeServicioSismografo = new Estado("FueraDeServicio");
        Estado estadoOperativoSismografo = new Estado("Operativo");

        repoEstados.guardar(estadoCompletamenteRealizada);
        repoEstados.guardar(estadoEnCurso);
        repoEstados.guardar(estadoPendiente);
        repoEstados.guardar(estadoCerrada);
        repoEstados.guardar(estadoFueraDeServicioSismografo);
        repoEstados.guardar(estadoOperativoSismografo);
        System.out.println("DEBUG: Estados inicializados.");

        // 2. Inicialización de Motivos de Fuera de Servicio (MotivoTipo)
        MotivoTipo mtCalibracion = new MotivoTipo(1, "Necesita Calibración", "Requiere ajuste de sensores.");
        MotivoTipo mtDanoSensor = new MotivoTipo(2, "Sensor Dañado", "Sensor principal averiado.");
        MotivoTipo mtMantenimiento = new MotivoTipo(3, "Mantenimiento Rutinario", "Mantenimiento programado.");

        repoMotivoTipo.guardar(mtCalibracion);
        repoMotivoTipo.guardar(mtDanoSensor);
        repoMotivoTipo.guardar(mtMantenimiento);
        System.out.println("DEBUG: Tipos de Motivo inicializados.");

        // 3. Inicialización de Roles <--- ¡NUEVA SECCIÓN!
        // Asegúrate de que tu constructor de Rol sea public Rol(String descripcion, String nombre)
        Rol rolResponsableInspeccion = new Rol("Encargado de inspecciones y cierre de órdenes", "Responsable de Inspeccion");
        Rol rolTecnicoMantenimiento = new Rol("Realiza tareas de mantenimiento en sismógrafos", "Técnico de Mantenimiento");

        repoRol.guardar(rolResponsableInspeccion); // <--- Guarda el rol
        repoRol.guardar(rolTecnicoMantenimiento); // <--- Guarda el rol
        System.out.println("DEBUG: Roles inicializados.");

        // 4. Inicialización de Empleados (Responsables de Inspección)
        // Ahora pasamos el objeto Rol, no un String
        Empleado empleadoRI_Juan = new Empleado("12345", "Perez", "juan.perez@example.com", "Juan", "123456789", rolResponsableInspeccion);
        Empleado empleadoRI_Laura = new Empleado("67890", "Gomez", "laura.gomez@example.com", "Laura", "987654321", rolResponsableInspeccion);

        repoEmpleado.guardar(empleadoRI_Juan);
        repoEmpleado.guardar(empleadoRI_Laura);
        System.out.println("DEBUG: Empleados inicializados.");

        // 5. Inicialización de Usuarios
        Usuario usuarioJuan = new Usuario("juanp", "pass123", empleadoRI_Juan);
        Usuario usuarioLaura = new Usuario("laurag", "pass456", empleadoRI_Laura);
        System.out.println("DEBUG: Usuarios inicializados.");

        // 6. Configuración de la Sesión con un usuario logueado para pruebas
        sesion.setUsuarioLogueado(usuarioJuan);
        System.out.println("DEBUG: Sesión inicializada con Usuario: " + usuarioJuan.getNombre() + " (Empleado: " + usuarioJuan.getEmpleado().getNombre() + ")");

        // 7. Inicialización de Sismógrafos y Estaciones Sismológicas
        Sismografo sismografo1 = new Sismografo(101, LocalDate.of(2020, 1, 15), 1001);
        sismografo1.getCambiosDeEstados().add(new CambioEstado(null, null, estadoOperativoSismografo, LocalDateTime.of(2020, 1, 15, 9, 0), null, new ArrayList<>()));
        EstacionSismologica estacionSismologicaA = new EstacionSismologica(1, "DOC001", LocalDate.of(2020, 2, 1), -31.4167, -64.1833, "Estacion Central", empleadoRI_Juan.getLegajo(), sismografo1);

        Sismografo sismografo2 = new Sismografo(102, LocalDate.of(2021, 3, 10), 1002);
        sismografo2.getCambiosDeEstados().add(new CambioEstado(null, null, estadoOperativoSismografo, LocalDateTime.of(2021, 3, 10, 10, 0), null, new ArrayList<>()));
        EstacionSismologica estacionSismologicaB = new EstacionSismologica(2, "DOC002", LocalDate.of(2021, 4, 5), -32.0, -65.0, "Estacion Sur", empleadoRI_Laura.getLegajo(), sismografo2);
        System.out.println("DEBUG: Sismógrafos y Estaciones inicializados.");

        // 8. Inicialización de Órdenes de Inspección
        long numeroOrden = 1000L;

        OrdenDeInspeccion orden1 = new OrdenDeInspeccion(
                numeroOrden++,
                LocalDateTime.now().minusDays(2).withHour(10).withMinute(0),
                empleadoRI_Juan,
                LocalDateTime.now().minusDays(1).withHour(15).withMinute(30),
                "Inspección completa Juan",
                LocalDateTime.now().minusDays(1).withHour(15).withMinute(0),
                estadoCompletamenteRealizada,
                estacionSismologicaA
        );
        repoOrdenes.insertar(orden1);

        OrdenDeInspeccion orden2 = new OrdenDeInspeccion(
                numeroOrden++,
                LocalDateTime.now().minusDays(5).withHour(9).withMinute(0),
                empleadoRI_Laura,
                LocalDateTime.now().minusDays(4).withHour(12).withMinute(45),
                "Todo OK Laura",
                LocalDateTime.now().minusDays(4).withHour(12).withMinute(0),
                estadoCompletamenteRealizada,
                estacionSismologicaB
        );
        repoOrdenes.insertar(orden2);

        OrdenDeInspeccion orden3 = new OrdenDeInspeccion(
                numeroOrden++,
                LocalDateTime.now().minusDays(1).withHour(14).withMinute(0),
                empleadoRI_Juan,
                LocalDateTime.now().withHour(11).withMinute(0),
                "Sin novedades Juan",
                LocalDateTime.now().withHour(10).withMinute(30),
                estadoCompletamenteRealizada,
                estacionSismologicaA
        );
        repoOrdenes.insertar(orden3);

        OrdenDeInspeccion orden4 = new OrdenDeInspeccion(
                numeroOrden++,
                LocalDateTime.now().minusDays(1).withHour(16).withMinute(0),
                empleadoRI_Juan,
                LocalDateTime.now().minusDays(1).withHour(18).withMinute(0),
                "Orden en curso",
                null,
                estadoEnCurso,
                estacionSismologicaB
        );
        repoOrdenes.insertar(orden4);

        System.out.println("DEBUG: Todas las órdenes de prueba inicializadas. Total órdenes: " + repoOrdenes.findAll().size());
        System.out.println("DEBUG: ¡Inicialización de datos de prueba completada!");
    }
}