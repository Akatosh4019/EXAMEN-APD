package pe.edu.upeu.serviceImpl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import pe.edu.upeu.entity.Cliente;
import pe.edu.upeu.repository.ClienteRepository;
import pe.edu.upeu.services.ClienteService;
import pe.edu.upeu.errors.*;

import java.util.List;

@ApplicationScoped
public class ClienteServiceImpl implements ClienteService {

    @Inject
    ClienteRepository repository;

    @Override
    @Transactional
    public Cliente create(Cliente cliente) {

        if (repository.find("correo", cliente.getCorreo()).firstResult() != null) {
            throw new ConflictException("El correo ya está registrado");
        }

        repository.persist(cliente);
        return cliente;
    }

    @Override
    public List<Cliente> findAll() {
        return repository.listAll();
    }

    @Override
    public Cliente findById(Long id) {
        Cliente c = repository.findById(id);

        if (c == null) {
            throw new NotFoundException("Cliente no encontrado con id: " + id);
        }

        return c;
    }

    @Override
    @Transactional
    public Cliente update(Long id, Cliente cliente) {
        Cliente entity = repository.findById(id);

        if (entity == null) {
            throw new NotFoundException("Cliente no encontrado con id: " + id);
        }

        entity.setNombres(cliente.getNombres());
        entity.setApellidos(cliente.getApellidos());
        entity.setCorreo(cliente.getCorreo());
        entity.setTelefono(cliente.getTelefono());
        entity.setEstado(cliente.getEstado());

        return entity;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.deleteById(id)) {
            throw new NotFoundException("Cliente no encontrado con id: " + id);
        }
    }
    @Override
    @Transactional
    public Cliente desactivar(Long id) {

        Cliente c = repository.findById(id);

        if (c == null) {
            throw new NotFoundException("Cliente no encontrado con id: " + id);
        }

        c.setEstado("I");

        return c;
    }

    @Override
    @Transactional
    public Cliente activar(Long id) {

        Cliente c = repository.findById(id);

        if (c == null) {
            throw new NotFoundException("Cliente no encontrado con id: " + id);
        }

        c.setEstado("A"); // 🔥 como es String

        return c;
    }
}