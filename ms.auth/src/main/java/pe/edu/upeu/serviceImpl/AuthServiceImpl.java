package pe.edu.upeu.serviceImpl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import org.jboss.logging.Logger;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import pe.edu.upeu.client.ClienteClient;
import pe.edu.upeu.dto.ClienteDTO;
import pe.edu.upeu.dto.LoginRequest;
import pe.edu.upeu.dto.LoginResponse;
import pe.edu.upeu.dto.RegistroClienteRequest;
import pe.edu.upeu.dto.RegistroClienteResponse;
import pe.edu.upeu.entity.Rol;
import pe.edu.upeu.entity.Usuario;
import pe.edu.upeu.entity.UsuarioRol;
import pe.edu.upeu.errors.BadRequestException;
import pe.edu.upeu.errors.ConflictException;
import pe.edu.upeu.repository.RolRepository;
import pe.edu.upeu.repository.UsuarioRepository;
import pe.edu.upeu.repository.UsuarioRolRepository;
import pe.edu.upeu.security.JwtService;
import pe.edu.upeu.services.AuthService;

import java.util.List;

@ApplicationScoped
public class AuthServiceImpl implements AuthService {

    private static final Logger LOG = Logger.getLogger(AuthServiceImpl.class);
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_CORREO = "admin@saga.local";

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    UsuarioRolRepository usuarioRolRepository;

    @Inject
    RolRepository rolRepository;

    @Inject
    JwtService jwtService;

    @Inject
    @RestClient
    ClienteClient clienteClient;

    @Override
    @Transactional
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

        asegurarClienteAdmin(usuario);

        List<UsuarioRol> rolesUsuario = usuarioRolRepository.findByUsuarioId(usuario.id);

        if (rolesUsuario == null || rolesUsuario.isEmpty()) {
            throw new RuntimeException("El usuario no tiene roles asignados");
        }

        String rolPrincipal = rolesUsuario.get(0).rol.nombre;

        String token = jwtService.generarToken(usuario.username, rolPrincipal, usuario.idcliente);

        return new LoginResponse(token, rolPrincipal, usuario.idcliente);
    }

    @Override
    @Transactional
    public RegistroClienteResponse registrarCliente(RegistroClienteRequest request) {
        validarRegistroCliente(request);

        if (usuarioRepository.findByUsername(request.username) != null) {
            throw new ConflictException("El username ya esta registrado");
        }

        Rol rolCliente = rolRepository.findByNombre("ROLE_CLIENTE");
        if (rolCliente == null) {
            throw new BadRequestException("El rol ROLE_CLIENTE no existe");
        }

        ClienteDTO cliente = new ClienteDTO();
        cliente.nombres = request.nombres;
        cliente.apellidos = request.apellidos;
        cliente.correo = request.correo;
        cliente.telefono = request.telefono;
        cliente.estado = "A";

        ClienteDTO clienteCreado = clienteClient.crearCliente(cliente);

        Usuario usuario = new Usuario();
        usuario.username = request.username;
        usuario.password = request.password;
        usuario.rol = "ROLE_CLIENTE";
        usuario.idcliente = clienteCreado.idcliente;
        usuario.estado = true;
        usuarioRepository.persist(usuario);

        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.usuario = usuario;
        usuarioRol.rol = rolCliente;
        usuarioRolRepository.persist(usuarioRol);

        return new RegistroClienteResponse(
                "Cliente registrado correctamente",
                usuario.id,
                clienteCreado.idcliente,
                usuario.username,
                "ROLE_CLIENTE"
        );
    }

    private void validarRegistroCliente(RegistroClienteRequest request) {
        if (request == null) {
            throw new BadRequestException("La solicitud es obligatoria");
        }
        if (esVacio(request.username)) {
            throw new BadRequestException("El username es obligatorio");
        }
        if (esVacio(request.password)) {
            throw new BadRequestException("La password es obligatoria");
        }
        if (esVacio(request.nombres)) {
            throw new BadRequestException("Los nombres son obligatorios");
        }
        if (esVacio(request.apellidos)) {
            throw new BadRequestException("Los apellidos son obligatorios");
        }
        if (esVacio(request.correo)) {
            throw new BadRequestException("El correo es obligatorio");
        }
        if (esVacio(request.telefono)) {
            throw new BadRequestException("El telefono es obligatorio");
        }
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private void asegurarClienteAdmin(Usuario usuario) {
        if (usuario == null || usuario.idcliente != null || !ADMIN_USERNAME.equals(usuario.username)) {
            return;
        }

        try {
            ClienteDTO clienteAdmin = obtenerOCrearClienteAdmin();
            usuario.idcliente = clienteAdmin.idcliente;
            LOG.infof("Usuario admin asociado al cliente %d", usuario.idcliente);
        } catch (Exception e) {
            LOG.warnf("No se pudo asociar el usuario admin a un cliente: %s", e.getMessage());
        }
    }

    private ClienteDTO obtenerOCrearClienteAdmin() {
        try {
            return clienteClient.buscarClientePorCorreo(ADMIN_CORREO);
        } catch (WebApplicationException e) {
            if (e.getResponse() == null || e.getResponse().getStatus() != 404) {
                throw e;
            }
        }

        ClienteDTO cliente = new ClienteDTO();
        cliente.nombres = "Administrador";
        cliente.apellidos = "General";
        cliente.correo = ADMIN_CORREO;
        cliente.telefono = "999999999";
        cliente.estado = "A";
        return clienteClient.crearCliente(cliente);
    }
}
