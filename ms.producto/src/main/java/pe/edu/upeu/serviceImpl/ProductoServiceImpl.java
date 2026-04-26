package pe.edu.upeu.serviceImpl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import pe.edu.upeu.entity.Producto;
import pe.edu.upeu.repository.ProductoRepository;
import pe.edu.upeu.services.ProductoService;
import pe.edu.upeu.errors.*;

import java.util.List;

@ApplicationScoped
public class ProductoServiceImpl implements ProductoService {

    @Inject
    ProductoRepository repository;

    @Override
    @Transactional
    public Producto create(Producto producto) {

        // 🔥 VALIDAR DUPLICADO
        if (repository.find("nombre", producto.getNombre()).firstResult() != null) {
            throw new ConflictException("El producto ya existe");
        }

        repository.persist(producto);
        return producto;
    }

    @Override
    @Transactional
    public Producto update(Long id, Producto producto) {

        Producto entity = repository.findById(id);

        if (entity == null) {
            throw new NotFoundException("Producto no encontrado con id: " + id);
        }

        entity.setNombre(producto.getNombre());
        entity.setPrecio(producto.getPrecio());
        entity.setStock(producto.getStock());
        entity.setEstado(producto.getEstado());

        return entity;
    }

    @Override
    @Transactional
    public void delete(Long id) {

        if (!repository.deleteById(id)) {
            throw new NotFoundException("Producto no encontrado con id: " + id);
        }
    }

    @Override
    public Producto findById(Long id) {

        Producto p = repository.findById(id);

        if (p == null) {
            throw new NotFoundException("Producto no encontrado con id: " + id);
        }

        return p;
    }

    @Override
    public List<Producto> findAll() {
        return repository.listAll();
    }

    // 🔥 DESCONTAR STOCK
    @Override
    @Transactional
    public Producto descontarStock(Long id, int cantidad) {

        Producto p = repository.findById(id);

        if (p == null) {
            throw new NotFoundException("Producto no encontrado con id: " + id);
        }

        if (p.getStock() < cantidad) {
            throw new BadRequestException("No hay stock suficiente");
        }

        p.setStock(p.getStock() - cantidad);

        return p;
    }

    // 🔥 DESACTIVAR PRODUCTO (NUEVO)
    @Override
    @Transactional
    public Producto desactivar(Long id) {

        Producto p = repository.findById(id);

        if (p == null) {
            throw new NotFoundException("Producto no encontrado con id: " + id);
        }

        p.setEstado('I');

        return p;
    }

    @Override
    @Transactional
    public Producto aumentarStock(Long id, int cantidad) {

        Producto p = repository.findById(id);

        if (p == null) {
            throw new NotFoundException("Producto no encontrado con id: " + id);
        }

        if (cantidad <= 0) {
            throw new BadRequestException("La cantidad debe ser mayor a 0");
        }

        p.setStock(p.getStock() + cantidad);

        return p;
    }

    @Override
    @Transactional
    public Producto activar(Long id) {

        Producto p = repository.findById(id);

        if (p == null) {
            throw new NotFoundException("Producto no encontrado con id: " + id);
        }

        p.setEstado('A'); // 🔥 Character

        return p;
    }
}