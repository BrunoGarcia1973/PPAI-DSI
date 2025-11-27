package dsi.ppai.repositories;

import dsi.ppai.entities.Sismografo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioSismografos extends JpaRepository<Sismografo, Long> {
    
    Optional<Sismografo> findByIdentificadorSismografo(String identificadorSismografo);
    
    Optional<Sismografo> findByNroSerie(String nroSerie);
    
    List<Sismografo> findByEstacionSismologicaEstacionId(Long estacionId);
    
    default void guardar(Sismografo sismografo) {
        save(sismografo);
    }
    
    default Sismografo buscar(Long id) {
        return findById(id).orElse(null);
    }
    
    default List<Sismografo> buscarTodos() {
        return findAll();
    }
    
    default void eliminar(Long id) {
        deleteById(id);
    }
    
    default Sismografo buscarPorNroSerie(String nroSerie) {
        return findByNroSerie(nroSerie).orElse(null);
    }
}
