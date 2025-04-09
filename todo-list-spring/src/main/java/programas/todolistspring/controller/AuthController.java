package programas.todolistspring.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import programas.todolistspring.dto.AuthResponse;
import programas.todolistspring.dto.LoginRequest;
import programas.todolistspring.dto.RegisterRequest;
import programas.todolistspring.service.AuthService;


@RestController
@RequestMapping("/api/auth")

public class AuthController {

    private final AuthService authenticationService;

    public AuthController(AuthService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // En JWT, el logout se maneja del lado del cliente eliminando el token
        return ResponseEntity.ok("Sesión cerrada exitosamente");
    }
}
