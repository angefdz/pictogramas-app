package com.example.app.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <--- IMPORTE IMPORTANTE

import com.example.app.model.Configuracion;     // <--- IMPORTE IMPORTANTE
import com.example.app.model.TipoVoz;          // <--- IMPORTE IMPORTANTE
import com.example.app.model.Usuario;
import com.example.app.repository.ConfiguracionRepository; // <--- IMPORTE IMPORTANTE
import com.example.app.repository.UsuarioRepository;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ConfiguracionRepository configuracionRepository; 
    
    @Transactional 
    public boolean registrarUsuario(Usuario usuario) {
        if (usuarioRepository.buscarPorEmail(usuario.getEmail()).isPresent()) {
            return false; 
        }

        String contrasenaEncriptada = passwordEncoder.encode(usuario.getContrasena());
        usuario.setContrasena(contrasenaEncriptada);

        Usuario nuevoUsuario = usuarioRepository.save(usuario);

        Configuracion configuracionDefault = new Configuracion();
        configuracionDefault.setUsuario(nuevoUsuario); 
        configuracionDefault.setBotonesPorPantalla(9); 
        configuracionDefault.setTipoVoz(TipoVoz.femenina); 

        configuracionRepository.save(configuracionDefault);

        nuevoUsuario.setConfiguracion(configuracionDefault);

        return true; 
    }

    public boolean autenticarUsuario(String email, String contrasenaPlano) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorEmail(email);
        if (usuarioOpt.isEmpty()) return false;

        Usuario usuario = usuarioOpt.get();

        if (!"manual".equalsIgnoreCase(usuario.getMetodoAutenticacion())) {
            return false; 
        }

        return passwordEncoder.matches(contrasenaPlano, usuario.getContrasena());
    }

    @Transactional 
    public Usuario registrarUsuarioGoogle(Usuario usuario) {
        if (usuarioRepository.buscarPorEmail(usuario.getEmail()).isPresent()) {
            return null; 
        }

        usuario.setMetodoAutenticacion("google"); 

        Usuario nuevoUsuario = usuarioRepository.save(usuario);

        Configuracion configuracionDefault = new Configuracion();
        configuracionDefault.setUsuario(nuevoUsuario); 
        configuracionDefault.setBotonesPorPantalla(9);
        configuracionDefault.setMostrarPorCategoria(false);
        configuracionDefault.setTipoVoz(TipoVoz.femenina);

        configuracionRepository.save(configuracionDefault);

        nuevoUsuario.setConfiguracion(configuracionDefault);

        return nuevoUsuario;
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.buscarPorEmail(email);
    }
}