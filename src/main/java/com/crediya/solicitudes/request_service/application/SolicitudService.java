package com.crediya.solicitudes.request_service.application;

import com.crediya.solicitudes.request_service.domain.model.Solicitud;
import com.crediya.solicitudes.request_service.domain.model.Estado;
import com.crediya.solicitudes.request_service.domain.model.TipoPrestamo;
import com.crediya.solicitudes.request_service.domain.repository.SolicitudRepositoryPort;
import com.crediya.solicitudes.request_service.domain.repository.TipoPrestamoRepositoryPort;
import com.crediya.solicitudes.request_service.domain.repository.EstadoRepositoryPort;
import com.crediya.solicitudes.request_service.infraestructure.dto.SolicitudDetallesDTO;
import com.crediya.solicitudes.request_service.infraestructure.entities.EstadoEntity;
import com.crediya.solicitudes.request_service.infraestructure.entities.TipoPrestamoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import java.util.UUID;

@Service
@Slf4j
public class SolicitudService {
    private final SolicitudRepositoryPort solicitudRepositoryPort;
    private final TipoPrestamoRepositoryPort tipoPrestamoRepositoryPort;
    private final EstadoRepositoryPort estadoRepositoryPort;
    private static final String ESTADO_INICIAL = "PENDIENTE_REVISION";

    public SolicitudService(SolicitudRepositoryPort solicitudRepositoryPort, TipoPrestamoRepositoryPort tipoPrestamoRepositoryPort, EstadoRepositoryPort estadoRepositoryPort) {
        this.solicitudRepositoryPort = solicitudRepositoryPort;
        this.tipoPrestamoRepositoryPort = tipoPrestamoRepositoryPort;
        this.estadoRepositoryPort = estadoRepositoryPort;
    }


    public Mono<Boolean> checkDbConnection() {
        return estadoRepositoryPort.findByNombre(ESTADO_INICIAL)
                .hasElement() // Retorna `true` si encuentra un elemento, `false` si no.
                .log("***** SolicitudService - Resultado de la prueba de conexión a la base de datos");
    }

    public Mono<Solicitud> createSolicitud(Solicitud solicitud) {
        log.info("***** SolicitudService - Iniciando el proceso de creación.");
        return tipoPrestamoRepositoryPort.findById(solicitud.getIdTipoPrestamo())
                .log("***** SolicitudService - Buscando el tipo de préstamo con ID: " + solicitud.getIdTipoPrestamo())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El tipo de préstamo seleccionado no existe.")))
                .flatMap(tipoPrestamo -> {
                    log.info("***** SolicitudService - Tipo de préstamo encontrado.");
                    return estadoRepositoryPort.findByNombre(ESTADO_INICIAL)
                            .log("***** SolicitudService - Buscando el estado inicial: " + ESTADO_INICIAL)
                            .switchIfEmpty(Mono.error(new IllegalStateException("El estado inicial '" + ESTADO_INICIAL + "' no se encontró en la base de datos.")))
                            .flatMap(estado -> {
                                solicitud.setIdEstado(estado.getId());
                                log.info("***** SolicitudService - Asignando el estado inicial a la solicitud.");
                                return solicitudRepositoryPort.save(solicitud)
                                        .log("***** SolicitudService - Guardando la solicitud.");
                            });
                })
                .doOnError(error -> log.error("***** ERROR en el flujo de solicitud: " + error.getMessage()));
    }

    public Mono<SolicitudDetallesDTO> getDetailsForResponse(Solicitud solicitud) {
        log.info("***** getDetailsForResponse - Preparando la respuesta.");
        Mono<Estado> estadoMono = estadoRepositoryPort.findById(solicitud.getIdEstado());
        Mono<TipoPrestamo> tipoMono = tipoPrestamoRepositoryPort.findById(solicitud.getIdTipoPrestamo());
        return Mono.zip(estadoMono, tipoMono)
                .log("***** getDetailsForResponse - Combinando los resultados para el DTO.")
                .map(tuple -> SolicitudDetallesDTO.builder()
                        .nombreEstado(tuple.getT1().getNombre())
                        .nombreTipoPrestamo(tuple.getT2().getNombre())
                        .build());
    }
}