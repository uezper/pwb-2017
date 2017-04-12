package py.una.pol.iin.pwb.bean;

import java.util.List;

import javax.ejb.Stateless;

import org.apache.ibatis.session.SqlSession;

import py.una.pol.iin.pwb.exception.DataNotFoundException;
import py.una.pol.iin.pwb.exception.InvalidFormatException;
import py.una.pol.iin.pwb.model.Cliente;
import py.una.pol.iin.pwb.mybatis.ClienteMapper;
import py.una.pol.iin.pwb.mybatis.MyBatisUtil;
import py.una.pol.iin.pwb.validator.CustomValidator;

@Stateless
public class ClienteBean implements IClienteBean {

	
	@Override
	public List<Cliente> getAllClientes() throws Exception {
				
		SqlSession session = MyBatisUtil.getSession();
		ClienteMapper clienteMapper = session.getMapper(ClienteMapper.class);
		List<Cliente> clientes = null;		
		clientes = clienteMapper.findAllClientes();
		session.close();
			
		return clientes;		
	}

	@Override
	public Cliente getCliente(Long id) throws DataNotFoundException, Exception {
		
		SqlSession session = MyBatisUtil.getSession();
		ClienteMapper clienteMapper = session.getMapper(ClienteMapper.class);
		Cliente cliente = null;		
		cliente = clienteMapper.findClienteById(id);
		session.close();			
		
		if (cliente == null)
			throw new DataNotFoundException("El cliente con id " + id + " no existe");
		
		
		return cliente;
	}

	@Override
	public Cliente addCliente(Cliente cliente) throws InvalidFormatException, Exception {

		CustomValidator.validateAndThrow(cliente);
		
		SqlSession session = MyBatisUtil.getSession();
		ClienteMapper clienteMapper = session.getMapper(ClienteMapper.class);
		clienteMapper.insertCliente(cliente);
		session.close();
		
		return cliente;
	}

	@Override
	public Cliente updateCliente(Cliente cliente)  throws DataNotFoundException, InvalidFormatException, Exception {
		CustomValidator.validateAndThrow(cliente);
		
		SqlSession session = MyBatisUtil.getSession();				
		ClienteMapper clienteMapper = session.getMapper(ClienteMapper.class);
		
		Cliente cliente2 = clienteMapper.findClienteById(cliente.getId());
		if (cliente2 == null) throw new DataNotFoundException("El cliente con el id " + cliente.getId() + " no existe");
		
		cliente2.setNombre(cliente.getNombre());
		cliente2.setTelefono(cliente.getTelefono());
		cliente2.setDeuda(cliente.getDeuda());
		
		clienteMapper.updateCliente(cliente2);
		session.close();
		
		return cliente2;
	}

	@Override
	public void removeCliente(Long id) throws Exception {
		
		SqlSession session = MyBatisUtil.getSession();						
		ClienteMapper clienteMapper = session.getMapper(ClienteMapper.class);
		clienteMapper.deleteCliente(id);
		session.close();
					
	}

}
