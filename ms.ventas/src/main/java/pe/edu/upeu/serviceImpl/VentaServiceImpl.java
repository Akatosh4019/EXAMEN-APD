package pe.edu.upeu.serviceImpl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import pe.edu.upeu.entity.Venta;
import pe.edu.upeu.repository.VentaRepository;
import pe.edu.upeu.services.VentaService;

import pe.edu.upeu.errors.*;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import pe.edu.upeu.client.ProductoClient;
import pe.edu.upeu.client.ClienteClient;

import pe.edu.upeu.dto.ProductoDTO;
import pe.edu.upeu.dto.ClienteDTO;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;

@ApplicationScoped
public class VentaServiceImpl implements VentaService {

    @Inject
    VentaRepository repository;

    @Inject
    @RestClient
    ProductoClient productoClient;

    @Inject
    @RestClient
    ClienteClient clienteClient;

    @CircuitBreaker(
            requestVolumeThreshold = 4,
            failureRatio = 0.5,
            delay = 5000
    )
    @Fallback(fallbackMethod = "fallbackProducto")
    @Timeout(3000)
    public ProductoDTO obtenerProducto(Long id) {

        return productoClient.buscarProductoPorId(id);
    }

    public ProductoDTO fallbackProducto(Long id) {

        ProductoDTO p = new ProductoDTO();

        p.nombre = "Servicio producto no disponible";
        p.stock = 0;
        p.precio = 0.0;
        p.estado = "I";

        return p;
    }

    @CircuitBreaker(
            requestVolumeThreshold = 4,
            failureRatio = 0.5,
            delay = 5000
    )
    @Fallback(fallbackMethod = "fallbackCliente")
    @Timeout(3000)
    public ClienteDTO obtenerCliente(Long id) {

        return clienteClient.buscarClientePorId(id);
    }

    public ClienteDTO fallbackCliente(Long id) {

        ClienteDTO c = new ClienteDTO();

        c.estado = "I";

        return c;
    }

    @Override
    @Transactional
    public Venta create(Venta venta) {

        if (venta.getCantidad() <= 0) {
            throw new BadRequestException("La cantidad debe ser mayor a 0");
        }

        ClienteDTO cliente = obtenerCliente(
                venta.getIdcliente()
        );

        if (cliente == null) {
            throw new NotFoundException("Cliente no existe");
        }

        if (!"A".equals(cliente.estado)) {
            throw new BadRequestException("Cliente inactivo o servicio no disponible");
        }

        ProductoDTO producto = obtenerProducto(
                venta.getIdproducto()
        );

        if (producto == null) {
            throw new NotFoundException("Producto no existe");
        }

        if (!"A".equals(producto.estado)) {
            throw new BadRequestException("Producto inactivo o servicio no disponible");
        }

        if (producto.stock < venta.getCantidad()) {
            throw new BadRequestException("No hay suficiente stock");
        }

        productoClient.descontarStock(
                venta.getIdproducto(),
                venta.getCantidad()
        );

        double total = producto.precio * venta.getCantidad();

        venta.setTotal(total);

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
            throw new NotFoundException(
                    "Venta no encontrada con id: " + id
            );
        }

        return v;
    }

    @Override
    @Transactional
    public Venta update(Long id, Venta venta) {

        Venta entity = repository.findById(id);

        if (entity == null) {
            throw new NotFoundException(
                    "Venta no encontrada con id: " + id
            );
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

            throw new NotFoundException(
                    "Venta no encontrada con id: " + id
            );
        }
    }
}