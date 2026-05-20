package pe.edu.upeu.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true, length = 50)
    public String username;

    @Column(nullable = false, length = 100)
    public String password;

    @Column(nullable = false, length = 20)
    public String rol;

    @Column(nullable = false)
    public Boolean estado = true;
}