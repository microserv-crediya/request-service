package com.crediya.solicitudes.request_service.domain.repository;


import com.crediya.solicitudes.request_service.domain.model.Solicitud;
import com.crediya.solicitudes.request_service.infraestructure.entities.SolicitudEntity;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface SolicitudRepositoryPort extends GenericRepository<Solicitud, UUID> {
   Flux<Solicitud> findByEmail(String email);
}
