package dsi.ppai.repositories;

import dsi.ppai.entities.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioEstados extends JpaRepository<Estado, Long> {
    
    Optional<Estado> findByNombre(String nombre);
    
    List<Estado> findByAmbito(Estado.AmbitoEstado ambito);
    
    // Método de compatibilidad con código existente
    default Estado buscarEstado(String nombre) {
        return findByNombre(nombre).orElse(null);
    }
}
