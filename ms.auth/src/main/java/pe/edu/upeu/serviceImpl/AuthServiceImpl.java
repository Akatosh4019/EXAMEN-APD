package pe.edu.upeu.serviceImpl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pe.edu.upeu.dto.LoginRequest;
import pe.edu.upeu.dto.LoginResponse;
import pe.edu.upeu.entity.Usuario;
import pe.edu.upeu.repository.UsuarioRepository;
import pe.edu.upeu.services.AuthService;

@ApplicationScoped
public class AuthServiceImpl implements AuthService {

    @Inject
    UsuarioRepository usuarioRepository;

    @Override
    public LoginResponse login(LoginRequest request) {

        Usuario usuario = usuarioRepository.findByUsername(request.username);

        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        if (!usuario.password.equals(request.password)) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        if (!usuario.estado) {
            throw new RuntimeException("Usuario inactivo");
        }

        String tokenTemporal = "TOKEN_DE_PRUEBA";

        return new LoginResponse(tokenTemporal, usuario.rol);
    }
}