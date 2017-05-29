package py.una.pol.iin.pwb.bean;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import py.una.pol.iin.pwb.model.Cliente;
import py.una.pol.iin.pwb.model.Venta;
import py.una.pol.iin.pwb.mybatis.ClienteMapper;

@RunWith(MockitoJUnitRunner.class)
public class ClienteBeanTest {

	@Mock
	private ClienteMapper clienteMapper;

	@InjectMocks
	private ClienteBean clienteBean = new ClienteBean();

    private void prepareMapper(){
    	ArrayList<Cliente> clientesDB = new ArrayList<Cliente>();
    	clientesDB.add(new Cliente());
    	clientesDB.get(0).setId(10L);
    	clientesDB.get(0).setNombre("Juan");
    	clientesDB.get(0).setTelefono("0987-654321");
    	clientesDB.get(0).setVentas(new TreeSet<Venta>());
    	clientesDB.add(new Cliente());
    	clientesDB.get(1).setId(15L);
    	clientesDB.get(1).setNombre("Maria");
    	clientesDB.get(1).setTelefono("0986-543212");
    	clientesDB.get(1).setVentas(new TreeSet<Venta>());
    	
    	when(clienteMapper.findAllClientes()).thenReturn(clientesDB);
    	when(clienteMapper.findClienteById(10L)).thenReturn(clientesDB.get(0));
    	when(clienteMapper.findClienteById(15L)).thenReturn(clientesDB.get(1));
    }

    @Test
    public void getAllClientesTest() throws Exception{
    	prepareMapper();

    	List<Cliente> resultado = clienteBean.getAllClientes();
	    	
    	assertEquals(resultado.size(), 2);
    }

    @Test
    public void getClienteTest() throws Exception{
    	prepareMapper();

    	Cliente resultado = clienteBean.getCliente(10L);
    	assertEquals(resultado.getNombre(), "Juan");

    	resultado = clienteBean.getCliente(15L);
    	assertEquals(resultado.getNombre(), "Maria");
    }

    @Test
    public void addClienteTest() throws Exception{
    	prepareMapper();
    	
    	Cliente newCliente = new Cliente();
       	newCliente.setId(null);
       	newCliente.setNombre("Alberto");
       	newCliente.setTelefono("021-123456");
       	newCliente.setDeuda(0);
       	
       	clienteBean.addCliente(newCliente);
    	
    	verify(clienteMapper).insertCliente(newCliente);
    }

    @Test
	public void updateClienteTest() throws Exception {
    	prepareMapper();
    	
    	Cliente newCliente = new Cliente();
       	newCliente.setId(10L);
       	newCliente.setNombre("Juana");
       	newCliente.setTelefono("0987-654321");
       	newCliente.setVentas(new TreeSet<Venta>());
       	
       	Cliente resultado = clienteBean.updateCliente(newCliente);
       	
       	assertEquals((long) resultado.getId(), 10L);
       	assertEquals(resultado.getNombre(), "Juana");
       	assertEquals(resultado.getTelefono(), "0987-654321");
	}

	@Test
	public void removeClienteTest() throws Exception {		
    	prepareMapper();
    	
    	clienteBean.removeCliente(10L);
    	
    	verify(clienteMapper).deleteClienteById(10L);
	}
}
