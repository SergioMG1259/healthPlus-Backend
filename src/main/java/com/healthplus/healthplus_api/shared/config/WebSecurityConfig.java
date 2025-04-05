package com.healthplus.healthplus_api.shared.config;

import com.healthplus.healthplus_api.auth.security.JWTConfigurer;
import com.healthplus.healthplus_api.auth.security.JWTFilter;
import com.healthplus.healthplus_api.auth.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Importante para anotaciones @PreAuthorize
public class WebSecurityConfig {

    private final TokenProvider tokenProvider;
    private final JWTFilter jwtFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // Proporcionar el AuthenticationManager que gestionará la autenticación basada en los detalles de usuario y contraseña
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //.cors(Customizer.withDefaults()) // Permite solicitudes cors desde otros dominios
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("https://localhost:4200")); // URL del frontend
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowCredentials(true); // Permitir cookies
                    config.addExposedHeader("Message");
                    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable) // CSRF no es necesaria en aplicaciones que utilizan tokens JWT para autenticación
                .authorizeHttpRequests(
                        auth -> auth
                                // Permite el acceso sin autenticación a las rutas de registro y autenticación.
                                .requestMatchers(antMatcher("/auth/login")).permitAll()
                                .requestMatchers(antMatcher("/auth/register/specialist")).permitAll()
                                //.requestMatchers(antMatcher("/auth/register/admin")).permitAll()
                                .requestMatchers(antMatcher("/auth/refresh")).permitAll()
                                // Cualquier otra solicitud requiere autenticación (JWT u otra autenticación configurada)
                                .anyRequest().authenticated()
                )
                // Permite la autenticación básica (por ejemplo, para testing con postman)
                // .httpBasic(Customizer.withDefaults())
                // Desactiva el formulario de inicio de sesión predeterminado, ya que se usa JWT
                .formLogin(AbstractHttpConfigurer::disable)
                // Configura le manejo de excepciones para autenticación. Usa JwtAuthenticationEntryPoint para manejar errores 401
                .exceptionHandling(e -> e.authenticationEntryPoint(authenticationEntryPoint))
                // Configura la política de sesiones como "sin estado" (stateless),
                // ya que JWT maneja la autenticación, no las sesiones de servidor
                .sessionManagement(h -> h.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Agrega la configuración para JWT en el filtro antes de los filtros predeterminados de Spring Security
                .with(new JWTConfigurer(tokenProvider), Customizer.withDefaults());

        // Añadir el JWTFilter antes del filtro de la autenticación de usuario/contraseña.
        // Esto permite que el JWTFilter valide el token antes de la autenticación
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
