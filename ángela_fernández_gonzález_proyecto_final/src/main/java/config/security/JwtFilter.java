package config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import repository.UsuarioRepository;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Obtener el token del header Authorization
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // quitamos "Bearer "
            String email = jwtUtil.extractEmail(token);

            // 2. Verificamos que el token sea válido y que no haya ya una autenticación activa
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

                if (usuario != null && jwtUtil.isTokenValid(token, email)) {
                    // 3. Creamos una autenticación "falsa" para que Spring confíe
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(usuario, null, null);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 4. Establecemos la autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        // 5. Continuamos con el siguiente filtro de la cadena
        filterChain.doFilter(request, response);
    }
}
