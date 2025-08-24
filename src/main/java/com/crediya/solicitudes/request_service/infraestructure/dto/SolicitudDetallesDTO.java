package com.crediya.solicitudes.request_service.infraestructure.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class SolicitudDetallesDTO {
    private String nombreEstado;
    private String nombreTipoPrestamo;
}