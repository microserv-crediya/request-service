package com.crediya.solicitudes.request_service.infraestructure.adapter;

import com.crediya.solicitudes.request_service.domain.model.Estado;
import com.crediya.solicitudes.request_service.domain.repository.EstadoRepositoryPort;
import com.crediya.solicitudes.request_service.infraestructure.adapter.mappers.EstadoMapper;
import com.crediya.solicitudes.request_service.infraestructure.adapter.repository.EstadoR2dbcRepository;
import com.crediya.solicitudes.request_service.infraestructure.entities.EstadoEntity;
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
    public Mono<Estado> save(Estado estado) {
        return Mono.just(estado)
                .map(EstadoMapper::toEntity)
                .flatMap(repository::save)
                .map(EstadoMapper::toDomain);
    }




    @Override
    public Mono<Estado> findById(UUID id) {
        return repository.findById(id)
                .map(EstadoMapper::toDomain);
    }

    @Override
    public Flux<Estado> findAll() {
        return repository.findAll()
                .map(EstadoMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<Estado> findByNombre(String nombre) {
        return repository.findByNombre(nombre)
                .map(EstadoMapper::toDomain);
    }
}