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
}