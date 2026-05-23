package pe.edu.upeu.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import pe.edu.upeu.entity.Permiso;

@ApplicationScoped
public class PermisoRepository implements PanacheRepository<Permiso> {
}