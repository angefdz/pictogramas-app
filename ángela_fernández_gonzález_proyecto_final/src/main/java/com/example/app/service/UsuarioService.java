package com.example.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Eliminadas: Configuracion, TipoVoz, ConfiguracionRepository,
// ya que 'crearUsuario' con la lógica de configuración no se usa aquí para el registro inicial.
import com.example.app.model.Usuario;
import com.example.app.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.buscarPorId(id);
    }

    @Transactional
    public Optional<Usuario> cambiarContrasena(Long id, String nuevaContrasena) {
        return usuarioRepository.buscarPorId(id).map(u -> {
            u.setContrasena(nuevaContrasena);
            return usuarioRepository.save(u);
        });
    }

    @Transactional
    public boolean eliminarUsuario(Long id) {
        return usuarioRepository.buscarPorId(id).map(u -> {
            usuarioRepository.delete(u);
            return true;
        }).orElse(false);
    }
    
    public Long obtenerIdPorCorreo(String correo) {
        return usuarioRepository.buscarPorEmail(correo.trim())
            .map(Usuario::getId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + correo));
    }


}