package com.crediya.solicitudes.request_service.infraestructure.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AutenticacionWebClient {

    private final WebClient autenticacionWebClient;

    public AutenticacionWebClient(WebClient autenticacionWebClient) {
        this.autenticacionWebClient = autenticacionWebClient;
    }

    public Mono<Boolean> validarUsuario(String documentoIdentidad) {
        return autenticacionWebClient.get()
                // Este endpoint debe existir en tu microservicio de autenticación.
                .uri("/api/v1/usuarios/existe/{documentoIdentidad}", documentoIdentidad)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(e -> Mono.just(false));
    }
}

