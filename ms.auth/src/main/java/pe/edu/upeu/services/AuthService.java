package pe.edu.upeu.services;

import pe.edu.upeu.dto.LoginRequest;
import pe.edu.upeu.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}