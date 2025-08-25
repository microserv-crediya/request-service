package com.crediya.solicitudes.request_service.infraestructure.adapter.mappers;

import com.crediya.solicitudes.request_service.domain.model.Estado;
import com.crediya.solicitudes.request_service.infraestructure.entities.EstadoEntity;
import org.springframework.stereotype.Component;

@Component
public class EstadoMapper {

    /**
     * Convierte un modelo de dominio (Estado) a una entidad de infraestructura (EstadoEntity).
     *
     * @param domain El objeto Estado del dominio.
     * @return La entidad EstadoEntity para la persistencia.
     */
    public static EstadoEntity toEntity(Estado domain) {
        return EstadoEntity.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .descripcion(domain.getDescripcion())
                .build();
    }

    /**
     * Convierte una entidad de infraestructura (EstadoEntity) a un modelo de dominio (Estado).
     *
     * @param entity La entidad EstadoEntity de la base de datos.
     * @return El objeto Estado para la capa de dominio.
     */
    public static Estado toDomain(EstadoEntity entity) {
        return Estado.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .build();
    }
}