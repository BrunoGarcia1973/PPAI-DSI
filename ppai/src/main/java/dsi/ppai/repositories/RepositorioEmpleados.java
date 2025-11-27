package dsi.ppai.repositories;

import dsi.ppai.entities.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioEmpleados extends JpaRepository<Empleado, Long> {
    
    Optional<Empleado> findByEmail(String email);
    
    // Método para buscar por legajo (que es el ID convertido a String)
    default Empleado buscarEmpleadoPorLegajo(String legajo) {
        try {
            Long id = Long.parseLong(legajo);
            return findById(id).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    // Método para buscar responsables de inspección
    @Query("SELECT DISTINCT e FROM Empleado e JOIN e.usuarios u JOIN u.roles r WHERE r.nombre = 'RESPONSABLE_DE_INSPECCION'")
    List<Empleado> findResponsablesDeInspeccion();
    
    default List<Empleado> buscarResponsablesDeInspeccion() {
        return findResponsablesDeInspeccion();
    }
}
