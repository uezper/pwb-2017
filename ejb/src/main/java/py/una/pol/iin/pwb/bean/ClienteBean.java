package py.una.pol.iin.pwb.bean;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import py.una.pol.iin.pwb.exception.DataNotFoundException;
import py.una.pol.iin.pwb.exception.InvalidFormatException;
import py.una.pol.iin.pwb.model.Cliente;
import py.una.pol.iin.pwb.repository.ClienteRepository;
import py.una.pol.iin.pwb.validator.CustomValidator;

@Stateless
public class ClienteBean implements IClienteBean {

	@Inject
	EntityManager em;
	@Inject
	ClienteRepository clienteRepository;
	
	@Override
	public List<Cliente> getAllClientes() {
		return clienteRepository.findAllClientes();
	}

	@Override
	public Cliente getCliente(Long id) throws DataNotFoundException {
		Cliente cliente = clienteRepository.findClienteById(id);
		if (cliente == null)
			throw new DataNotFoundException("El cliente con id " + id + " no existe");
		return cliente;
	}

	@Override
	public Cliente addCliente(Cliente cliente) throws InvalidFormatException {
		CustomValidator.validateAndThrow(cliente);
		em.persist(cliente);
		em.flush();
		em.refresh(cliente);
		return cliente;
	}

	@Override
	public Cliente updateCliente(Cliente cliente)  throws DataNotFoundException, InvalidFormatException {
		CustomValidator.validateAndThrow(cliente);		
		Cliente cliente2 = clienteRepository.findClienteById(cliente.getId());
		if (cliente2 == null) throw new DataNotFoundException("El cliente con el id " + cliente.getId() + " no existe");
		
		cliente2.setNombre(cliente.getNombre());
		cliente2.setTelefono(cliente.getTelefono());
		cliente2.setDeuda(cliente.getDeuda());
		
		em.merge(cliente2);
		em.flush();
		em.refresh(cliente2);
		return cliente2;
	}

	@Override
	public void removeCliente(Long id) {
		Cliente cliente = clienteRepository.findClienteById(id);
		if (cliente != null) em.remove(cliente);		
	}

}
