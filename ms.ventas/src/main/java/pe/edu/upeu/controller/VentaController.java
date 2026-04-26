package pe.edu.upeu.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import pe.edu.upeu.dto.ProductoDTO;
import pe.edu.upeu.entity.Venta;
import pe.edu.upeu.services.VentaService;

import java.util.List;

// 🔥 IMPORTS NUEVOS
import org.eclipse.microprofile.rest.client.inject.RestClient;
import pe.edu.upeu.client.ProductoClient;

@Path("/ventas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VentaController {

    @Inject
    VentaService service;

    // 🔥 CLIENTE PARA ms-producto
    @Inject
    @RestClient
    ProductoClient productoClient;

    // ======================
    // CRUD NORMAL
    // ======================

    @GET
    public List<Venta> list() {
        return service.findAll();
    }

    @GET
    @Path("/{id}")
    public Venta get(@PathParam("id") Long id) {
        return service.findById(id);
    }

    @POST
    public Venta create(Venta venta) {
        return service.create(venta);
    }

    @PUT
    @Path("/{id}")
    public Venta update(@PathParam("id") Long id, Venta venta) {
        return service.update(id, venta);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        service.delete(id);
    }

    // ======================
    // 🔥 PRUEBA MICROSERVICIO
    // ======================

    @GET
    @Path("/test-producto")
    public ProductoDTO testProducto() {
        return productoClient.buscarProductoPorId(21L);
    }
}