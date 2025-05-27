package dsi.ppai.repositories;

import dsi.ppai.entities.Empleado;
import dsi.ppai.entities.Rol;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class RepositorioEmpleados {

    private final Map<String, Empleado> empleados = new HashMap<>();

    public RepositorioEmpleados() {
        // Roles de ejemplo
        Rol responsableDeInspeccion = new Rol("RESPONSABLE_DE_INSPECCION", "Responsable de las inspecciones de sismógrafos.");
        Rol tecnico = new Rol("TECNICO", "Técnico encargado del mantenimiento.");
        Rol administrador = new Rol("ADMINISTRADOR", "Administrador del sistema.");
        Rol sysUser = new Rol("SISTEMA", "Usuario para operaciones del sistema.");

        // Empleados de prueba
        empleados.put("1001", new Empleado("1001", "Juan", "Pérez", "juan.perez@example.com", "3511112222", responsableDeInspeccion));
        empleados.put("2002", new Empleado("2002", "Laura", "Gómez", "laura.gomez@example.com", "3513334444", responsableDeInspeccion));
        empleados.put("3003", new Empleado("3003", "Carlos", "Rodríguez", "carlos.r@example.com", "3515556666", responsableDeInspeccion));
        empleados.put("4004", new Empleado("4004", "Ana", "Díaz", "ana.d@example.com", "3517778888", tecnico));
        empleados.put("SYS", new Empleado("SYS", "Sistema", "Automatizado", "sistema@example.com", "0000000000", sysUser));
    }

    public Empleado buscarEmpleadoPorLegajo(String legajo) {
        return empleados.get(legajo);
    }

    public List<Empleado> buscarResponsablesDeInspeccion() {
        return empleados.values().stream()
                .filter(Empleado::esResponsableDeInspeccion)
                .collect(Collectors.toList());
    }

    public List<Empleado> findAll() {
        return new ArrayList<>(empleados.values());
    }
}