package com.crediya.solicitudes.request_service.infraestructure.adapter;

import com.crediya.solicitudes.request_service.domain.model.Solicitud;
import com.crediya.solicitudes.request_service.infraestructure.entities.SolicitudEntity;
import com.crediya.solicitudes.request_service.infraestructure.dto.SolicitudDTO;
import com.crediya.solicitudes.request_service.infraestructure.entities.SolicitudEntity;;
import com.crediya.solicitudes.request_service.infraestructure.dto.SolicitudResponseDTO;

public class SolicitudMapper {

    /**
     * Convierte un DTO de solicitud de la capa de infraestructura a una entidad de dominio.
     * @param dto El objeto SolicitudRequestDTO de entrada.
     * @return Una entidad de dominio SolicitudEntity.
     */
    public static Solicitud toDomain(SolicitudDTO dto) {
        return Solicitud.builder()
                .monto(dto.getMonto())
                .plazo(dto.getPlazo())
                .email(dto.getEmail())
                .idTipoPrestamo(dto.getIdTipoPrestamo())
                .build();
    }

    /**
     * Convierte una entidad de dominio a un DTO de respuesta para la capa de infraestructura.
     * @param solicitud La entidad SolicitudEntity.
     * @param estadoName El nombre del estado de la solicitud.
     * @param tipoPrestamoName El nombre del tipo de préstamo.
     * @return Un objeto SolicitudResponseDTO.
     */
    public static SolicitudResponseDTO toResponseDto(Solicitud solicitud, String estadoName, String tipoPrestamoName) {
        return SolicitudResponseDTO.builder()
                .id(solicitud.getId())
                .monto(solicitud.getMonto())
                .plazo(solicitud.getPlazo())
                .email(solicitud.getEmail())
                .estado(estadoName)
                .tipoPrestamo(tipoPrestamoName)
                .build();
    }
}