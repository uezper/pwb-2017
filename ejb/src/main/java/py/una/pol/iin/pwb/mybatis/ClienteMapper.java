package py.una.pol.iin.pwb.mybatis;

import java.util.List;

import py.una.pol.iin.pwb.model.Cliente;

public interface ClienteMapper {
	public void insertCliente(Cliente cliente);
	public void updateCliente(Cliente cliente);
	public void deleteCliente(Long id);
	public List<Cliente> findAllClientes();
	public Cliente findClienteById(Long id);
}
