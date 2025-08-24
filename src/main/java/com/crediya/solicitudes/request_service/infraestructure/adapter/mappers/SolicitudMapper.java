package com.crediya.solicitudes.request_service.infraestructure.adapter.mappers;

import com.crediya.solicitudes.request_service.domain.model.Solicitud;
import com.crediya.solicitudes.request_service.infraestructure.entities.SolicitudEntity;
import com.crediya.solicitudes.request_service.infraestructure.dto.SolicitudDTO;
;
import com.crediya.solicitudes.request_service.infraestructure.dto.SolicitudResponseDTO;

import java.util.UUID;

public class SolicitudMapper {

    public static Solicitud toDomain(SolicitudDTO dto) {
        return Solicitud.builder()
                .id(UUID.randomUUID())
                .monto(dto.getMonto())
                .plazo(dto.getPlazo())
                .email(dto.getEmail())
                .idEstado(dto.getIdEstado())
                .idTipoPrestamo(dto.getIdTipoPrestamo())
                .documentoIdentidad(dto.getDocumentoIdentidad())
                .build();
    }

    //Convierte un DTO de respuesta a una entidad de dominio.
    public static Solicitud toDomain(SolicitudEntity entity) {
        return Solicitud.builder()
                .id(entity.getId())
                .monto(entity.getMonto())
                .plazo(entity.getPlazo())
                .email(entity.getEmail())
                .idEstado(entity.getIdEstado())
                .idTipoPrestamo(entity.getIdTipoPrestamo())
                .documentoIdentidad(entity.getDocumentoIdentidad())
                .build();
    }

    //Convierte una entidad de dominio a una entidad de infraestructura.
    public static SolicitudEntity toEntity(Solicitud domain) {
        return SolicitudEntity.builder()
                .id(domain.getId())
                .monto(domain.getMonto())
                .plazo(domain.getPlazo())
                .email(domain.getEmail())
                .idEstado(domain.getIdEstado())
                .idTipoPrestamo(domain.getIdTipoPrestamo())
                .documentoIdentidad(domain.getDocumentoIdentidad())
                .build();
    }

    public static SolicitudResponseDTO toResponseDto(Solicitud solicitud, String estadoName, String tipoPrestamoName, String documentoIdentidad) {
        return SolicitudResponseDTO.builder()
                .id(solicitud.getId())
                .monto(solicitud.getMonto())
                .plazo(solicitud.getPlazo())
                .email(solicitud.getEmail())
                .estado(estadoName)
                .tipoPrestamo(tipoPrestamoName)
                .documentoIdentidad(documentoIdentidad)
                .build();
    }
}