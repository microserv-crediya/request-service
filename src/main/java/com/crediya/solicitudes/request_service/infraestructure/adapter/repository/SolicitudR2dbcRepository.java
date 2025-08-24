package com.crediya.solicitudes.request_service.infraestructure.adapter.repository;

import com.crediya.solicitudes.request_service.domain.model.Solicitud;
import com.crediya.solicitudes.request_service.infraestructure.entities.SolicitudEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface SolicitudR2dbcRepository extends R2dbcRepository<SolicitudEntity, UUID> {
    Flux<SolicitudEntity> findByEmail(String email);
}