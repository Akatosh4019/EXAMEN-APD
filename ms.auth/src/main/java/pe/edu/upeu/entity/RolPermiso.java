package pe.edu.upeu.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "rol_permisos")
public class RolPermiso extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    public Rol rol;

    @ManyToOne
    @JoinColumn(name = "permiso_id", nullable = false)
    public Permiso permiso;
}