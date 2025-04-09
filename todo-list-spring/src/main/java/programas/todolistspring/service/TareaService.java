package programas.todolistspring.service;

import org.springframework.stereotype.Service;
import programas.todolistspring.models.Tarea;
import programas.todolistspring.models.Usuario;
import programas.todolistspring.repository.TareaRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
@Service
public class TareaService {
    private final TareaRepository tareaRepository;

    public TareaService(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    public List<Tarea> obtenerPorUsuario(Usuario usuario) {
        return tareaRepository.findByUsuario(usuario);
    }

    public Optional<Tarea> obtenerPorIdYUsuario(Long id, Usuario usuario) {
        return tareaRepository.findByIdAndUsuario(id, usuario);
    }

    public Tarea guardar(Tarea tarea) {
        return tareaRepository.save(tarea);
    }

    public Tarea actualizar(Long id, Tarea tareaActualizada, Usuario usuario) {
        return tareaRepository.findByIdAndUsuario(id, usuario)
                .map(tarea -> {
                    tarea.setTitulo(tareaActualizada.getTitulo());
                    tarea.setDescripcion(tareaActualizada.getDescripcion());
                    tarea.setCompletada(tareaActualizada.isCompletada());
                    return tareaRepository.save(tarea);
                })
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada o no pertenece al usuario"));
    }

    public Tarea actualizarParcial(Long id, Map<String, Object> campos, Usuario usuario) {
        return tareaRepository.findByIdAndUsuario(id, usuario)
                .map(tarea -> {
                    if (campos.containsKey("titulo")) {
                        tarea.setTitulo((String) campos.get("titulo"));
                    }
                    if (campos.containsKey("descripcion")) {
                        tarea.setDescripcion((String) campos.get("descripcion"));
                    }
                    if (campos.containsKey("completada")) {
                        tarea.setCompletada((Boolean) campos.get("completada"));
                    }
                    return tareaRepository.save(tarea);
                })
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada o no pertenece al usuario"));
    }

    public void eliminar(Long id, Usuario usuario) {
        Optional<Tarea> tarea = tareaRepository.findByIdAndUsuario(id, usuario);
        if (tarea.isPresent()) {
            tareaRepository.delete(tarea.get());
        } else {
            throw new RuntimeException("Tarea no encontrada o no pertenece al usuario");
        }
    }
}