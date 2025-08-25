package com.crediya.solicitudes.request_service.application;

import com.crediya.solicitudes.request_service.domain.model.Solicitud;
import com.crediya.solicitudes.request_service.domain.model.Estado;
import com.crediya.solicitudes.request_service.domain.repository.SolicitudRepositoryPort;
import com.crediya.solicitudes.request_service.domain.repository.EstadoRepositoryPort;
import com.crediya.solicitudes.request_service.infraestructure.client.AutenticacionWebClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock
    private SolicitudRepositoryPort solicitudRepositoryPort;

    @Mock
    private AutenticacionWebClient autenticacionWebClient;

    @Mock
    private EstadoRepositoryPort estadoRepositoryPort;

    @InjectMocks
    private SolicitudService solicitudService;

    private Solicitud solicitud;
    private Estado estadoPendiente;

    @BeforeEach
    void setUp() {
        // Inicializa los objetos de prueba antes de cada test
        solicitud = new Solicitud();
        solicitud.setDocumentoIdentidad("1047373307");
        solicitud.setMonto(new BigDecimal("1000000"));
        solicitud.setPlazo(12);

        estadoPendiente = new Estado();
        estadoPendiente.setId(UUID.randomUUID());
        estadoPendiente.setNombre("PENDIENTE_REVISION");
    }

    @Test
    void createSolicitud_withValidUser() {

        when(autenticacionWebClient.validarUsuario(anyString())).thenReturn(Mono.just(true));
        when(estadoRepositoryPort.findByNombre(anyString())).thenReturn(Mono.just(estadoPendiente));
        when(solicitudRepositoryPort.save(any(Solicitud.class))).thenReturn(Mono.just(solicitud));

        // Act & Assert
        StepVerifier.create(solicitudService.createSolicitud(solicitud))
                .expectNextMatches(savedSolicitud -> {
                    // Verificaciones
                    return savedSolicitud.getDocumentoIdentidad().equals("1047373307")
                            && savedSolicitud.getIdEstado().equals(estadoPendiente.getId());
                })
                .verifyComplete();

        // Verificar que los métodos de las dependencias fueron llamados
        verify(autenticacionWebClient, times(1)).validarUsuario(solicitud.getDocumentoIdentidad());
        verify(estadoRepositoryPort, times(1)).findByNombre("PENDIENTE_REVISION");
        verify(solicitudRepositoryPort, times(1)).save(solicitud);
    }

    @Test
    void createSolicitud_withNonExistentUser() {

        when(autenticacionWebClient.validarUsuario(anyString())).thenReturn(Mono.just(false));

        // Act & Assert
        StepVerifier.create(solicitudService.createSolicitud(solicitud))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("El documento proporcionado no existe, no puede continuar con su solicitud."))
                .verify();

        // Verificar que no se llamó a los métodos de guardado
        verify(solicitudRepositoryPort, never()).save(any(Solicitud.class));
        verify(estadoRepositoryPort, never()).findByNombre(anyString());
    }

    @Test
    void createSolicitud_withNotExistInitialStatus() {

        when(autenticacionWebClient.validarUsuario(anyString())).thenReturn(Mono.just(true));
        when(estadoRepositoryPort.findByNombre(anyString())).thenReturn(Mono.empty()); // Simula que el estado no se encuentra

        // Act & Assert
        StepVerifier.create(solicitudService.createSolicitud(solicitud))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalStateException &&
                                throwable.getMessage().equals("El estado inicial 'PENDIENTE_REVISION' no se encontró."))
                .verify();

        // Verificar que no se llamó al metodo de guardado
        verify(solicitudRepositoryPort, never()).save(any(Solicitud.class));
        verify(autenticacionWebClient, times(1)).validarUsuario(anyString());
        verify(estadoRepositoryPort, times(1)).findByNombre(anyString());
    }


    private Solicitud createValidSolicitud() {
        return Solicitud.builder()
                .id(UUID.randomUUID())
                .documentoIdentidad("1234567890")
                .monto(new BigDecimal("1000000"))
                .plazo(12)
                .build();
    }




    @Test
    void debeHacerRollbackCuandoOcurreUnErrorEnElServicio() {
        // Arrange
        Solicitud solicitud = createValidSolicitud();

        // Simula que el repositorio guarda la solicitud exitosamente
        when(solicitudRepositoryPort.save(any(Solicitud.class))).thenReturn(Mono.just(solicitud));

        // Simula una operación que falla después de guardar la solicitud
        when(solicitudRepositoryPort.findById(any(UUID.class))).thenReturn(Mono.error(new RuntimeException("Error simulado para el rollback")));

        // Act & Assert
        StepVerifier.create(solicitudService.createSolicitudYComprobar(solicitud))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Error simulado para el rollback"))
                .verify();
    }
}
