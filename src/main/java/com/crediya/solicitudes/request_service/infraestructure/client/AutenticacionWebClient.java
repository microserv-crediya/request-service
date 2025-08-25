package com.crediya.solicitudes.request_service.infraestructure.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AutenticacionWebClient {

    private final WebClient autenticacionWebClient;
    private final Logger log = LoggerFactory.getLogger(AutenticacionWebClient.class);


    public AutenticacionWebClient(WebClient autenticacionWebClient) {
        this.autenticacionWebClient = autenticacionWebClient;
    }

    public Mono<Boolean> validarUsuario(String documentoIdentidad) {
        log.info("=== INICIO validarUsuario ===");
        log.info("Documento a validar: {}", documentoIdentidad);

        String uri = "/api/v1/usuarios/existe/{documentoIdentidad}";
        log.info("URI a llamar: {}", uri);

        return autenticacionWebClient.get()
                .uri(uri, documentoIdentidad)
                .retrieve()
                .toEntity(String.class) // Cambiar temporalmente para ver
                .doOnNext(response -> {
                    log.info("Status Code: {}", response.getStatusCode());
                    log.info("Headers: {}", response.getHeaders());
                    log.info("Body: '{}'", response.getBody());
                })
                .map(response -> {
                    String body = response.getBody();
                    if (body == null) {
                        log.warn("Body es null");
                        return false;
                    }

                    boolean result = Boolean.parseBoolean(body.trim());
                    log.info("Resultado: {}", result);
                    return result;
                })
                .doOnError(error -> {    log.error("Error en WebClient: ", error);    })
                .onErrorResume(e -> {
                    log.warn("Capturando error y retornando false: {}", e.getMessage());
                    return Mono.just(false);
                })
                .doFinally(signal -> {  log.info("=== FIN validarUsuario - Signal: {} ===", signal);  });
    }

    public Mono<Boolean> comprobarEmail(String email) {
        log.info("=== INICIO validarEmail ===");
        log.info("*****Email a validar: {}", email);

        String uri = "/api/v1/usuarios/email/{email}";
        log.info("*****URI a llamar: {}", uri);

        return autenticacionWebClient.get()
                .uri(uri, email)
                .retrieve()
                .toEntity(String.class) // Cambiar temporalmente para ver
                .doOnNext(response -> {
                    log.info("Status Code: {}", response.getStatusCode());
                    log.info("Headers: {}", response.getHeaders());
                    log.info("Body: '{}'", response.getBody());
                })
                .map(response -> {
                    String body = response.getBody();
                    if (body == null) {
                        log.warn("Body es null");
                        return false;
                    }
                    boolean result = Boolean.parseBoolean(body.trim());
                    log.info("Resultado: {}", result);
                    return result;
                })
                .doOnError(error -> {
                    log.error("Error en WebClient: ", error);
                })
                .onErrorResume(e -> {
                    log.warn("Capturando error y retornando false: {}", e.getMessage());
                    return Mono.just(false);
                })
                .doFinally(signal -> {  log.info("=== FIN comprobarEmail - Signal: {} ===", signal);  });
    }
}

