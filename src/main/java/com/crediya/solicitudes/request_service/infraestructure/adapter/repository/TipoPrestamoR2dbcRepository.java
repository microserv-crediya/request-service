package com.crediya.solicitudes.request_service.infraestructure.adapter.repository;

import com.crediya.solicitudes.request_service.domain.model.TipoPrestamo;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface TipoPrestamoR2dbcRepository extends R2dbcRepository<TipoPrestamo, UUID> {
    Mono<TipoPrestamo> findByNombre(String nombre);
}
