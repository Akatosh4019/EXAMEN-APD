package pe.edu.upeu.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import pe.edu.upeu.dto.ClienteDTO;

@Path("/clientes")
@RegisterRestClient(configKey = "cliente-api")
public interface ClienteClient {

    @GET
    @Path("/{id}")
    ClienteDTO buscarClientePorId(@PathParam("id") Long id);
}