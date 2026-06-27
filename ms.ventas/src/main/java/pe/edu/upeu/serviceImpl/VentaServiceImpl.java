package pe.edu.upeu.serviceImpl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import pe.edu.upeu.client.ClienteClient;
import pe.edu.upeu.client.ProductoClient;
import pe.edu.upeu.dto.ClienteDTO;
import pe.edu.upeu.dto.ProductoDTO;
import pe.edu.upeu.dto.SagaVentaResponse;
import pe.edu.upeu.entity.Venta;
import pe.edu.upeu.errors.BadRequestException;
import pe.edu.upeu.errors.ConflictException;
import pe.edu.upeu.errors.NotFoundException;
import pe.edu.upeu.repository.VentaRepository;
import pe.edu.upeu.services.VentaService;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class VentaServiceImpl implements VentaService {

    private static final Logger LOG = Logger.getLogger(VentaServiceImpl.class);

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
        return realizarVentaSaga(venta).venta;
    }

    @Override
    @Transactional
    public SagaVentaResponse realizarVentaSaga(Venta venta) {
        String sagaId = UUID.randomUUID().toString();
        boolean stockDescontado = false;

        LOG.infof("SAGA %s | INICIADA | cliente=%s producto=%s cantidad=%d",
                sagaId, venta.getIdcliente(), venta.getIdproducto(), venta.getCantidad());

        validarSolicitudVenta(venta);

        try {
            LOG.infof("SAGA %s | PASO 1 | Validando cliente", sagaId);
            ClienteDTO cliente = clienteClient.validarClienteActivo(venta.getIdcliente());
            validarClienteRespuesta(cliente);
            LOG.infof("SAGA %s | PASO 1 OK | Cliente activo", sagaId);

            LOG.infof("SAGA %s | PASO 2 | Validando producto y stock", sagaId);
            ProductoDTO producto = productoClient.validarProductoDisponible(venta.getIdproducto(), venta.getCantidad());
            validarProductoRespuesta(producto, venta.getCantidad());
            LOG.infof("SAGA %s | PASO 2 OK | Producto disponible stock=%d precio=%.2f",
                    sagaId, producto.stock, producto.precio);

            LOG.infof("SAGA %s | PASO 3 | Descontando stock", sagaId);
            productoClient.descontarStock(venta.getIdproducto(), venta.getCantidad());
            stockDescontado = true;
            LOG.infof("SAGA %s | PASO 3 OK | Stock descontado", sagaId);

            LOG.infof("SAGA %s | PASO 4 | Registrando venta", sagaId);
            venta.setTotal(producto.precio * venta.getCantidad());
            if (venta.getEstado() == null || venta.getEstado().isBlank()) {
                venta.setEstado("A");
            }
            repository.persistAndFlush(venta);
            LOG.infof("SAGA %s | COMPLETADA | venta=%d total=%.2f",
                    sagaId, venta.getIdventa(), venta.getTotal());

            return new SagaVentaResponse(sagaId, "COMPLETADA", "Venta registrada correctamente", venta);
        } catch (RuntimeException ex) {
            if (ex instanceof WebApplicationException) {
                LOG.warnf("SAGA %s | FALLIDA | %s", sagaId, mensajeSeguro(ex));
            } else {
                LOG.errorf(ex, "SAGA %s | FALLIDA | %s", sagaId, mensajeSeguro(ex));
            }

            if (stockDescontado) {
                compensarStock(sagaId, venta);
            }

            throw new ConflictException("Saga fallida: " + mensajeSeguro(ex));
        }
    }

    private void validarSolicitudVenta(Venta venta) {
        if (venta.getIdcliente() == null) {
            throw new BadRequestException("El cliente es obligatorio");
        }

        if (venta.getIdproducto() == null) {
            throw new BadRequestException("El producto es obligatorio");
        }

        if (venta.getCantidad() <= 0) {
            throw new BadRequestException("La cantidad debe ser mayor a 0");
        }
    }

    private void validarClienteRespuesta(ClienteDTO cliente) {
        if (cliente == null) {
            throw new NotFoundException("Cliente no existe");
        }

        if (!"A".equals(cliente.estado)) {
            throw new BadRequestException("Cliente inactivo o servicio no disponible");
        }
    }

    private void validarProductoRespuesta(ProductoDTO producto, int cantidad) {
        if (producto == null) {
            throw new NotFoundException("Producto no existe");
        }

        if (!"A".equals(producto.estado)) {
            throw new BadRequestException("Producto inactivo o servicio no disponible");
        }

        if (producto.stock < cantidad) {
            throw new BadRequestException("No hay suficiente stock");
        }
    }

    private void compensarStock(String sagaId, Venta venta) {
        try {
            LOG.warnf("SAGA %s | COMPENSACION | Restaurando stock producto=%d cantidad=%d",
                    sagaId, venta.getIdproducto(), venta.getCantidad());
            productoClient.restaurarStock(venta.getIdproducto(), venta.getCantidad());
            LOG.warnf("SAGA %s | COMPENSACION OK | Stock restaurado", sagaId);
        } catch (RuntimeException compensacionEx) {
            LOG.errorf(compensacionEx,
                    "SAGA %s | COMPENSACION FALLIDA | revisar producto=%d cantidad=%d",
                    sagaId, venta.getIdproducto(), venta.getCantidad());
        }
    }

    private String mensajeSeguro(RuntimeException ex) {
        if (ex.getMessage() == null || ex.getMessage().isBlank()) {
            return "error durante el proceso de venta";
        }

        return ex.getMessage();
    }

    @Override
    public List<Venta> findAll() {
        return repository.listAll();
    }

    @Override
    public List<Venta> findByCliente(Long idcliente) {
        if (idcliente == null) {
            throw new BadRequestException("El cliente autenticado es obligatorio");
        }

        return repository.list("idcliente", idcliente);
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
