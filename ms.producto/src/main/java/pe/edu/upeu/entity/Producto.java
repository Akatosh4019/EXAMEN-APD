package pe.edu.upeu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idproducto;

    private String nombre;

    private Double precio;

    private Integer stock;

    private Character estado;
}