package pe.edu.upeu.config;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import pe.edu.upeu.entity.*;
import pe.edu.upeu.repository.*;

@ApplicationScoped
public class DataInitializer {

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

    @Transactional
    void onStart(@Observes StartupEvent event) {

        Rol adminRol = crearRolSiNoExiste(
                "ROLE_ADMIN",
                "Administrador con acceso total"
        );

        Rol userRol = crearRolSiNoExiste(
                "ROLE_USER",
                "Usuario con acceso limitado"
        );

        Usuario admin = crearUsuarioSiNoExiste(
                "admin",
                "admin123",
                "ROLE_ADMIN"
        );

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
                .find("rol.id = ?1 and permiso.id = ?2", adminRol.id, permiso.id)
                .firstResult() != null;

        if (!asignado) {
            RolPermiso rolPermiso = new RolPermiso();
            rolPermiso.rol = adminRol;
            rolPermiso.permiso = permiso;
            rolPermisoRepository.persist(rolPermiso);
        }
    }
}