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

import py.una.pol.iin.pwb.model.Proveedor;
import py.una.pol.iin.pwb.model.Compra;
import py.una.pol.iin.pwb.mybatis.ProveedorMapper;

@RunWith(MockitoJUnitRunner.class)
public class ProveedorBeanTest {

	@Mock
	private ProveedorMapper proveedorMapper;

	@InjectMocks
	private ProveedorBean proveedorBean = new ProveedorBean();

    private void prepareMapper(){
    	ArrayList<Proveedor> proveedoresDB = new ArrayList<Proveedor>();
    	proveedoresDB.add(new Proveedor());
    	proveedoresDB.get(0).setId(10L);
    	proveedoresDB.get(0).setNombre("Juan");
    	proveedoresDB.get(0).setTelefono("0987-654321");
    	proveedoresDB.get(0).setCompras(new TreeSet<Compra>());
    	proveedoresDB.add(new Proveedor());
    	proveedoresDB.get(1).setId(15L);
    	proveedoresDB.get(1).setNombre("Maria");
    	proveedoresDB.get(1).setTelefono("0986-543212");
    	proveedoresDB.get(1).setCompras(new TreeSet<Compra>());
    	
    	when(proveedorMapper.findAllProveedores()).thenReturn(proveedoresDB);
    	when(proveedorMapper.findProveedorById(10L)).thenReturn(proveedoresDB.get(0));
    	when(proveedorMapper.findProveedorById(15L)).thenReturn(proveedoresDB.get(1));
    }

    @Test
    public void getAllProveedoresTest() throws Exception{
    	prepareMapper();

    	List<Proveedor> resultado = proveedorBean.getAllProveedores();
	    	
    	assertEquals(resultado.size(), 2);
    }

    @Test
    public void getProveedorTest() throws Exception{
    	prepareMapper();

    	Proveedor resultado = proveedorBean.getProveedor(10L);
    	assertEquals(resultado.getNombre(), "Juan");

    	resultado = proveedorBean.getProveedor(15L);
    	assertEquals(resultado.getNombre(), "Maria");
    }

    @Test
    public void addProveedorTest() throws Exception{
    	prepareMapper();
    	
    	Proveedor newProveedor = new Proveedor();
       	newProveedor.setId(null);
       	newProveedor.setNombre("Alberto");
       	newProveedor.setTelefono("021-123456");
       	
       	proveedorBean.addProveedor(newProveedor);
    	
    	verify(proveedorMapper).insertProveedor(newProveedor);
    }

    @Test
	public void updateProveedorTest() throws Exception {
    	prepareMapper();
    	
    	Proveedor newProveedor = new Proveedor();
       	newProveedor.setId(10L);
       	newProveedor.setNombre("Juana");
       	newProveedor.setTelefono("0987-654321");
       	newProveedor.setCompras(new TreeSet<Compra>());
       	
       	Proveedor resultado = proveedorBean.updateProveedor(newProveedor);
       	
       	assertEquals((long) resultado.getId(), 10L);
       	assertEquals(resultado.getNombre(), "Juana");
       	assertEquals(resultado.getTelefono(), "0987-654321");
	}

	@Test
	public void removeProveedorTest() throws Exception {		
    	prepareMapper();
    	
    	proveedorBean.removeProveedor(10L);
    	
    	verify(proveedorMapper).deleteProveedorById(10L);
	}
}
