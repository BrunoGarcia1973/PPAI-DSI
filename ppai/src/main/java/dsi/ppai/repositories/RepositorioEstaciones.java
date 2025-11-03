package dsi.ppai.repositories;

import dsi.ppai.entities.EstacionSismologica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositorioEstaciones extends JpaRepository<EstacionSismologica, Long> {
    
    Optional<EstacionSismologica> findByCodigoEstacion(String codigoEstacion);
}

