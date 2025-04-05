package com.healthplus.healthplus_api.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleModelNotFoundException(ResourceNotFoundException ex,
                                                                              WebRequest request) {

        CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CustomErrorResponse> handleModelBadRequestException(BadRequestException ex,
                                                                              WebRequest request) {

        CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleModelAllException(Exception ex, WebRequest request) {

        CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomErrorResponse> handleAccessDeniedException(AccessDeniedException ex,
                                                                           WebRequest request) {

        CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(err, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomErrorResponse> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        CustomErrorResponse error = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);  // 401 Unauthorized
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField().concat(":").concat(e.getDefaultMessage()))
                .collect(Collectors.joining(","));

        CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), msg, request.getDescription(false));

        return new ResponseEntity<>(err, HttpStatus.UNPROCESSABLE_ENTITY);
    }

}

