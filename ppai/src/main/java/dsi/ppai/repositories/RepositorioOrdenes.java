package dsi.ppai.repositories;

import dsi.ppai.entities.OrdenDeInspeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioOrdenes extends JpaRepository<OrdenDeInspeccion, Long> {
    
    Optional<OrdenDeInspeccion> findByNumeroOrden(String numeroOrden);
    
    @Query("SELECT o FROM OrdenDeInspeccion o " +
           "LEFT JOIN FETCH o.empleado " +
           "LEFT JOIN FETCH o.estado " +
           "LEFT JOIN FETCH o.estacionSismologica " +
           "WHERE o.empleado.id = :empleadoId")
    List<OrdenDeInspeccion> findByEmpleadoIdWithRelations(@Param("empleadoId") Long empleadoId);
    
    @Query("SELECT o FROM OrdenDeInspeccion o " +
           "LEFT JOIN FETCH o.empleado " +
           "LEFT JOIN FETCH o.estado " +
           "LEFT JOIN FETCH o.estacionSismologica")
    @Override
    List<OrdenDeInspeccion> findAll();
    
    // Método para buscar por legajo del empleado (compatibilidad con código existente)
    default List<OrdenDeInspeccion> buscarOrdenesInspeccionDeRI(String legajoRI) {
        try {
            Long empleadoId = Long.parseLong(legajoRI);
            return findByEmpleadoIdWithRelations(empleadoId);
        } catch (NumberFormatException e) {
            return List.of();
        }
    }
    
    // Método de compatibilidad con código existente
    default OrdenDeInspeccion buscarOrdenDeInspeccion(Long numeroOrden) {
        return findById(numeroOrden).orElse(null);
    }
    
    // Método de compatibilidad - insertar es guardar
    default void insertar(OrdenDeInspeccion orden) {
        save(orden);
    }
}
