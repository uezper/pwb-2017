package py.una.pol.iin.pwb.bean;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.ibatis.session.SqlSession;

import py.una.pol.iin.pwb.exception.DataNotFoundException;
import py.una.pol.iin.pwb.exception.InvalidFormatException;
import py.una.pol.iin.pwb.model.Cliente;
import py.una.pol.iin.pwb.mybatis.MyBatisUtil;
import py.una.pol.iin.pwb.repository.ClienteRepository;
import py.una.pol.iin.pwb.validator.CustomValidator;

@Stateless
public class ClienteBean implements IClienteBean {

	@Inject
	ClienteRepository clienteRepository;
	
	
	
	@Override
	public List<Cliente> getAllClientes() throws Exception {
				
		SqlSession session = MyBatisUtil.getSession();			
		List<Cliente> clientes = null;		
		clientes = session.selectList("Cliente.findAllClientes");
		session.close();
			
		return clientes;		
	}

	@Override
	public Cliente getCliente(Long id) throws DataNotFoundException, Exception {
		
		SqlSession session = MyBatisUtil.getSession();				
		Cliente cliente = null;		
		cliente = session.selectOne("Cliente.findClienteById", id);
		session.close();			
		
		if (cliente == null)
			throw new DataNotFoundException("El cliente con id " + id + " no existe");
		
		
		return cliente;
	}

	@Override
	public Cliente addCliente(Cliente cliente) throws InvalidFormatException, Exception {

		CustomValidator.validateAndThrow(cliente);
		
		SqlSession session = MyBatisUtil.getSession();
		session.insert("Cliente.insertCliente", cliente);				
		session.close();
		
		return cliente;
	}

	@Override
	public Cliente updateCliente(Cliente cliente)  throws DataNotFoundException, InvalidFormatException, Exception {
		CustomValidator.validateAndThrow(cliente);
		
		SqlSession session = MyBatisUtil.getSession();				
		
		Cliente cliente2 = session.selectOne("Cliente.findClienteById", cliente.getId());
		if (cliente2 == null) throw new DataNotFoundException("El cliente con el id " + cliente.getId() + " no existe");
		
		cliente2.setNombre(cliente.getNombre());
		cliente2.setTelefono(cliente.getTelefono());
		cliente2.setDeuda(cliente.getDeuda());
		
		session.update("Cliente.updateCliente", cliente2);
		session.close();
		
		return cliente2;
	}

	@Override
	public void removeCliente(Long id) throws Exception {
		
		SqlSession session = MyBatisUtil.getSession();						
		session.delete("Cliente.deleteClienteById", id);
		session.close();
					
	}

}
