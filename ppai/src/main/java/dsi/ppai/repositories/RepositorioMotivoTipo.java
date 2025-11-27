package dsi.ppai.repositories;

import dsi.ppai.entities.MotivoTipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioMotivoTipo extends JpaRepository<MotivoTipo, Long> {
    
    Optional<MotivoTipo> findByDescripcion(String descripcion);
    
    // Métodos de compatibilidad con código existente
    default List<MotivoTipo> buscarTiposMotivosFueraDeServicios() {
        return findAll();
    }
    
    default MotivoTipo buscarMotivoPorDescripcion(String descripcion) {
        return findByDescripcion(descripcion).orElse(null);
    }
}
