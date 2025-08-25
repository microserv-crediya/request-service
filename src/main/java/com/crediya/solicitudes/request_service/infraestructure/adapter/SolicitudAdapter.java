package com.crediya.solicitudes.request_service.infraestructure.adapter;

import com.crediya.solicitudes.request_service.domain.model.Solicitud;
import com.crediya.solicitudes.request_service.domain.repository.SolicitudRepositoryPort;
import com.crediya.solicitudes.request_service.infraestructure.adapter.mappers.SolicitudMapper;
import com.crediya.solicitudes.request_service.infraestructure.adapter.repository.SolicitudR2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import java.util.UUID;

@Repository
public class SolicitudAdapter implements SolicitudRepositoryPort {

    private final SolicitudR2dbcRepository repository;

    public SolicitudAdapter(SolicitudR2dbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Solicitud> save(Solicitud entity) {
         return Mono.just(entity)
                .map(SolicitudMapper::toEntity) // Convierte el modelo de dominio a una entidad de infraestructura
                .flatMap(repository::save) // Guarda la entidad en la base de datos
                .map(SolicitudMapper::toDomain);
    }

    @Override
    public Mono<Solicitud> findById(UUID id) {
        return repository.findById(id).map(SolicitudMapper::toDomain);
    }

    @Override
    public Flux<Solicitud> findAll() {
        return repository.findAll().map(SolicitudMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }

    @Override
    public Flux<Solicitud> findByEmail(String email) {
        return repository.findByEmail(email).map(SolicitudMapper::toDomain);
    }
}