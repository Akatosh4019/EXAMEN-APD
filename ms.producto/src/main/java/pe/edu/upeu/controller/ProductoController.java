package pe.edu.upeu.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import pe.edu.upeu.entity.Producto;
import pe.edu.upeu.services.ProductoService;

import java.util.List;

    @Path("/productos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public class ProductoController {

        @Inject
        ProductoService service;

        @GET
        public List<Producto> list() {
            return service.findAll();
        }

        @GET
        @Path("/{id}")
        public Producto get(@PathParam("id") Long id) {
            return service.findById(id);
        }

        @POST
        public Producto create(Producto producto) {
            return service.create(producto);
        }

        @PUT
        @Path("/{id}")
        public Producto update(@PathParam("id") Long id, Producto producto) {
            return service.update(id, producto);
        }

        @DELETE
        @Path("/{id}")
        public void delete(@PathParam("id") Long id) {
            service.delete(id);
        }
        @PUT
        @Path("/{id}/descontar/{cantidad}")
        public Producto descontarStock(@PathParam("id") Long id, @PathParam("cantidad") int cantidad) {
            return service.descontarStock(id, cantidad);
        }
        @PUT
        @Path("/{id}/desactivar")
        public Producto desactivar(@PathParam("id") Long id) {
            return service.desactivar(id);
        }

        @PUT
        @Path("/{id}/aumentar/{cantidad}")
        public Producto aumentarStock(@PathParam("id") Long id, @PathParam("cantidad") int cantidad) {
            return service.aumentarStock(id, cantidad);
        }

        @PUT
        @Path("/{id}/activar")
        public Producto activar(@PathParam("id") Long id) {
            return service.activar(id);
        }
    }

