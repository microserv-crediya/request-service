package com.crediya.solicitudes.request_service.infraestructure.adapter;

import com.crediya.solicitudes.request_service.domain.model.Solicitud;
import com.crediya.solicitudes.request_service.domain.repository.SolicitudRepositoryPort;
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
        return repository.save(entity);
    }

    @Override
    public Mono<Solicitud> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Flux<Solicitud> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }

    @Override
    public Flux<Solicitud> findByEmail(String email) {
        return repository.findByEmail(email);
    }
}