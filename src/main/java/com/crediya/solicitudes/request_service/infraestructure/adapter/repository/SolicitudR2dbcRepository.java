package com.crediya.solicitudes.request_service.infraestructure.adapter.repository;

import com.crediya.solicitudes.request_service.domain.model.Solicitud;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface SolicitudR2dbcRepository extends R2dbcRepository<Solicitud, UUID> {
    Flux<Solicitud> findByEmail(String email);
}