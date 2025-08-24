package com.crediya.solicitudes.request_service.infraestructure;

import com.crediya.solicitudes.request_service.application.SolicitudService;
import com.crediya.solicitudes.request_service.domain.model.Solicitud;
import com.crediya.solicitudes.request_service.infraestructure.controllers.SolicitudController;
import com.crediya.solicitudes.request_service.infraestructure.dto.SolicitudDTO;
import com.crediya.solicitudes.request_service.infraestructure.dto.SolicitudDetallesDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(SolicitudController.class)
class SolicitudControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SolicitudService solicitudService;

    // Métodos de ayuda para crear objetos de prueba
    private SolicitudDTO createValidSolicitudRequestDTO() {
        return SolicitudDTO.builder()
                .documentoIdentidad("1234567890")
                .monto(new BigDecimal(1000000.0))
                .plazo(12)
                .build();
    }

    private Solicitud createValidSolicitud() {
        return Solicitud.builder()
                .id(UUID.randomUUID())
                .documentoIdentidad("1234567890")
                .monto(new BigDecimal("1000000"))
                .plazo(12)
                .build();
    }

    private SolicitudDetallesDTO createValidDetallesDTO() {
        return SolicitudDetallesDTO.builder()
                .nombreEstado("En proceso")
                .nombreTipoPrestamo("Préstamo Personal")
                .documentoIdentidad("7777788888")
                .build();
    }

    @Test
    void debeCrearSolicitud_cuandoUsuarioEsValido() {
        // Arrange
        SolicitudDTO requestDTO = createValidSolicitudRequestDTO();
        Solicitud solicitud = createValidSolicitud();

        // 1. Simula el comportamiento de createSolicitud
        when(solicitudService.createSolicitud(any(Solicitud.class))).thenReturn(Mono.just(solicitud));

        // 2. Simula el comportamiento de getDetailsForResponse
        //    Esto es crucial para que el Mono no sea null
        when(solicitudService.getDetailsForResponse(any(Solicitud.class))).thenReturn(Mono.just(createValidDetallesDTO()));

        // Act & Assert
        webTestClient.post().uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SolicitudDetallesDTO.class)
                .consumeWith(response -> {
                    SolicitudDetallesDTO responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assert responseBody.getDocumentoIdentidad().equals("1234567890");
                });
    }

    @Test
    void debeRetornarBadRequest_cuandoUsuarioNoExiste() {
        // Arrange
        SolicitudDTO requestDTO = createValidSolicitudRequestDTO();

        // Simula el comportamiento del servicio: debe lanzar una excepción
        when(solicitudService.createSolicitud(any(Solicitud.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("El usuario con el documento de identidad proporcionado no existe.")));

        // Act & Assert
        webTestClient.post().uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isBadRequest() // 400 Bad Request
                .expectBody(String.class)
                .isEqualTo("El usuario con el documento de identidad proporcionado no existe.");
    }


    @Test
    void debeRetornarNotFound_cuandoTipoPrestamoNoExiste() {
        // Arrange
        SolicitudDTO requestDTO = createValidSolicitudRequestDTO();

        // Simula que el servicio lanza una excepción porque el tipo de préstamo no se encuentra
        when(solicitudService.createSolicitud(any(Solicitud.class)))
                .thenReturn(Mono.error(new IllegalStateException("El tipo de préstamo no fue encontrado.")));

        // Act & Assert
        webTestClient.post().uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isNotFound() // Espera un código de estado 404
                .expectBody(String.class)
                .isEqualTo("El tipo de préstamo no fue encontrado.");
    }


    @Test
    void debeRetornarNotFound_cuandoEstadoNoExiste() {
        // Arrange
        SolicitudDTO requestDTO = createValidSolicitudRequestDTO();

        // Simula que el servicio lanza una excepción porque el estado no se encuentra
        when(solicitudService.createSolicitud(any(Solicitud.class)))
                .thenReturn(Mono.error(new IllegalStateException("El estado no fue encontrado.")));

        // Act & Assert
        webTestClient.post().uri("/api/v1/solicitud") // URI corregida
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isNotFound() // Espera un código de estado 404
                .expectBody(String.class)
                .isEqualTo("El estado no fue encontrado.");
    }


}
