package dsi.ppai.repositories;

import dsi.ppai.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositorioUsuarios extends JpaRepository<Usuario, Long> {

    // El método findById(Long id) ya está incluido
    // por la herencia de JpaRepository<Usuario, Long>.

    /**
     * Busca un Usuario por su nombre de usuario (username).
     * Esto se añade como un método personalizado común en lógica de login.
     * @param nombreUsuario El nombre de usuario a buscar.
     * @return Un Optional que contiene el Usuario si se encuentra.
     */
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
}