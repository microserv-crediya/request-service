package com.crediya.solicitudes.request_service.infraestructure.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("solicitud")
public class SolicitudEntity {
    @Id
    @Column("id_solicitud")
    private UUID id;
    private BigDecimal monto;
    private Integer plazo;
    private String email;
    @Column("id_estado")
    private UUID idEstado;
    @Column("id_tipo_prestamo")
    private UUID idTipoPrestamo;
}
