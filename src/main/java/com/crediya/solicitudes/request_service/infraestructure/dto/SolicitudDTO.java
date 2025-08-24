package com.crediya.solicitudes.request_service.infraestructure.dto;

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
public class SolicitudDTO {
    private BigDecimal monto;
    private Integer plazo;
    private String email;
    private UUID idTipoPrestamo;
    private UUID idEstado;
}
