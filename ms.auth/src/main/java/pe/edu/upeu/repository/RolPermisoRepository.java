package pe.edu.upeu.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import pe.edu.upeu.entity.RolPermiso;

@ApplicationScoped
public class RolPermisoRepository implements PanacheRepository<RolPermiso> {
}