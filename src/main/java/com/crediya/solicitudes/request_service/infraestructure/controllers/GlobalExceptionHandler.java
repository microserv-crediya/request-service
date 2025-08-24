package com.crediya.solicitudes.request_service.infraestructure.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {


    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Solicitud inválida: {}", ex.getMessage());
        return Mono.just(ex.getMessage());
    }

    // Puedes agregar más manejadores para otros tipos de excepciones.
    // Ejemplo para manejar errores genéricos y no deseados.
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<String> handleGenericException(Exception ex) {
        // Devuelve un mensaje de error genérico para el usuario
        // mientras se loguea la excepción completa para el equipo de desarrollo.
        log.error("Ocurrió un error inesperado. Consulte los registros para más detalles::.", ex);
        return Mono.just("Ocurrió un error inesperado. Por favor, inténtelo de nuevo más tarde.");
    }
}
