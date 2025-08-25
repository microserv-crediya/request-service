package com.crediya.solicitudes.request_service.infraestructure.adapter.mappers;

import com.crediya.solicitudes.request_service.domain.model.TipoPrestamo;
import com.crediya.solicitudes.request_service.infraestructure.entities.TipoPrestamoEntity;
import org.springframework.stereotype.Component;


@Component
public class TipoPrestamoMapper {

    /**
     * Convierte una entidad de infraestructura (TipoPrestamoEntity) a un modelo de dominio (TipoPrestamo).
     *
     * @param entity La entidad de la base de datos que tiene las anotaciones @Table, @Id, etc.
     * @return Un objeto de dominio limpio, sin anotaciones de persistencia.
     */
    public static TipoPrestamo toDomain(TipoPrestamoEntity entity) {
        return TipoPrestamo.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .montoMinimo(entity.getMontoMinimo())
                .montoMaximo(entity.getMontoMaximo())
                .tasaInteres(entity.getTasaInteres())
                .validacionAutomatica(entity.getValidacionAutomatica())
                .build();
    }

    /**
     * Convierte un modelo de dominio (TipoPrestamo) a una entidad de infraestructura (TipoPrestamoEntity).
     *
     * @param domain El objeto TipoPrestamo de la capa de dominio.
     * @return La entidad TipoPrestamoEntity para la persistencia.
     */
    public static TipoPrestamoEntity toEntity(TipoPrestamo domain) {
        return TipoPrestamoEntity.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .montoMinimo(domain.getMontoMinimo())
                .montoMaximo(domain.getMontoMaximo())
                .tasaInteres(domain.getTasaInteres())
                .validacionAutomatica(domain.getValidacionAutomatica())
                .build();
    }
}
