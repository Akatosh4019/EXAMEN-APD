package pe.edu.upeu.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "usuario_roles")
public class UsuarioRol extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    public Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    public Rol rol;
}