package com.crediya.solicitudes.request_service.infraestructure.adapter.repository;

import com.crediya.solicitudes.request_service.domain.model.Estado;
import com.crediya.solicitudes.request_service.infraestructure.entities.EstadoEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface EstadoR2dbcRepository extends R2dbcRepository<EstadoEntity, UUID> {
    Mono<EstadoEntity> findByNombre(String nombre);

}