package com.crediya.solicitudes.request_service.domain.repository;

import com.crediya.solicitudes.request_service.domain.model.Estado;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface EstadoRepositoryPort extends GenericRepository<Estado, UUID> {
    Mono<Estado> findByNombre(String nombre);
}
