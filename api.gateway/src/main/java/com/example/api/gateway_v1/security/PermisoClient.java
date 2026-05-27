package com.example.api.gateway_v1.security;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class PermisoClient {

    private final WebClient webClient;

    public PermisoClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://ms-auth:8084").build();
    }

    public Mono<List<String>> obtenerPermisosPorRol(String rol) {
        return webClient.get()
                .uri("/auth/seguridad/roles/{rol}/permisos", rol)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {});
    }
}