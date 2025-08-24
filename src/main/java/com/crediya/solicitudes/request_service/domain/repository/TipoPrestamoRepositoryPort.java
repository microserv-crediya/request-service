package com.crediya.solicitudes.request_service.domain.repository;

import com.crediya.solicitudes.request_service.domain.model.TipoPrestamo;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface TipoPrestamoRepositoryPort extends GenericRepository<TipoPrestamo, UUID> {
    Mono<TipoPrestamo> findByNombre(String nombre);
}
