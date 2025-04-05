package com.healthplus.healthplus_api.auth.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthplus.healthplus_api.exception.CustomErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // Este método se ejecuta cuando un usuario no autenticado intenta acceder a un recurso que requiere autenticación
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // Obtener el mensaje de error si es que existe, de lo contrario establece un mensaje por defecto
        String exceptionMsg = (String) request.getAttribute("exception");

        if (exceptionMsg == null) {
            exceptionMsg = "Token not found or invalid";
        }

        // Crear un objeto de error personalizado con la fecha, mensaje de error y la URI de la solicitud
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(LocalDateTime.now(), exceptionMsg, request.getRequestURI());

        // Establecer el estado HTTP de respuesta como 401 Unauthorized
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        // Escribir la respuesta de error en formato JSON en el cuerpo de la respuesta
        response.getWriter().write(convertObjectToJson(customErrorResponse));

        // Establecer el tipo de la respuesta del contenido como JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null)
            return null;
        // Convertir el objeto en un cadena JSON con el ObjectMapper de Jackson
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules(); // Esto registra módulos adicionales si es necesario (por ejemplo, soporte de fechas)
        return mapper.writeValueAsString(object); // Devolver el objeto convertido a JSON
    }
}
