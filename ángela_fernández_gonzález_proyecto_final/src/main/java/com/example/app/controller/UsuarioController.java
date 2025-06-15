package com.example.app.controller;

import java.util.List;
import java.util.Optional; // Importa Optional para el delete y put

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.UsuarioSimple;
import com.example.app.model.Usuario;
// Ya no necesitamos UsuarioRepository aquí
// import com.example.app.repository.UsuarioRepository;
import com.example.app.service.UsuarioService; // Importa tu servicio

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

	
	 private String getCorreoAutenticado() {
	        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

	        if (principal instanceof Usuario usuario) {
	            return usuario.getEmail();
	        }

	        return principal.toString();
	    }
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

    @GetMapping("/yo")
    public ResponseEntity<UsuarioSimple> getUsuarioActual() {
        try {
        	
            String correo = getCorreoAutenticado(); 
            Long usuarioId = usuarioService.obtenerIdPorCorreo(correo);

            return usuarioService.obtenerPorId(usuarioId)
                .map(usuario -> {
                    UsuarioSimple dto = new UsuarioSimple();
                    dto.setId(usuario.getId());
                    dto.setCorreo(usuario.getEmail());
                    dto.setNombre(usuario.getNombre());
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
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
    
    @PutMapping("/yo")
    public ResponseEntity<UsuarioSimple> editarUsuarioActual(@RequestBody UsuarioSimple datos) {
        try {
            String correo = getCorreoAutenticado();
            Optional<Usuario> usuarioEditado = usuarioService.editarUsuarioPorCorreo(correo, datos);

            return usuarioEditado
                .map(usuario -> {
                    UsuarioSimple dto = new UsuarioSimple();
                    dto.setId(usuario.getId());
                    dto.setNombre(usuario.getNombre());
                    dto.setCorreo(usuario.getEmail());
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}