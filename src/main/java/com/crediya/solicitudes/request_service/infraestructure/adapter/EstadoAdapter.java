package com.crediya.solicitudes.request_service.infraestructure.adapter;

import com.crediya.solicitudes.request_service.domain.model.Estado;
import com.crediya.solicitudes.request_service.domain.repository.EstadoRepositoryPort;
import com.crediya.solicitudes.request_service.infraestructure.adapter.repository.EstadoR2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import java.util.UUID;

@Repository
public class EstadoAdapter implements EstadoRepositoryPort {

    private final EstadoR2dbcRepository repository;

    public EstadoAdapter(EstadoR2dbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Estado> save(Estado entity) {
        return repository.save(entity);
    }

    @Override
    public Mono<Estado> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Flux<Estado> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<Estado> findByNombre(String nombre) {
        return repository.findByNombre(nombre);
    }
}