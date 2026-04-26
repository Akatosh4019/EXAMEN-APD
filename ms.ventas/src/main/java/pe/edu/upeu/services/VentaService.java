package pe.edu.upeu.services;

import pe.edu.upeu.entity.Venta;
import java.util.List;

public interface VentaService {

    Venta create(Venta venta);

    List<Venta> findAll();

    Venta findById(Long id);

    Venta update(Long id, Venta venta);

    void delete(Long id);
}