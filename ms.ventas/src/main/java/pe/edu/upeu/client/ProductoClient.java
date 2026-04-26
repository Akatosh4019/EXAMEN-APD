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

    // 🔹 Obtener producto
    @GET
    @Path("/{id}")
    ProductoDTO buscarProductoPorId(@PathParam("id") Long id);

    // 🔥 NUEVO → Descontar stock
    @PUT
    @Path("/{id}/descontar/{cantidad}")
    ProductoDTO descontarStock(
            @PathParam("id") Long id,
            @PathParam("cantidad") int cantidad
    );
}