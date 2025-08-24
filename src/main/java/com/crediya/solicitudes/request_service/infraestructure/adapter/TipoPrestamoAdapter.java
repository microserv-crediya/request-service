package com.crediya.solicitudes.request_service.infraestructure.adapter;

import com.crediya.solicitudes.request_service.domain.model.TipoPrestamo;
import com.crediya.solicitudes.request_service.domain.repository.TipoPrestamoRepositoryPort;
import com.crediya.solicitudes.request_service.infraestructure.adapter.mappers.TipoPrestamoMapper;
import com.crediya.solicitudes.request_service.infraestructure.adapter.repository.TipoPrestamoR2dbcRepository;
import com.crediya.solicitudes.request_service.infraestructure.entities.TipoPrestamoEntity;
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
        TipoPrestamoEntity tipoPrestamo = TipoPrestamoMapper.toEntity(entity);
        return repository.save(tipoPrestamo).map(TipoPrestamoMapper::toDomain);
    }

    @Override
    public Mono<TipoPrestamo> findById(UUID id) {
        return repository.findById(id).map(TipoPrestamoMapper::toDomain);
    }

    @Override
    public Flux<TipoPrestamo> findAll() {
        return repository.findAll().map(TipoPrestamoMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<TipoPrestamo> findByNombre(String nombre) {
        return repository.findByNombre(nombre).map(TipoPrestamoMapper::toDomain);
    }
}
