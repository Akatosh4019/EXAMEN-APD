package pe.edu.upeu.dto;

public class LoginResponse {
    public String token;
    public String rol;
    public Long idcliente;

    public LoginResponse(String token, String rol) {
        this.token = token;
        this.rol = rol;
    }

    public LoginResponse(String token, String rol, Long idcliente) {
        this.token = token;
        this.rol = rol;
        this.idcliente = idcliente;
    }
}
