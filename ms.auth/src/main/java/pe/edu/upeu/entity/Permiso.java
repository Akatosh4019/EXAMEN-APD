package pe.edu.upeu.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "permisos")
public class Permiso extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true, length = 80)
    public String nombre;

    @Column(nullable = false, length = 20)
    public String metodo;

    @Column(nullable = false, length = 120)
    public String endpoint;

    @Column(nullable = false)
    public Boolean estado = true;
}