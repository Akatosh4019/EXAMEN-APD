package pe.edu.upeu.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import pe.edu.upeu.dto.ProductoDTO;

@Path("/productos")
@RegisterRestClient(configKey = "producto-api")
public interface ProductoClient {

    @GET
    @Path("/{id}")
    ProductoDTO buscarProductoPorId(@PathParam("id") Long id);

    @GET
    @Path("/{id}/validar-stock/{cantidad}")
    ProductoDTO validarProductoDisponible(
            @PathParam("id") Long id,
            @PathParam("cantidad") int cantidad
    );

    @PUT
    @Path("/{id}/descontar-stock/{cantidad}")
    ProductoDTO descontarStock(
            @PathParam("id") Long id,
            @PathParam("cantidad") int cantidad
    );

    @PUT
    @Path("/{id}/restaurar-stock/{cantidad}")
    ProductoDTO restaurarStock(
            @PathParam("id") Long id,
            @PathParam("cantidad") int cantidad
    );
}
