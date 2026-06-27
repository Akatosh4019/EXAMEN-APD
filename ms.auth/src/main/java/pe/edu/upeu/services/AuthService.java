package pe.edu.upeu.services;

import pe.edu.upeu.dto.LoginRequest;
import pe.edu.upeu.dto.LoginResponse;
import pe.edu.upeu.dto.RegistroClienteRequest;
import pe.edu.upeu.dto.RegistroClienteResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    RegistroClienteResponse registrarCliente(RegistroClienteRequest request);
}
