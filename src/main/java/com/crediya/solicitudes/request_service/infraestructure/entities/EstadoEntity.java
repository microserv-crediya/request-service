package com.crediya.solicitudes.request_service.infraestructure.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("estados")
public class EstadoEntity {
    @Id
    @Column("id_estado")
    private UUID id;
    @Column("nombre")
    private String nombre;
    private String descripcion;
}