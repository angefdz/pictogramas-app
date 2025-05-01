package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import model.Usuario;
import service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Usuario usuario) {
        if (!authService.registrarUsuario(usuario)) {
            return ResponseEntity.badRequest().body("El correo ya está registrado");
        }
        return ResponseEntity.ok("Usuario registrado correctamente");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Usuario loginData) {
        boolean autenticado = authService.autenticarUsuario(
                loginData.getEmail(),
                loginData.getContrasena() // esto es el plain text que escribe el usuario
        );

        if (!autenticado) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }

        return ResponseEntity.ok("Inicio de sesión correcto");
    }
}
