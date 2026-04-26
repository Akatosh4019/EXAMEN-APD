package pe.edu.upeu.services;

import pe.edu.upeu.entity.Cliente;

import java.util.List;

public interface ClienteService {

    Cliente create(Cliente cliente);

    List<Cliente> findAll();

    Cliente findById(Long id);

    Cliente update(Long id, Cliente cliente);

    void delete(Long id);

    Cliente desactivar(Long id);

    Cliente activar(Long id);
}