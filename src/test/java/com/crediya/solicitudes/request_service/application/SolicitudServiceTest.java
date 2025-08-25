package com.crediya.solicitudes.request_service.application;

import com.crediya.solicitudes.request_service.domain.model.Solicitud;
import com.crediya.solicitudes.request_service.domain.model.Estado;
import com.crediya.solicitudes.request_service.domain.model.TipoPrestamo;
import com.crediya.solicitudes.request_service.domain.repository.SolicitudRepositoryPort;
import com.crediya.solicitudes.request_service.domain.repository.TipoPrestamoRepositoryPort;
import com.crediya.solicitudes.request_service.domain.repository.EstadoRepositoryPort;
import com.crediya.solicitudes.request_service.infraestructure.adapter.mappers.EstadoMapper;
import com.crediya.solicitudes.request_service.infraestructure.adapter.mappers.SolicitudMapper;
import com.crediya.solicitudes.request_service.infraestructure.adapter.mappers.TipoPrestamoMapper;
import com.crediya.solicitudes.request_service.infraestructure.client.AutenticacionWebClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock
    private SolicitudRepositoryPort solicitudRepositoryPort;

    @Mock
    private AutenticacionWebClient autenticacionWebClient;

    @Mock
    private EstadoRepositoryPort estadoRepositoryPort;

    @Mock
    private TipoPrestamoRepositoryPort tipoPrestamoRepositoryPort;

    @Mock
    private SolicitudMapper solicitudMapper;

    @Mock
    private EstadoMapper estadoMapper;

    @Mock
    private TipoPrestamoMapper tipoPrestamoMapper;

    private SolicitudService solicitudService;

    private Solicitud solicitudMock;
    private Estado estadoMock;
    private TipoPrestamo tipoPrestamoMock;

    @BeforeEach
    void setUp() {
        solicitudService = new SolicitudService(
                solicitudRepositoryPort,
                autenticacionWebClient,
                estadoRepositoryPort,
                tipoPrestamoRepositoryPort,
                solicitudMapper,
                estadoMapper,
                tipoPrestamoMapper
        );

        // Setup mock objects
        solicitudMock = createSolicitudMock();
        estadoMock = createEstadoMock();
        tipoPrestamoMock = createTipoPrestamoMock();
    }

    @Test
    void createSolicitud_WhenUserExistsAndEstadoExists_ShouldCreateSolicitud() {
        // Given
        when(autenticacionWebClient.comprobarEmail(anyString()))
                .thenReturn(Mono.just(true));
        when(estadoRepositoryPort.findByNombre("PENDIENTE_REVISION"))
                .thenReturn(Mono.just(estadoMock));
        when(solicitudRepositoryPort.save(any(Solicitud.class)))
                .thenReturn(Mono.just(solicitudMock));

        // When & Then
        StepVerifier.create(solicitudService.createSolicitud(solicitudMock))
                .expectNext(solicitudMock)
                .verifyComplete();

        verify(autenticacionWebClient).comprobarEmail(solicitudMock.getEmail());
        verify(estadoRepositoryPort).findByNombre("PENDIENTE_REVISION");
        verify(solicitudRepositoryPort).save(any(Solicitud.class));
    }

    @Test
    void createSolicitud_WhenUserDoesNotExist_ShouldThrowIllegalArgumentException() {
        // Given
        when(autenticacionWebClient.comprobarEmail(anyString()))
                .thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(solicitudService.createSolicitud(solicitudMock))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(autenticacionWebClient).comprobarEmail(solicitudMock.getEmail());
        verifyNoInteractions(estadoRepositoryPort);
        verifyNoInteractions(solicitudRepositoryPort);
    }

    @Test
    void createSolicitud_WhenUserExistsButEstadoNotFound_ShouldThrowIllegalStateException() {
        // Given
        when(autenticacionWebClient.comprobarEmail(anyString()))
                .thenReturn(Mono.just(true));
        when(estadoRepositoryPort.findByNombre("PENDIENTE_REVISION"))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(solicitudService.createSolicitud(solicitudMock))
                .expectError(IllegalStateException.class)
                .verify();

        verify(autenticacionWebClient).comprobarEmail(solicitudMock.getEmail());
        verify(estadoRepositoryPort).findByNombre("PENDIENTE_REVISION");
        verifyNoInteractions(solicitudRepositoryPort);
    }

    @Test
    void createSolicitud_WhenEmailCheckFails_ShouldPropagateError() {
        // Given
        RuntimeException emailError = new RuntimeException("Email service error");
        when(autenticacionWebClient.comprobarEmail(anyString()))
                .thenReturn(Mono.error(emailError));

        // When & Then
        StepVerifier.create(solicitudService.createSolicitud(solicitudMock))
                .expectError(RuntimeException.class)
                .verify();

        verify(autenticacionWebClient).comprobarEmail(solicitudMock.getEmail());
        verifyNoInteractions(estadoRepositoryPort);
        verifyNoInteractions(solicitudRepositoryPort);
    }

    @Test
    void createSolicitud_WhenSaveOperationFails_ShouldPropagateError() {
        // Given
        RuntimeException saveError = new RuntimeException("Database save error");
        when(autenticacionWebClient.comprobarEmail(anyString()))
                .thenReturn(Mono.just(true));
        when(estadoRepositoryPort.findByNombre("PENDIENTE_REVISION"))
                .thenReturn(Mono.just(estadoMock));
        when(solicitudRepositoryPort.save(any(Solicitud.class)))
                .thenReturn(Mono.error(saveError));

        // When & Then
        StepVerifier.create(solicitudService.createSolicitud(solicitudMock))
                .expectError(RuntimeException.class)
                .verify();

        verify(autenticacionWebClient).comprobarEmail(solicitudMock.getEmail());
        verify(estadoRepositoryPort).findByNombre("PENDIENTE_REVISION");
        verify(solicitudRepositoryPort).save(any(Solicitud.class));
    }

    @Test
    void getDetailsForResponse_WhenBothEstadoAndTipoPrestamoExist_ShouldReturnDTO() {
        // Given
        when(estadoRepositoryPort.findById(solicitudMock.getIdEstado()))
                .thenReturn(Mono.just(estadoMock));
        when(tipoPrestamoRepositoryPort.findById(solicitudMock.getIdTipoPrestamo()))
                .thenReturn(Mono.just(tipoPrestamoMock));

        // When & Then
        StepVerifier.create(solicitudService.getDetailsForResponse(solicitudMock))
                .assertNext(dto -> {
                    assert dto.getNombreEstado().equals(estadoMock.getNombre());
                    assert dto.getNombreTipoPrestamo().equals(tipoPrestamoMock.getNombre());
                })
                .verifyComplete();

        verify(estadoRepositoryPort).findById(solicitudMock.getIdEstado());
        verify(tipoPrestamoRepositoryPort).findById(solicitudMock.getIdTipoPrestamo());
    }


    @Test
    void findByNameEstado_WhenCalled_ShouldReturnEstado() {
        // Given
        String nombreEstado = "APROBADO";
        when(estadoRepositoryPort.findByNombre(nombreEstado))
                .thenReturn(Mono.just(estadoMock));

        // When & Then
        StepVerifier.create(solicitudService.findByNameEstado(nombreEstado))
                .expectNext(estadoMock)
                .verifyComplete();

        verify(estadoRepositoryPort).findByNombre(nombreEstado);
    }

    @Test
    void findByNameEstado_WhenNotFound_ShouldReturnEmpty() {
        // Given
        String nombreEstado = "ESTADO_INEXISTENTE";
        when(estadoRepositoryPort.findByNombre(nombreEstado))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(solicitudService.findByNameEstado(nombreEstado))
                .verifyComplete();

        verify(estadoRepositoryPort).findByNombre(nombreEstado);
    }

    @Test
    void findByNamePrestamo_WhenCalled_ShouldReturnTipoPrestamo() {
        // Given
        String nombreTipo = "PERSONAL";
        when(tipoPrestamoRepositoryPort.findByNombre(nombreTipo))
                .thenReturn(Mono.just(tipoPrestamoMock));

        // When & Then
        StepVerifier.create(solicitudService.findByNamePrestamo(nombreTipo))
                .expectNext(tipoPrestamoMock)
                .verifyComplete();

        verify(tipoPrestamoRepositoryPort).findByNombre(nombreTipo);
    }

    @Test
    void findByNamePrestamo_WhenNotFound_ShouldReturnEmpty() {
        // Given
        String nombreTipo = "TIPO_INEXISTENTE";
        when(tipoPrestamoRepositoryPort.findByNombre(nombreTipo))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(solicitudService.findByNamePrestamo(nombreTipo))
                .verifyComplete();

        verify(tipoPrestamoRepositoryPort).findByNombre(nombreTipo);
    }

    // Helper methods to create mock objects
    private Solicitud createSolicitudMock() {
        Solicitud solicitud = new Solicitud();
        solicitud.setId(UUID.randomUUID());
        solicitud.setEmail("test@example.com");
        solicitud.setDocumentoIdentidad("12345678");
        solicitud.setIdEstado(UUID.randomUUID());
        solicitud.setIdTipoPrestamo(UUID.randomUUID());
        solicitud.setMonto(BigDecimal.valueOf(10000));
        solicitud.setPlazo(12);
        return solicitud;
    }

    private Estado createEstadoMock() {
        Estado estado = new Estado();
        estado.setId(UUID.randomUUID());
        estado.setNombre("PENDIENTE_REVISION");
        estado.setDescripcion("Estado inicial de la solicitud");
        return estado;
    }

    private TipoPrestamo createTipoPrestamoMock() {
        TipoPrestamo tipoPrestamo = new TipoPrestamo();
        tipoPrestamo.setId(UUID.randomUUID());
        tipoPrestamo.setNombre("PERSONAL");
        tipoPrestamo.setMontoMaximo(new BigDecimal("1500000"));
        return tipoPrestamo;
    }
}