package com.crediya.solicitudes.request_service.domain.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GenericRepository<T, I> {
    Mono<T> save(T entity);
    Mono<T> findById(I id);
    Flux<T> findAll();
    Mono<Void> deleteById(I id);
}