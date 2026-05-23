package pe.edu.upeu.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Rol extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true, length = 50)
    public String nombre;

    @Column(length = 150)
    public String descripcion;

    @Column(nullable = false)
    public Boolean estado = true;
}