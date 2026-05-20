package pe.edu.upeu.dto;

public class LoginResponse {
    public String token;
    public String rol;

    public LoginResponse(String token, String rol) {
        this.token = token;
        this.rol = rol;
    }
}