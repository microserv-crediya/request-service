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

        return solicitudService.findByNamePrestamo(requestDTO.getTipoPrestamo())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El tipo de préstamo '" + requestDTO.getTipoPrestamo() + "' no existe.")))
                .flatMap(tipoPrestamo -> {
                    Solicitud solicitud = SolicitudMapper.toDomainRequest(requestDTO);
                    solicitud.setIdTipoPrestamo(tipoPrestamo.getId());
                    return solicitudService.createSolicitud(solicitud);
                })
                .flatMap(solicitudEnt ->
                        // Obtén los detalles adicionales para la respuesta.
                        solicitudService.getDetailsForResponse(solicitudEnt)
                                .switchIfEmpty(Mono.error(new IllegalStateException("No se encontraron detalles para la solicitud")))
                                .map(details -> {

                                    log.info("***** SolicitudController - Solicitud procesada y lista para la respuesta.");
                                    return SolicitudMapper.toResponseDto(
                                            solicitudEnt,
                                            details.getNombreEstado(),
                                            details.getNombreTipoPrestamo(),
                                            solicitudEnt.getDocumentoIdentidad()
                                    );
                                })
                );
    }
}