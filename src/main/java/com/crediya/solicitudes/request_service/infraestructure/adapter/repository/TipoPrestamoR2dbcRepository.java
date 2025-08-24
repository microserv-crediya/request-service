package com.crediya.solicitudes.request_service.infraestructure.adapter.repository;

import com.crediya.solicitudes.request_service.domain.model.TipoPrestamo;
import com.crediya.solicitudes.request_service.infraestructure.entities.TipoPrestamoEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface TipoPrestamoR2dbcRepository extends R2dbcRepository<TipoPrestamoEntity, UUID> {
    Mono<TipoPrestamoEntity> findByNombre(String nombre);
}
