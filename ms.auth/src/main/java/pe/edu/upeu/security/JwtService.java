package pe.edu.upeu.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class JwtService {

    private static final String SECRET = "MiClaveSuperSecretaParaJwtDebeTenerMinimo32Caracteres123456";

    public String generarToken(String username, String rol) {
        return Jwt.issuer("ms-auth")
                .subject(username)
                .groups(Set.of(rol))
                .expiresIn(Duration.ofHours(1))
                .signWithSecret(SECRET);
    }
}