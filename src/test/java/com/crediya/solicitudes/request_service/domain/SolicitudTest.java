package com.crediya.solicitudes.request_service.domain;

import com.crediya.solicitudes.request_service.domain.model.Solicitud;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SolicitudTest {

    @Test
    void debeCrearSolicitudConDatosValidos() {
        // Arrange
        String documentoIdentidad = "1234567890";
        BigDecimal monto = new BigDecimal("1000000.00");
        int plazo = 12;

        // Act
        Solicitud solicitud = Solicitud.builder()
                .documentoIdentidad(documentoIdentidad)
                .monto(monto)
                .plazo(plazo)
                .build();

        // Assert
        assertNotNull(solicitud);
        assertEquals(documentoIdentidad, solicitud.getDocumentoIdentidad());
        assertEquals(monto, solicitud.getMonto());
        assertEquals(plazo, solicitud.getPlazo());
    }

    @Test
    void debeAsignarIdAlCrearSolicitud() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        Solicitud solicitud = Solicitud.builder()
                .id(id)
                .documentoIdentidad("1234567890")
                .monto(new BigDecimal("1000000.00"))
                .plazo(12)
                .build();

        // Assert
        assertNotNull(solicitud.getId());
        assertEquals(id, solicitud.getId());
    }
}
