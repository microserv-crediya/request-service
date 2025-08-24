package com.crediya.solicitudes.request_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Solicitud {
    private UUID id;
    private BigDecimal monto;
    private Integer plazo;
    private String email;
    private UUID idEstado;
    private UUID idTipoPrestamo;
    private String documentoIdentidad;
}