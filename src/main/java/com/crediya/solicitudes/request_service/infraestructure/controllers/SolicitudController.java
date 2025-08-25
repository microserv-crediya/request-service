package com.crediya.solicitudes.request_service.infraestructure.controllers;

import com.crediya.solicitudes.request_service.application.SolicitudService;
import com.crediya.solicitudes.request_service.domain.model.Solicitud;
import com.crediya.solicitudes.request_service.domain.model.TipoPrestamo;
import com.crediya.solicitudes.request_service.infraestructure.adapter.mappers.SolicitudMapper;
import com.crediya.solicitudes.request_service.infraestructure.dto.SolicitudDTO;
import com.crediya.solicitudes.request_service.infraestructure.dto.SolicitudRequestDTO;
import com.crediya.solicitudes.request_service.infraestructure.dto.SolicitudResponseDTO;
import com.crediya.solicitudes.request_service.infraestructure.entities.SolicitudEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/solicitud")
@Tag(name = "Gestión de Solicitudes", description = "Operaciones para la creación de solicitudes de préstamos.")
public class SolicitudController {

    // Declaración estándar y correcta del logger
    private static final Logger log = LoggerFactory.getLogger(SolicitudController.class);
    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Crear una nueva solicitud de préstamo",
            description = "Registra una solicitud de préstamo y asigna un estado inicial.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Solicitud creada exitosamente.",  content = @Content(schema = @Schema(implementation = SolicitudResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida. El tipo de préstamo no existe.")
            }
    )
    public Mono<SolicitudResponseDTO> createSolicitud(@RequestBody SolicitudRequestDTO requestDTO) {
        log.info("***** SolicitudController - Recibiendo una nueva solicitud de préstamo.");

        return solicitudService.procesarSolicitudCompleta(requestDTO)
                .doOnSuccess(response -> log.info("***** SolicitudController - Solicitud procesada exitosamente."))
                .doOnError(error -> log.error("***** SolicitudController - Error procesando solicitud: {}", error.getMessage()));
    }

    @Operation(summary = "Comprueba el estado inicial",description = "Verfica si existe el registo PENDIENTE_REVISION existe en la tabla de Estados")
    @GetMapping("/status")
    public Mono<ResponseEntity<String>> checkEstado() {
        return solicitudService.findByNombre()
                .map(exists -> {
                    if (exists) {
                        return ResponseEntity.ok("Si existe estado Inicial");
                    } else {
                        return ResponseEntity.status(500).body("Error:  el registro inicial no se encontró");
                    }
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.status(500).body("Error de conexión a la base de datos: " + e.getMessage())));
    }
}