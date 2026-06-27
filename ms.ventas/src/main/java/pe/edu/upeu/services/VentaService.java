package pe.edu.upeu.services;

import pe.edu.upeu.entity.Venta;
import pe.edu.upeu.dto.SagaVentaResponse;
import java.util.List;

public interface VentaService {

    Venta create(Venta venta);

    SagaVentaResponse realizarVentaSaga(Venta venta);

    List<Venta> findAll();

    List<Venta> findByCliente(Long idcliente);

    Venta findById(Long id);

    Venta update(Long id, Venta venta);

    void delete(Long id);
}
