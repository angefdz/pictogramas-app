package service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import model.Usuario;
import repository.UsuarioRepository;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean registrarUsuario(Usuario usuario) {
        // Comprobamos si el email ya está registrado
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return false;
        }

        // Codificamos la contraseña antes de guardarla
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        usuarioRepository.save(usuario);
        return true;
    }

    public boolean autenticarUsuario(String email, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        return usuarioOpt.isPresent()
                && passwordEncoder.matches(password, usuarioOpt.get().getContrasena());
    }
}
