package com.example.app.controller;

import java.util.List;
import java.util.Optional; // Importa Optional para el delete y put

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.model.Usuario;
// Ya no necesitamos UsuarioRepository aquí
// import com.example.app.repository.UsuarioRepository;
import com.example.app.service.UsuarioService; // Importa tu servicio

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    // Inyección de dependencias para el UsuarioService
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping()
    public ResponseEntity<List<Usuario>> getUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodos(); // Usa el servicio
        if (usuarios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        // Usa el servicio
        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok) // Más conciso
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> changePassword(@PathVariable Long id,
                                                  @RequestBody String nuevaContrasena) {
        // Usa el servicio, que devuelve un Optional
        Optional<Usuario> updatedUser = usuarioService.cambiarContrasena(id, nuevaContrasena);
        return updatedUser
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUsuario(@PathVariable Long id) {
        // Usa el servicio, que devuelve un boolean
        boolean deleted = usuarioService.eliminarUsuario(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}