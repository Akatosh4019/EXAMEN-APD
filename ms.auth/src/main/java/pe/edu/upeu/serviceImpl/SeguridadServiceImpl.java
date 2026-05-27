package pe.edu.upeu.serviceImpl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import pe.edu.upeu.dto.AsignarPermisoRequest;
import pe.edu.upeu.dto.AsignarRolRequest;
import pe.edu.upeu.dto.PermisoRequest;
import pe.edu.upeu.dto.RolRequest;
import pe.edu.upeu.entity.*;
import pe.edu.upeu.repository.*;
import pe.edu.upeu.services.SeguridadService;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class SeguridadServiceImpl implements SeguridadService {

    @Inject
    RolRepository rolRepository;

    @Inject
    PermisoRepository permisoRepository;

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    UsuarioRolRepository usuarioRolRepository;

    @Inject
    RolPermisoRepository rolPermisoRepository;

    @Override
    @Transactional
    public Rol crearRol(RolRequest request) {
        Rol rol = new Rol();
        rol.nombre = request.nombre;
        rol.descripcion = request.descripcion;
        rol.estado = true;

        rolRepository.persist(rol);
        return rol;
    }

    @Override
    @Transactional
    public Permiso crearPermiso(PermisoRequest request) {
        Permiso permiso = new Permiso();
        permiso.nombre = request.nombre;
        permiso.metodo = request.metodo;
        permiso.endpoint = request.endpoint;
        permiso.estado = true;

        permisoRepository.persist(permiso);
        return permiso;
    }

    @Override
    @Transactional
    public String asignarRolAUsuario(AsignarRolRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId);
        Rol rol = rolRepository.findById(request.rolId);

        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        if (rol == null) {
            throw new RuntimeException("Rol no encontrado");
        }

        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.usuario = usuario;
        usuarioRol.rol = rol;

        usuarioRolRepository.persist(usuarioRol);

        return "Rol asignado correctamente al usuario";
    }

    @Override
    @Transactional
    public String asignarPermisoARol(AsignarPermisoRequest request) {
        Rol rol = rolRepository.findById(request.rolId);
        Permiso permiso = permisoRepository.findById(request.permisoId);

        if (rol == null) {
            throw new RuntimeException("Rol no encontrado");
        }

        if (permiso == null) {
            throw new RuntimeException("Permiso no encontrado");
        }

        RolPermiso rolPermiso = new RolPermiso();
        rolPermiso.rol = rol;
        rolPermiso.permiso = permiso;

        rolPermisoRepository.persist(rolPermiso);

        return "Permiso asignado correctamente al rol";
    }

    @Override
    @Transactional
    public String crearPermisosDefault() {

        crearPermisoSiNoExiste("LISTAR_PRODUCTOS", "GET", "/productos/**");
        crearPermisoSiNoExiste("CREAR_PRODUCTOS", "POST", "/productos/**");
        crearPermisoSiNoExiste("EDITAR_PRODUCTOS", "PUT", "/productos/**");
        crearPermisoSiNoExiste("ELIMINAR_PRODUCTOS", "DELETE", "/productos/**");

        crearPermisoSiNoExiste("LISTAR_CLIENTES", "GET", "/clientes/**");
        crearPermisoSiNoExiste("CREAR_CLIENTES", "POST", "/clientes/**");
        crearPermisoSiNoExiste("EDITAR_CLIENTES", "PUT", "/clientes/**");
        crearPermisoSiNoExiste("ELIMINAR_CLIENTES", "DELETE", "/clientes/**");

        crearPermisoSiNoExiste("LISTAR_VENTAS", "GET", "/ventas/**");
        crearPermisoSiNoExiste("CREAR_VENTAS", "POST", "/ventas/**");
        crearPermisoSiNoExiste("ELIMINAR_VENTAS", "DELETE", "/ventas/**");

        crearPermisoSiNoExiste("CREAR_ROLES", "POST", "/auth/seguridad/roles");
        crearPermisoSiNoExiste("CREAR_PERMISOS", "POST", "/auth/seguridad/permisos");
        crearPermisoSiNoExiste("CREAR_PERMISOS_DEFAULT", "POST", "/auth/seguridad/permisos/default");
        crearPermisoSiNoExiste("ASIGNAR_ROL_USUARIO", "POST", "/auth/seguridad/usuarios/asignar-rol");
        crearPermisoSiNoExiste("ASIGNAR_PERMISO_ROL", "POST", "/auth/seguridad/roles/asignar-permiso");
        crearPermisoSiNoExiste("VER_PERMISOS_ROL", "GET", "/auth/seguridad/roles/**/permisos");

        return "Permisos default creados correctamente";
    }

    @Override
    public List<String> listarPermisosPorRol(String nombreRol) {
        return rolPermisoRepository.findByRolNombre(nombreRol)
                .stream()
                .map(rp -> rp.permiso.metodo + " " + rp.permiso.endpoint)
                .collect(Collectors.toList());
    }

    private void crearPermisoSiNoExiste(String nombre, String metodo, String endpoint) {
        Permiso existe = permisoRepository.find("nombre", nombre).firstResult();

        if (existe == null) {
            Permiso permiso = new Permiso();
            permiso.nombre = nombre;
            permiso.metodo = metodo;
            permiso.endpoint = endpoint;
            permiso.estado = true;

            permisoRepository.persist(permiso);
        }
    }
}