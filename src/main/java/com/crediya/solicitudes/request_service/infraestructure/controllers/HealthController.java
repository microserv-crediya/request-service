package com.crediya.solicitudes.request_service.infraestructure.controllers;

import com.crediya.solicitudes.request_service.application.SolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {

    private final SolicitudService solicitudService;

    @GetMapping("/db")
    public Mono<ResponseEntity<String>> checkDbConnection() {
        return solicitudService.checkDbConnection()
                .map(isConnected -> {
                    if (isConnected) {
                        return ResponseEntity.ok("Conexión a la base de datos y consulta exitosa. ✅");
                    } else {
                        return ResponseEntity.status(500).body("Error: La conexión funciona, pero el registro inicial no se encontró. ❌");
                    }
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.status(500).body("Error de conexión a la base de datos: " + e.getMessage())));
    }
}
