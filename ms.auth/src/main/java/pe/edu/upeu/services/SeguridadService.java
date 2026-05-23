package pe.edu.upeu.services;

import pe.edu.upeu.dto.AsignarPermisoRequest;
import pe.edu.upeu.dto.AsignarRolRequest;
import pe.edu.upeu.dto.PermisoRequest;
import pe.edu.upeu.dto.RolRequest;
import pe.edu.upeu.entity.Permiso;
import pe.edu.upeu.entity.Rol;

public interface SeguridadService {

    Rol crearRol(RolRequest request);

    Permiso crearPermiso(PermisoRequest request);

    String asignarRolAUsuario(AsignarRolRequest request);

    String asignarPermisoARol(AsignarPermisoRequest request);
}