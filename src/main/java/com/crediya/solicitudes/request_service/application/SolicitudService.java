package com.crediya.solicitudes.request_service.application;

import com.crediya.solicitudes.request_service.domain.model.Solicitud;
import com.crediya.solicitudes.request_service.domain.model.Estado;
import com.crediya.solicitudes.request_service.domain.model.TipoPrestamo;
import com.crediya.solicitudes.request_service.domain.repository.SolicitudRepositoryPort;
import com.crediya.solicitudes.request_service.domain.repository.TipoPrestamoRepositoryPort;
import com.crediya.solicitudes.request_service.domain.repository.EstadoRepositoryPort;
import com.crediya.solicitudes.request_service.infraestructure.dto.SolicitudDetallesDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import java.util.UUID;

@Service
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

    /**
     * Crea una nueva solicitud de préstamo, valida el tipo de préstamo y asigna un estado inicial.
     * @param solicitud La entidad de la solicitud a crear.
     * @return Un Mono que emite la solicitud guardada.
     */
    public Mono<Solicitud> createSolicitud(Solicitud solicitud) {
        return Mono.just(solicitud)
                .log("SolicitudService.createSolicitud - Inicia el proceso de creación")
                .flatMap(s -> tipoPrestamoRepositoryPort.findById(s.getIdTipoPrestamo())
                        .log("SolicitudService - Buscando tipo de préstamo: " + s.getIdTipoPrestamo())
                )
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El tipo de préstamo seleccionado no existe.")))
                .flatMap(tipoPrestamo -> {
                    // Validación del monto (ejemplo de lógica de negocio)
                    if (solicitud.getMonto().compareTo(tipoPrestamo.getMontoMinimo()) < 0 || solicitud.getMonto().compareTo(tipoPrestamo.getMontoMaximo()) > 0) {
                        return Mono.error(new IllegalArgumentException("El monto de la solicitud está fuera del rango permitido."));
                    }

                    return estadoRepositoryPort.findByNombre(ESTADO_INICIAL)
                            .log("SolicitudService - Buscando estado inicial: " + ESTADO_INICIAL)
                            .switchIfEmpty(Mono.error(new IllegalStateException("El estado inicial '" + ESTADO_INICIAL + "' no se encontró en la base de datos.")))
                            .flatMap(estado -> {
                                solicitud.setIdEstado(estado.getId());
                                return solicitudRepositoryPort.save(solicitud)
                                        .log("SolicitudService - Guardando la solicitud con el estado inicial");
                            });
                })
                .doOnError(error -> System.err.println("Error en el flujo de solicitud: " + error.getMessage()));
    }

    /**
     * Obtiene el nombre del estado y del tipo de préstamo y los empaqueta en un DTO.
     */
    public Mono<SolicitudDetallesDTO> getDetailsForResponse(Solicitud solicitud) {
        Mono<Estado> estadoMono = estadoRepositoryPort.findById(solicitud.getIdEstado())
                .log("getDetailsForResponse - Buscando el estado por ID");
        Mono<TipoPrestamo> tipoMono = tipoPrestamoRepositoryPort.findById(solicitud.getIdTipoPrestamo())
                .log("getDetailsForResponse - Buscando el tipo de préstamo por ID");

        return Mono.zip(estadoMono, tipoMono)
                .log("getDetailsForResponse - Combinando resultados")
                .map(tuple -> SolicitudDetallesDTO.builder()
                        .nombreEstado(tuple.getT1().getNombre())
                        .nombreTipoPrestamo(tuple.getT2().getNombre())
                        .build());
    }
}