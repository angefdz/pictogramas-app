package com.example.app.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.app.config.security.JwtUtil;
import com.example.app.model.Usuario;
import com.example.app.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    // Registro manual
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Usuario usuario) {
        usuario.setMetodoAutenticacion("manual");
        if (!authService.registrarUsuario(usuario)) {
            return ResponseEntity.badRequest().body("El correo ya está registrado");
        }
        return ResponseEntity.ok("Usuario registrado correctamente");
    }

    // Login manual
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Usuario loginData) {
        boolean autenticado = authService.autenticarUsuario(
                loginData.getEmail(),
                loginData.getContrasena()
        );

        if (!autenticado) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }

        String token = jwtUtil.generateToken(loginData.getEmail());

        // Buscar el usuario
        Optional<Usuario> usuarioOpt = authService.buscarPorEmail(loginData.getEmail());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
        }

        Usuario usuario = usuarioOpt.get();

        return ResponseEntity.ok(Map.of(
                "token", token,
                "usuarioId", usuario.getId()
        ));
    }

    // Login/registro con Google
    @PostMapping("/google")
    public ResponseEntity<Map<String, Object>> loginGoogle(@RequestBody Usuario usuarioGoogle) {
        usuarioGoogle.setMetodoAutenticacion("google");

        Optional<Usuario> usuarioOpt = authService.buscarPorEmail(usuarioGoogle.getEmail());

        Usuario usuario;

        if (usuarioOpt.isEmpty()) {
            usuario = authService.registrarUsuarioGoogle(usuarioGoogle);
        } else {
            usuario = usuarioOpt.get();
        }

        String token = jwtUtil.generateToken(usuario.getEmail());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "usuarioId", usuario.getId()
        ));
    }
}
