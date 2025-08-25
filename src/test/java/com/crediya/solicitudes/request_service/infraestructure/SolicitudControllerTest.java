package com.crediya.solicitudes.request_service.infraestructure;

import com.crediya.solicitudes.request_service.application.SolicitudService;
import com.crediya.solicitudes.request_service.infraestructure.controllers.SolicitudController;
import com.crediya.solicitudes.request_service.infraestructure.dto.SolicitudRequestDTO;
import com.crediya.solicitudes.request_service.infraestructure.dto.SolicitudResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@WebFluxTest(SolicitudController.class)
class SolicitudControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SolicitudService solicitudService;

    @Autowired
    private ObjectMapper objectMapper;

    private SolicitudRequestDTO solicitudRequestDTO;
    private SolicitudResponseDTO solicitudResponseDTO;

    @BeforeEach
    void setUp() {
        setupMockObjects();
    }

    @Test
    void createSolicitud_WhenValidRequest_ShouldReturnCreatedSolicitud() throws Exception {
        // Given
        when(solicitudService.procesarSolicitudCompleta(any(SolicitudRequestDTO.class)))
                .thenReturn(Mono.just(solicitudResponseDTO));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(solicitudRequestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(SolicitudResponseDTO.class)
                .value(response -> {
                    assert response.getId().equals(solicitudResponseDTO.getId());
                    assert response.getEstado().equals("PENDIENTE_REVISION");
                    assert response.getTipoPrestamo().equals("PERSONAL");
                    assert response.getMonto().equals(BigDecimal.valueOf(10000));
                    assert response.getPlazo() == 12;
                    assert response.getDocumentoIdentidad().equals("12345678");
                });

        verify(solicitudService).procesarSolicitudCompleta(any(SolicitudRequestDTO.class));
    }



    @Test
    void createSolicitud_WhenValidRequestWithDifferentTipoPrestamo_ShouldWork() throws Exception {
        // Given
        solicitudRequestDTO.setTipoPrestamo("HIPOTECARIO");

        SolicitudResponseDTO hipotecarioResponse = createSolicitudResponseDTO();
        hipotecarioResponse.setTipoPrestamo("HIPOTECARIO");

        when(solicitudService.procesarSolicitudCompleta(any(SolicitudRequestDTO.class)))
                .thenReturn(Mono.just(hipotecarioResponse));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(solicitudRequestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SolicitudResponseDTO.class)
                .value(response -> {
                    assert response.getTipoPrestamo().equals("HIPOTECARIO");
                });

        verify(solicitudService).procesarSolicitudCompleta(any(SolicitudRequestDTO.class));
    }

    @Test
    void createSolicitud_WhenLargeMontoValue_ShouldWork() throws Exception {
        // Given
        solicitudRequestDTO.setMonto(BigDecimal.valueOf(100000));

        SolicitudResponseDTO largeAmountResponse = createSolicitudResponseDTO();
        largeAmountResponse.setMonto(BigDecimal.valueOf(100000));

        when(solicitudService.procesarSolicitudCompleta(any(SolicitudRequestDTO.class)))
                .thenReturn(Mono.just(largeAmountResponse));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(solicitudRequestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SolicitudResponseDTO.class)
                .value(response -> {
                    assert response.getMonto().equals(BigDecimal.valueOf(100000));
                });

        verify(solicitudService).procesarSolicitudCompleta(any(SolicitudRequestDTO.class));
    }

    @Test
    void createSolicitud_WhenMaxPlazo_ShouldWork() throws Exception {
        // Given
        solicitudRequestDTO.setPlazo(60);

        SolicitudResponseDTO longTermResponse = createSolicitudResponseDTO();
        longTermResponse.setPlazo(60);

        when(solicitudService.procesarSolicitudCompleta(any(SolicitudRequestDTO.class)))
                .thenReturn(Mono.just(longTermResponse));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(solicitudRequestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SolicitudResponseDTO.class)
                .value(response -> {
                    assert response.getPlazo() == 60;
                });

        verify(solicitudService).procesarSolicitudCompleta(any(SolicitudRequestDTO.class));
    }

    @Test
    void createSolicitud_WhenServiceProcessingTakesTooLong_ShouldEventuallyRespond() throws Exception {
        // Given - Simula un procesamiento que toma tiempo pero eventualmente responde
        when(solicitudService.procesarSolicitudCompleta(any(SolicitudRequestDTO.class)))
                .thenReturn(Mono.just(solicitudResponseDTO).delayElement(java.time.Duration.ofMillis(100)));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(solicitudRequestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SolicitudResponseDTO.class);

        verify(solicitudService).procesarSolicitudCompleta(any(SolicitudRequestDTO.class));
    }

    // Helper methods
    private void setupMockObjects() {
        solicitudRequestDTO = createSolicitudRequestDTO();
        solicitudResponseDTO = createSolicitudResponseDTO();
    }

    private SolicitudRequestDTO createSolicitudRequestDTO() {
        SolicitudRequestDTO request = new SolicitudRequestDTO();
        request.setEmail("test@example.com");
        request.setDocumentoIdentidad("12345678");

        request.setTipoPrestamo("PERSONAL");
        request.setMonto(BigDecimal.valueOf(10000));
        request.setPlazo(12);
        return request;
    }

    private SolicitudResponseDTO createSolicitudResponseDTO() {
        SolicitudResponseDTO response = new SolicitudResponseDTO();
        response.setId(UUID.randomUUID());
        response.setEstado("PENDIENTE_REVISION");
        response.setTipoPrestamo("PERSONAL");
        response.setMonto(BigDecimal.valueOf(10000));
        response.setPlazo(12);
        response.setDocumentoIdentidad("12345678");
        return response;
    }
}