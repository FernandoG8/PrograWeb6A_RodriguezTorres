package programas.todolistspring.repository;

import programas.todolistspring.models.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import programas.todolistspring.models.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {
    List<Tarea> findByUsuario(Usuario usuario);
    Optional<Tarea> findByIdAndUsuario(Long id, Usuario usuario);
}
