package pe.edu.upeu.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import pe.edu.upeu.entity.RolPermiso;

import java.util.List;

@ApplicationScoped
public class RolPermisoRepository implements PanacheRepository<RolPermiso> {

    public List<RolPermiso> findByRolNombre(String nombreRol) {
        return list("rol.nombre", nombreRol);
    }
}