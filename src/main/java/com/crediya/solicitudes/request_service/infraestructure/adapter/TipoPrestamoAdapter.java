package com.crediya.solicitudes.request_service.infraestructure.adapter;

import com.crediya.solicitudes.request_service.domain.model.TipoPrestamo;
import com.crediya.solicitudes.request_service.domain.repository.TipoPrestamoRepositoryPort;
import com.crediya.solicitudes.request_service.infraestructure.adapter.repository.TipoPrestamoR2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import java.util.UUID;

@Repository
public class TipoPrestamoAdapter implements TipoPrestamoRepositoryPort {

    private final TipoPrestamoR2dbcRepository repository;

    public TipoPrestamoAdapter(TipoPrestamoR2dbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<TipoPrestamo> save(TipoPrestamo entity) {
        return repository.save(entity);
    }

    @Override
    public Mono<TipoPrestamo> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Flux<TipoPrestamo> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<TipoPrestamo> findByNombre(String nombre) {
        return repository.findByNombre(nombre);
    }
}
