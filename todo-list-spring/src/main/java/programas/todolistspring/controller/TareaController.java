package programas.todolistspring.controller;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import programas.todolistspring.dto.TareaDTO;
import programas.todolistspring.models.Tarea;
import programas.todolistspring.models.Usuario;
import programas.todolistspring.service.TareaService;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/tareas")
public class TareaController {
    private final TareaService tareaService;

    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    @GetMapping
    public ResponseEntity<List<TareaDTO>> obtenerTodas() {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        List<Tarea> tareas = tareaService.obtenerPorUsuario(usuarioAutenticado);
        List<TareaDTO> tareaDTOs = tareas.stream()
                .map(TareaDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tareaDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TareaDTO> obtenerPorId(@PathVariable Long id) {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        Optional<Tarea> tarea = tareaService.obtenerPorIdYUsuario(id, usuarioAutenticado);
        return tarea.map(t -> ResponseEntity.ok(new TareaDTO(t)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TareaDTO> crear(@RequestBody Tarea tarea) {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        tarea.setUsuario(usuarioAutenticado);
        Tarea nuevaTarea = tareaService.guardar(tarea);
        return ResponseEntity.ok(new TareaDTO(nuevaTarea));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TareaDTO> actualizar(@PathVariable Long id,@RequestBody Tarea tareaActualizada) {
        try {
            Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
            Tarea tarea = tareaService.actualizar(id, tareaActualizada, usuarioAutenticado);
            return ResponseEntity.ok(new TareaDTO(tarea));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TareaDTO> actualizarParcial(@PathVariable Long id, @RequestBody Map<String, Object> campos) {
        try {
            Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
            Tarea tarea = tareaService.actualizarParcial(id, campos, usuarioAutenticado);
            return ResponseEntity.ok(new TareaDTO(tarea));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();
        try {
            tareaService.eliminar(id, usuarioAutenticado);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) authentication.getPrincipal();
    }
}