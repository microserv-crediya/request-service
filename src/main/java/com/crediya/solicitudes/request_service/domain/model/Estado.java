package com.crediya.solicitudes.request_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Estado {
    private UUID id;
    private String nombre;
    private String descripcion;
}
