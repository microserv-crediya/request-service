package com.crediya.solicitudes.request_service.infraestructure.dto;

import java.util.UUID;

public class UserResponseDTO {

    private UUID id;
    private String nombres;
    private String apellidos;
    private String correoElectronico;
    private String documentoIdentidad;

    public void setId(UUID id) { this.id = id;}
    public void setNombres(String nombres) { this.nombres = nombres; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setDocumentoIdentidad(String documentoIdentidad) { this.documentoIdentidad = documentoIdentidad; }

    public UUID getId() { return id; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getCorreoElectronico() {return correoElectronico; }
    public String getDocumentoIdentidad() { return documentoIdentidad; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico;  }



}
