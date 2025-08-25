package com.crediya.solicitudes.request_service.application;

import com.crediya.solicitudes.request_service.domain.model.Solicitud;
import com.crediya.solicitudes.request_service.domain.model.Estado;
import com.crediya.solicitudes.request_service.domain.model.TipoPrestamo;
import com.crediya.solicitudes.request_service.domain.repository.SolicitudRepositoryPort;
import com.crediya.solicitudes.request_service.domain.repository.TipoPrestamoRepositoryPort;
import com.crediya.solicitudes.request_service.domain.repository.EstadoRepositoryPort;
import com.crediya.solicitudes.request_service.infraestructure.adapter.mappers.EstadoMapper;
import com.crediya.solicitudes.request_service.infraestructure.adapter.mappers.SolicitudMapper;
import com.crediya.solicitudes.request_service.infraestructure.adapter.mappers.TipoPrestamoMapper;
import com.crediya.solicitudes.request_service.infraestructure.client.AutenticacionWebClient;
import com.crediya.solicitudes.request_service.infraestructure.dto.SolicitudDetallesDTO;
import com.crediya.solicitudes.request_service.infraestructure.entities.EstadoEntity;
import com.crediya.solicitudes.request_service.infraestructure.entities.SolicitudEntity;
import com.crediya.solicitudes.request_service.infraestructure.entities.TipoPrestamoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import java.util.UUID;


@Service
@Slf4j
public class SolicitudService {
    private final SolicitudRepositoryPort solicitudRepositoryPort;
    private final AutenticacionWebClient autenticacionWebClient;
    private final EstadoRepositoryPort estadoRepositoryPort;
    private final TipoPrestamoRepositoryPort tipoPrestamoRepositoryPort;
    private final SolicitudMapper solicitudMapper;
    private final EstadoMapper estadoMapper;
    private final TipoPrestamoMapper tipoPrestamoMapper;

    private static final String ESTADO_INICIAL = "PENDIENTE_REVISION";

    public SolicitudService(SolicitudRepositoryPort solicitudRepositoryPort, AutenticacionWebClient autenticacionWebClient,
                            EstadoRepositoryPort estadoRepositoryPort, TipoPrestamoRepositoryPort tipoPrestamoRepositoryPort,
                            SolicitudMapper solicitudMapper, EstadoMapper estadoMapper, TipoPrestamoMapper tipoPrestamoMapper) {
        this.solicitudRepositoryPort = solicitudRepositoryPort;
        this.autenticacionWebClient = autenticacionWebClient;
        this.estadoRepositoryPort = estadoRepositoryPort;
        this.tipoPrestamoRepositoryPort = tipoPrestamoRepositoryPort;
        this.solicitudMapper = solicitudMapper;
        this.estadoMapper = estadoMapper;
        this.tipoPrestamoMapper = tipoPrestamoMapper;
    }


    public Mono<Boolean> checkDbConnection() {
        return estadoRepositoryPort.findByNombre(ESTADO_INICIAL)
                .hasElement() // Retorna `true` si encuentra un elemento, `false` si no.
                .log("***** SolicitudService - Resultado de la prueba de conexión a la base de datos");
    }

    @Transactional
    public Mono<Solicitud> createSolicitudYComprobar(Solicitud solicitud) {
        return solicitudRepositoryPort.save(solicitud)
                .log("Después de guardar la solicitud") // <-- Añade esto aquí
                .flatMap(savedSolicitud -> solicitudRepositoryPort.findById(savedSolicitud.getId())
                        .log("Después de buscar el registro") // <-- Y aquí
                        .switchIfEmpty(Mono.error(new IllegalStateException("El registro no se pudo encontrar")))
                );
    }

    @Transactional
    public Mono<Solicitud> createSolicitud(Solicitud solicitud) {
        log.info("***** SolicitudService - Iniciando el proceso de creación.");

        //return autenticacionWebClient.validarUsuario(solicitud.getDocumentoIdentidad())
        return autenticacionWebClient.comprobarEmail(solicitud.getEmail())
                .flatMap(usuarioExiste -> {
                    if (Boolean.FALSE.equals(usuarioExiste)) {
                        return Mono.error(new IllegalArgumentException("El usuario no existe, debe registrarse para continuar con esta solicitud."));
                    }

                    return estadoRepositoryPort.findByNombre(ESTADO_INICIAL)
                            .log("***** SolicitudService - Buscando el estado inicial: " + ESTADO_INICIAL)
                            .switchIfEmpty(Mono.error(new IllegalStateException("El estado inicial '" + ESTADO_INICIAL + "' no se encontró.")))

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


    public Mono<Estado> findByNameEstado(String name) {
        return estadoRepositoryPort.findByNombre(name);
    }

    public Mono<TipoPrestamo> findByNamePrestamo(String name) {
        return tipoPrestamoRepositoryPort.findByNombre(name);
    }

}