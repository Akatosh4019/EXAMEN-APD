package pe.edu.upeu.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import pe.edu.upeu.entity.Usuario;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepository<Usuario> {

    public Usuario findByUsername(String username) {
        return find("username", username).firstResult();
    }
}