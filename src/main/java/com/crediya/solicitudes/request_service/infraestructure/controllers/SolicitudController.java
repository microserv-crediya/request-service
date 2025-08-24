package com.crediya.solicitudes.request_service.infraestructure.controllers;

import com.crediya.solicitudes.request_service.application.SolicitudService;
import com.crediya.solicitudes.request_service.domain.model.Solicitud;
import com.crediya.solicitudes.request_service.infraestructure.adapter.mappers.SolicitudMapper;
import com.crediya.solicitudes.request_service.infraestructure.dto.SolicitudDTO;
import com.crediya.solicitudes.request_service.infraestructure.dto.SolicitudResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
    public Mono<SolicitudResponseDTO> createSolicitud(@RequestBody SolicitudDTO requestDTO) {
        log.info("***** SolicitudController - Recibiendo una nueva solicitud de préstamo.");

        Solicitud solicitud = SolicitudMapper.toDomain(requestDTO);

        return solicitudService.createSolicitud(solicitud)
                .flatMap(savedSolicitud ->
                        solicitudService.getDetailsForResponse(savedSolicitud)
                                .map(details -> {
                                    log.info("***** SolicitudController - Solicitud procesada y lista para la respuesta.");
                                    return SolicitudMapper.toResponseDto(
                                            savedSolicitud,
                                            details.getNombreEstado(),
                                            details.getNombreTipoPrestamo()
                                    );
                                })
                );
    }
}