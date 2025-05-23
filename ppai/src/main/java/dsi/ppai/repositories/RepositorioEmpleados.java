package dsi.ppai.repositories;

import dsi.ppai.entities.Empleado;
import dsi.ppai.entities.Rol;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RepositorioEmpleados {

    private final Map<String, Empleado> empleados = new HashMap<>(); // Usa el legajo como clave

    @PostConstruct
    public void init() {
        System.out.println("Inicializando RepositorioEmpleados...");

        // Precargar Roles primero (si no tienen un repositorio propio y se necesitan aquí)
        Rol rolRI = new Rol("RESPONSABLE_DE_INSPECCION", "Empleado responsable de realizar inspecciones.");
        Rol rolSoporte = new Rol("SOPORTE_TECNICO", "Empleado de soporte técnico.");
        Rol rolSistema = new Rol("SISTEMA", "Usuario del sistema para operaciones automáticas.");


        // Precargar Empleados (incluyendo Responsables de Inspección)
        Empleado empleado1 = new Empleado("1001", "Juan", "Perez", "juan.perez@example.com", "1122334455", rolRI);
        Empleado empleado2 = new Empleado("2002", "Laura", "Gomez", "laura.gomez@example.com", "2233445566", rolRI);
        Empleado empleado3 = new Empleado("3003", "Carlos", "Diaz", "carlos.diaz@example.com", "3344556677", rolRI);
        Empleado empleado4 = new Empleado("4001", "Ana", "Ruiz", "ana.ruiz@example.com", "4455667788", rolSoporte);
        Empleado empleado5 = new Empleado("5001", "Pedro", "Lopez", "pedro.lopez@example.com", "5566778899", rolSoporte);
        Empleado empleadoSistema = new Empleado("SYS", "Sistema", "sys@example.com", "Sistema", "000000000", rolSistema);


        empleados.put(empleado1.getLegajo(), empleado1);
        empleados.put(empleado2.getLegajo(), empleado2);
        empleados.put(empleado3.getLegajo(), empleado3);
        empleados.put(empleado4.getLegajo(), empleado4);
        empleados.put(empleado5.getLegajo(), empleado5);
        empleados.put(empleadoSistema.getLegajo(), empleadoSistema);


        System.out.println("Empleados precargados: " + empleados.keySet());
    }

    public Empleado buscarEmpleadoPorLegajo(String legajo) {
        return empleados.get(legajo);
    }

    public List<Empleado> buscarTodosLosEmpleados() {
        return new ArrayList<>(empleados.values());
    }

    public List<Empleado> buscarResponsablesDeInspeccion() {
        // Asumiendo que el Rol del Responsable de Inspección tiene el nombre "RESPONSABLE_DE_INSPECCION"
        return empleados.values().stream()
                .filter(empleado -> empleado.getRol() != null &&
                        "RESPONSABLE_DE_INSPECCION".equals(empleado.getRol().getNombre()))
                .collect(Collectors.toList());
    }

    // Método para insertar un empleado (útil si se necesitan agregar dinámicamente)
    public void insertar(Empleado empleado) {
        empleados.put(empleado.getLegajo(), empleado);
    }
}