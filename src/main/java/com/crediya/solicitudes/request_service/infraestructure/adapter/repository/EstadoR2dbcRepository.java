package com.crediya.solicitudes.request_service.infraestructure.adapter.repository;

import com.crediya.solicitudes.request_service.domain.model.Estado;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface EstadoR2dbcRepository extends R2dbcRepository<Estado, UUID> {
    Mono<Estado> findByNombre(String nombre);
}