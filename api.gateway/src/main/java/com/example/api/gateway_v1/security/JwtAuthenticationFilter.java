package com.example.api.gateway_v1.security;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PermisoClient permisoClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        if (path.equals("/api/auth/login")
                || path.equals("/auth/login")
                || path.equals("/api/auth/register-cliente")
                || path.equals("/auth/register-cliente")) {
            return chain.filter(exchange);
        }

        if (path.startsWith("/api/auth/seguridad/roles/") && path.endsWith("/permisos")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            String token = authHeader.substring(7);
            Claims claims = jwtUtil.validarToken(token);

            String username = claims.getSubject();
            Object idClienteClaim = claims.get("idcliente");

            String rol = claims.get("groups").toString()
                    .replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .trim();

            String metodo = exchange.getRequest().getMethod().name();

            return permisoClient.obtenerPermisosPorRol(rol)
                    .flatMap(permisos -> {

                        boolean permitido = validarPermiso(permisos, metodo, path);

                        if (!permitido) {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        }

                        ServerWebExchange modifiedExchange = exchange.mutate()
                                .request(builder -> builder
                                        .header("X-User-Name", username)
                                        .header("X-User-Role", rol)
                                        .header("X-Cliente-Id", idClienteClaim == null ? "" : idClienteClaim.toString())
                                )
                                .build();

                        return chain.filter(modifiedExchange);
                    })
                    .onErrorResume(error -> {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    });

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean validarPermiso(List<String> permisos, String metodo, String path) {

        if (permisos == null || permisos.isEmpty()) {
            return false;
        }

        return permisos.stream().anyMatch(permiso -> {
            String[] partes = permiso.split(" ", 2);

            if (partes.length < 2) {
                return false;
            }

            String metodoPermiso = partes[0].trim();
            String endpointPermiso = partes[1].trim();

            if (!metodo.equalsIgnoreCase(metodoPermiso)) {
                return false;
            }

            String pathSinApi = path.startsWith("/api/") ? path.substring(4) : path;

            if (endpointPermiso.endsWith("/**")) {
                String base = endpointPermiso.replace("/**", "");
                return path.equals(base)
                        || path.startsWith(base + "/")
                        || pathSinApi.equals(base)
                        || pathSinApi.startsWith(base + "/");
            }

            return path.equals(endpointPermiso) || pathSinApi.equals(endpointPermiso);
        });
    }
}
