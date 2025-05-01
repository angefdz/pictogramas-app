package service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import model.Usuario;
import repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario crearUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> cambiarContrasena(Long id, String nuevaContrasena) {
        return usuarioRepository.findById(id).map(u -> {
            u.setContrasena(nuevaContrasena);
            return usuarioRepository.save(u);
        });
    }

    public boolean eliminarUsuario(Long id) {
        return usuarioRepository.findById(id).map(u -> {
            usuarioRepository.delete(u);
            return true;
        }).orElse(false);
    }
}
