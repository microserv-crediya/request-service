package com.crediya.solicitudes.request_service.infraestructure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class WebClientTest {

    @Autowired
    @Qualifier("autenticacionBaseWebClient")
    private WebClient webClient;

    @Test
    void testWebClientConfiguration() {
        assertNotNull(webClient);

        // Test directo
        String result = webClient.get()
                .uri("/api/v1/usuarios/existe/{doc}", "1047373307")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println("Resultado: " + result);
    }
}