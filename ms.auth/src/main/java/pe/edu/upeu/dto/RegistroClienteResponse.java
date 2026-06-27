package pe.edu.upeu.dto;

public class RegistroClienteResponse {
    public String mensaje;
    public Long idusuario;
    public Long idcliente;
    public String username;
    public String rol;

    public RegistroClienteResponse() {
    }

    public RegistroClienteResponse(String mensaje, Long idusuario, Long idcliente, String username, String rol) {
        this.mensaje = mensaje;
        this.idusuario = idusuario;
        this.idcliente = idcliente;
        this.username = username;
        this.rol = rol;
    }
}
