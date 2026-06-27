package pe.edu.upeu.config;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import pe.edu.upeu.client.ClienteClient;
import pe.edu.upeu.dto.ClienteDTO;
import pe.edu.upeu.entity.*;
import pe.edu.upeu.repository.*;

@ApplicationScoped
public class DataInitializer {

    private static final Logger LOG = Logger.getLogger(DataInitializer.class);
    private static final String ADMIN_CORREO = "admin@saga.local";

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    RolRepository rolRepository;

    @Inject
    PermisoRepository permisoRepository;

    @Inject
    UsuarioRolRepository usuarioRolRepository;

    @Inject
    RolPermisoRepository rolPermisoRepository;

    @Inject
    @RestClient
    ClienteClient clienteClient;

    @Transactional
    void onStart(@Observes StartupEvent event) {

        Rol adminRol = crearRolSiNoExiste(
                "ROLE_ADMIN",
                "Administrador con acceso total"
        );

        Rol clienteRol = crearRolSiNoExiste(
                "ROLE_CLIENTE",
                "Cliente con acceso para consultar productos y comprar"
        );

        Usuario admin = crearUsuarioSiNoExiste(
                "admin",
                "admin123",
                "ROLE_ADMIN"
        );

        asociarClienteAdminSiEsPosible(admin);

        asignarRolSiNoExiste(admin, adminRol);

        crearPermisoYAsignarAdmin(adminRol, "LISTAR_PRODUCTOS", "GET", "/productos/**");
        crearPermisoYAsignarAdmin(adminRol, "CREAR_PRODUCTOS", "POST", "/productos/**");
        crearPermisoYAsignarAdmin(adminRol, "EDITAR_PRODUCTOS", "PUT", "/productos/**");
        crearPermisoYAsignarAdmin(adminRol, "ELIMINAR_PRODUCTOS", "DELETE", "/productos/**");

        crearPermisoYAsignarAdmin(adminRol, "LISTAR_CLIENTES", "GET", "/clientes/**");
        crearPermisoYAsignarAdmin(adminRol, "CREAR_CLIENTES", "POST", "/clientes/**");
        crearPermisoYAsignarAdmin(adminRol, "EDITAR_CLIENTES", "PUT", "/clientes/**");
        crearPermisoYAsignarAdmin(adminRol, "ELIMINAR_CLIENTES", "DELETE", "/clientes/**");

        crearPermisoYAsignarAdmin(adminRol, "LISTAR_VENTAS", "GET", "/ventas/**");
        crearPermisoYAsignarAdmin(adminRol, "CREAR_VENTAS", "POST", "/ventas/**");
        crearPermisoYAsignarAdmin(adminRol, "ELIMINAR_VENTAS", "DELETE", "/ventas/**");

        crearPermisoYAsignarAdmin(adminRol, "CREAR_ROLES", "POST", "/auth/seguridad/roles");
        crearPermisoYAsignarAdmin(adminRol, "CREAR_PERMISOS", "POST", "/auth/seguridad/permisos");
        crearPermisoYAsignarAdmin(adminRol, "CREAR_PERMISOS_DEFAULT", "POST", "/auth/seguridad/permisos/default");
        crearPermisoYAsignarAdmin(adminRol, "ASIGNAR_ROL_USUARIO", "POST", "/auth/seguridad/usuarios/asignar-rol");
        crearPermisoYAsignarAdmin(adminRol, "ASIGNAR_PERMISO_ROL", "POST", "/auth/seguridad/roles/asignar-permiso");
        crearPermisoYAsignarAdmin(adminRol, "VER_PERMISOS_ROL", "GET", "/auth/seguridad/roles/**");

        crearPermisoYAsignarRol(clienteRol, "CLIENTE_VER_PRODUCTOS", "GET", "/productos/**");
        crearPermisoYAsignarRol(clienteRol, "CLIENTE_COMPRAR", "POST", "/ventas/**");
        crearPermisoYAsignarRol(clienteRol, "CLIENTE_VER_MIS_VENTAS", "GET", "/ventas/mis-ventas");
    }

    private Rol crearRolSiNoExiste(String nombre, String descripcion) {
        Rol rol = rolRepository.findByNombre(nombre);

        if (rol == null) {
            rol = new Rol();
            rol.nombre = nombre;
            rol.descripcion = descripcion;
            rol.estado = true;
            rolRepository.persist(rol);
        }

        return rol;
    }

    private Usuario crearUsuarioSiNoExiste(String username, String password, String rolTexto) {
        Usuario usuario = usuarioRepository.findByUsername(username);

        if (usuario == null) {
            usuario = new Usuario();
            usuario.username = username;
            usuario.password = password;
            usuario.rol = rolTexto;
            usuario.estado = true;
            usuarioRepository.persist(usuario);
        }

        return usuario;
    }

    private void asociarClienteAdminSiEsPosible(Usuario admin) {
        if (admin.idcliente != null) {
            return;
        }

        try {
            ClienteDTO clienteAdmin = obtenerOCrearClienteAdmin();
            admin.idcliente = clienteAdmin.idcliente;
            LOG.infof("Usuario admin asociado al cliente %d", admin.idcliente);
        } catch (Exception e) {
            LOG.warnf("No se pudo crear/asociar el cliente del admin durante el arranque: %s", e.getMessage());
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

    private void asignarRolSiNoExiste(Usuario usuario, Rol rol) {
        boolean existe = usuarioRolRepository
                .find("usuario.id = ?1 and rol.id = ?2", usuario.id, rol.id)
                .firstResult() != null;

        if (!existe) {
            UsuarioRol usuarioRol = new UsuarioRol();
            usuarioRol.usuario = usuario;
            usuarioRol.rol = rol;
            usuarioRolRepository.persist(usuarioRol);
        }
    }

    private void crearPermisoYAsignarAdmin(Rol adminRol, String nombre, String metodo, String endpoint) {
        crearPermisoYAsignarRol(adminRol, nombre, metodo, endpoint);
    }

    private void crearPermisoYAsignarRol(Rol rol, String nombre, String metodo, String endpoint) {
        Permiso permiso = permisoRepository.find("nombre", nombre).firstResult();

        if (permiso == null) {
            permiso = new Permiso();
            permiso.nombre = nombre;
            permiso.metodo = metodo;
            permiso.endpoint = endpoint;
            permiso.estado = true;
            permisoRepository.persist(permiso);
        }

        boolean asignado = rolPermisoRepository
                .find("rol.id = ?1 and permiso.id = ?2", rol.id, permiso.id)
                .firstResult() != null;

        if (!asignado) {
            RolPermiso rolPermiso = new RolPermiso();
            rolPermiso.rol = rol;
            rolPermiso.permiso = permiso;
            rolPermisoRepository.persist(rolPermiso);
        }
    }
}
