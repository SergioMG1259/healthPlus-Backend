package com.healthplus.healthplus_api.auth.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
public class TokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.validity-in-seconds}")
    private long jwtValidityInSeconds;

    @Value("${jwt.refresh-token-validity-in-seconds-default}")
    private long jwtRefreshTokenValidityInSecondsDefault;

    //@Value("${jwt.refresh-token-validity-in-seconds-remember}")
    //private long jwtRefreshTokenValidityInSecondsRemember;

    private Key key;

    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        // Generar la clave para firmar el JWT a partir del secreto configurado
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));

        // Inicializar el parser JWT con la clave generada para firmar y validar los tokens
        jwtParser = Jwts
                        .parserBuilder()
                        .setSigningKey(key)
                        .build();
    }

    // Método para crear el token JWT con los detalles del usuario autenticado
    public String createAccessToken(Authentication authentication) {

        //String email = authentication.getName();

        String role = authentication.getAuthorities()
                                        .stream()
                                        .findFirst()
                                        .orElseThrow() // TODO: implementar la excepción RoleNotFoundException
                                        .getAuthority();

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        //Long userId = userPrincipal.getId(); // ID del usuario
        Long userByRolId = userPrincipal.getUserByRoleId(); // ID relacionado al rol (specialistId o adminId)
        Long userId = userPrincipal.getUserId();
        return Jwts
                .builder()
                .setSubject(userByRolId.toString()) // El sujeto del token es el ID del perfil o rol del usuario (specialistId o adminId)
                .claim("role", role) // El rol se incluye como claim en el token
                .claim("userId", userId) // El id del usuario de incluye como claim en el token
                //.claim("userByRolId", userByRolId)
                .signWith(key, SignatureAlgorithm.HS512) // Firmar el token con el algoritmo HS512 y la clave
                .setExpiration(new Date(System.currentTimeMillis() + jwtValidityInSeconds * 1000)) // Configurar la fecha de expiración
                .compact();
    }

    // Método para crear el refresh token
    public String createRefreshToken(Authentication authentication) {

        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .orElseThrow() // TODO: implementar la excepción RoleNotFoundException
                .getAuthority();

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Long userByRolId = userPrincipal.getUserByRoleId(); // ID relacionado al rol (specialistId o adminId)
        Long userId = userPrincipal.getUserId(); // ID del usuario, no del rol
        //long refreshTokenValidity = rememberMe ? jwtRefreshTokenValidityInSecondsRemember : jwtRefreshTokenValidityInSecondsDefault;
        long refreshTokenValidity = jwtRefreshTokenValidityInSecondsDefault;

        return Jwts
                .builder()
                .setSubject(userByRolId.toString()) // El sujeto del token es el id del perfil o rol del usuario (specialistId o adminId)
                .claim("role", role) // El rol se incluye como claim en el token
                .claim("userId", userId)
                .signWith(key, SignatureAlgorithm.HS512) // Firmar el token con el algoritmo HS512 y la clave
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity * 1000)) // Configurar la fecha de expiración
                .compact();
    }

    public String createAccessTokenFromRefreshToken (String refreshToken) {

        // Extrae todos los claims del refresh token
        Claims claims = jwtParser.parseClaimsJws(refreshToken).getBody();
        Long userByRolId = Long.parseLong(claims.getSubject());
        Long userId = Long.parseLong(claims.get("userId").toString());
        String role = claims.get("role").toString();
        // Regresa un nuevo access token
        return Jwts
                .builder()
                .setSubject(userByRolId.toString()) // El sujeto del token es el id del perfil o rol del usuario (specialistId o adminId)
                .claim("role", role) // El rol se incluye como claim en el token
                .claim("userId", userId)
                .signWith(key, SignatureAlgorithm.HS512) // Firmar el token con el algoritmo HS512 y la clave
                .setExpiration(new Date(System.currentTimeMillis() + jwtValidityInSeconds * 1000)) // Configurar la fecha de expiración
                .compact();
    }

    // Método para obtener la autenticación a partir del token JWT
    public Authentication getAuthentication(String token) {
        // Extrae todos los claims del token JWT
        Claims claims = jwtParser.parseClaimsJws(token).getBody();

        Long userByRolId = Long.parseLong(claims.getSubject());
        // Obtener el ID del user
        Long userId = Long.parseLong(claims.get("userId").toString());
        // Obtener el rol del token
        String role = claims.get("role").toString();
        //Long userByRolId = claims.get("userByRolId", Long.class);
        // Lista de autoridades (roles) para el usuario
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
        // Esto no: El principal del contexto de seguridad será el email (subject) extraído del token
        // Esto no: User principal = new User(claims.getSubject(), "", authorities); // Es el user de security
        // UserPrincipal se utiliza para representar al usuario autenticado en lugar del User de Spring Security
        // porque necesito incluir información adicional como userId y userByRoleId, que no están presentes en la clase User.
        UserPrincipal userPrincipal = new UserPrincipal();
       // userPrincipal.setId(userId);
        userPrincipal.setUserByRoleId(userByRolId);
        userPrincipal.setUserId(userId);
        // Crear el objeto de autenticación con los detalles del usuario
        return new UsernamePasswordAuthenticationToken(userPrincipal, token, authorities);
    }

    // Validar tokens JWT:
    // Al recibir una solicitud, el TokenProvider verifica la validez del token JWT.
    // Esto incluye comprobar si el token no ha sido alterado y si no ha expirado.
    // Si el token es válido, se extrae la información de autenticación del usuario del token.
    public boolean validateToken(String token) {

        try {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            // Verificar si el token ha expirado
            Date expirationDate = claims.getExpiration();
            return !expirationDate.before(new Date());
        } catch (JwtException e) {
            return false;
        }
    }
}
