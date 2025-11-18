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

    // Método para buscar por el atributo 'numOrden' (el número visible)
    Optional<OrdenDeInspeccion> findByNumOrden(Long numOrden);

    // --- CONSULTAS PERSONALIZADAS ---

    /**
     * Método que busca una Orden por su número visible (numOrden) y carga todas las relaciones
     * (Estado, EstacionSismologica y su Sismografo) para evitar LazyInitializationException
     * durante el cierre de orden.
     */
    @Query("SELECT o FROM OrdenDeInspeccion o " +
            "JOIN FETCH o.empleado e " +
            "JOIN FETCH o.estado s " +
            "JOIN FETCH o.estacionSismologica es " +
            "JOIN FETCH es.sismografos si " + // <-- CORREGIDO: Usamos 'sismografos' (la lista OneToMany)
            "WHERE o.numOrden = :numeroOrden")
    Optional<OrdenDeInspeccion> buscarOrdenDeInspeccionPorNumOrdenConRelaciones(@Param("numeroOrden") Long numeroOrden);


    // Métodos de búsqueda para el listado inicial (filtrado por ID de Empleado y ID de Estado)
    @Query("SELECT o FROM OrdenDeInspeccion o " +
           "WHERE o.empleado.id = :empleadoId AND o.estado.id = :estadoId")
    List<OrdenDeInspeccion> findByEmpleado_IdAndEstado_Id(@Param("empleadoId") Long empleadoId, @Param("estadoId") Long estadoId);


    // --- MÉTODOS DE COMPATIBILIDAD (Ajustados) ---

    // Reescribimos este método para usar la nueva consulta con JOIN FETCH
    default OrdenDeInspeccion buscarOrdenDeInspeccion(Long numeroOrden) {
        // Usa la consulta que busca por numOrden y trae todas las relaciones
        return buscarOrdenDeInspeccionPorNumOrdenConRelaciones(numeroOrden).orElse(null);
    }

    // Método de compatibilidad - insertar es guardar
    default void insertar(OrdenDeInspeccion orden) {
        save(orden);
    }

    // Mantener findAll para compatibilidad (aunque generalmente es mejor usar findBy... con relaciones)
    @Query("SELECT o FROM OrdenDeInspeccion o LEFT JOIN FETCH o.empleado LEFT JOIN FETCH o.estado LEFT JOIN FETCH o.estacionSismologica")
    @Override
    List<OrdenDeInspeccion> findAll();

    // Mantener este método para compatibilidad
    @Query("SELECT o FROM OrdenDeInspeccion o LEFT JOIN FETCH o.empleado LEFT JOIN FETCH o.estado LEFT JOIN FETCH o.estacionSismologica WHERE o.empleado.id = :empleadoId")
    List<OrdenDeInspeccion> findByEmpleadoIdWithRelations(@Param("empleadoId") Long empleadoId);
}