package pe.edu.upeu.serviceImpl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import pe.edu.upeu.entity.Venta;
import pe.edu.upeu.repository.VentaRepository;
import pe.edu.upeu.services.VentaService;
import pe.edu.upeu.errors.*;

import java.util.List;

// 🔥 REST CLIENTS
import org.eclipse.microprofile.rest.client.inject.RestClient;
import pe.edu.upeu.client.ProductoClient;
import pe.edu.upeu.client.ClienteClient;

// 🔥 DTOs
import pe.edu.upeu.dto.ProductoDTO;
import pe.edu.upeu.dto.ClienteDTO;

@ApplicationScoped
public class VentaServiceImpl implements VentaService {

    @Inject
    VentaRepository repository;

    // 🔥 CLIENTE PRODUCTO
    @Inject
    @RestClient
    ProductoClient productoClient;

    // 🔥 CLIENTE CLIENTE
    @Inject
    @RestClient
    ClienteClient clienteClient;

    @Override
    @Transactional
    public Venta create(Venta venta) {

        // ✔ Validación básica
        if (venta.getCantidad() <= 0) {
            throw new BadRequestException("La cantidad debe ser mayor a 0");
        }

        // 🔥 VALIDAR CLIENTE
        ClienteDTO cliente = clienteClient.buscarClientePorId(venta.getIdcliente());

        if (cliente == null) {
            throw new NotFoundException("Cliente no existe");
        }

        if (!"A".equals(cliente.estado)) {
            throw new BadRequestException("Cliente inactivo");
        }

        // 🔥 VALIDAR PRODUCTO
        ProductoDTO producto = productoClient.buscarProductoPorId(venta.getIdproducto());

        if (producto == null) {
            throw new NotFoundException("Producto no existe");
        }

        if (!"A".equals(producto.estado)) {
            throw new BadRequestException("Producto inactivo");
        }

        // 🔥 VALIDAR STOCK REAL
        if (producto.stock < venta.getCantidad()) {
            throw new BadRequestException("No hay suficiente stock");
        }

        // 🔥 DESCONTAR STOCK (CLAVE 🔥)
        productoClient.descontarStock(
                venta.getIdproducto(),
                venta.getCantidad()
        );

        // 🔥 CALCULAR TOTAL AUTOMÁTICO
        double total = producto.precio * venta.getCantidad();
        venta.setTotal(total);

        // ✔ Guardar venta
        repository.persist(venta);
        return venta;
    }

    @Override
    public List<Venta> findAll() {
        return repository.listAll();
    }

    @Override
    public Venta findById(Long id) {
        Venta v = repository.findById(id);
        if (v == null) {
            throw new NotFoundException("Venta no encontrada con id: " + id);
        }
        return v;
    }

    @Override
    @Transactional
    public Venta update(Long id, Venta venta) {
        Venta entity = repository.findById(id);

        if (entity == null) {
            throw new NotFoundException("Venta no encontrada con id: " + id);
        }

        entity.setIdcliente(venta.getIdcliente());
        entity.setIdproducto(venta.getIdproducto());
        entity.setCantidad(venta.getCantidad());
        entity.setTotal(venta.getTotal());
        entity.setEstado(venta.getEstado());

        return entity;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.deleteById(id)) {
            throw new NotFoundException("Venta no encontrada con id: " + id);
        }
    }
}