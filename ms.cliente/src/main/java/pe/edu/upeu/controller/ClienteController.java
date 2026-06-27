package pe.edu.upeu.controller;


import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import pe.edu.upeu.entity.Cliente;
import pe.edu.upeu.services.ClienteService;

import java.util.List;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteController {

    @Inject
    ClienteService service;

    @GET
    public List<Cliente> list() {
        return service.findAll();
    }

    @GET
    @Path("/{id}")
    public Cliente get(@PathParam("id") Long id) {
        return service.findById(id);
    }

    @GET
    @Path("/correo/{correo}")
    public Cliente getByCorreo(@PathParam("correo") String correo) {
        return service.findByCorreo(correo);
    }

    @GET
    @Path("/{id}/validar")
    public Cliente validarClienteActivo(@PathParam("id") Long id) {
        return service.validarClienteActivo(id);
    }

    @POST
    public Cliente create(Cliente cliente) {
        return service.create(cliente);
    }

    @PUT
    @Path("/{id}")
    public Cliente update(@PathParam("id") Long id, Cliente cliente) {
        return service.update(id, cliente);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        service.delete(id);
    }

    @PUT
    @Path("/{id}/desactivar")
    public Cliente desactivar(@PathParam("id") Long id) {
        return service.desactivar(id);
    }

    @PUT
    @Path("/{id}/activar")
    public Cliente activar(@PathParam("id") Long id) {
        return service.activar(id);
    }
}
