package config;

import config.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SeguridadConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // codifica contraseñas con hash seguro
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // desactiva CSRF porque usas JWT y no formularios
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll() // login y register sin token
                .anyRequest().authenticated() // todo lo demás requiere token válido
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // activa tu filtro JWT

        return http.build();
    }
}
