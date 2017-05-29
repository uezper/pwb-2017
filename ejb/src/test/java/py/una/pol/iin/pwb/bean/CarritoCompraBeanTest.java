package py.una.pol.iin.pwb.bean;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import py.una.pol.iin.pwb.model.Proveedor;
import py.una.pol.iin.pwb.exception.DataNotFoundException;
import py.una.pol.iin.pwb.exception.InvalidArgumentException;
import py.una.pol.iin.pwb.model.Compra;
import py.una.pol.iin.pwb.model.DetalleCompra;
import py.una.pol.iin.pwb.model.Producto;
import py.una.pol.iin.pwb.mybatis.CompraMapper;
import py.una.pol.iin.pwb.mybatis.DetalleCompraMapper;
import py.una.pol.iin.pwb.mybatis.ProductoMapper;
import py.una.pol.iin.pwb.mybatis.ProveedorMapper;

@RunWith(MockitoJUnitRunner.class)
public class CarritoCompraBeanTest {

	@Mock UserTransaction userTransaction;
	@Mock IProductoBean productoBean;
	@Mock IProveedorBean proveedorBean;
	@Mock CompraMapper compraMapper;
	@Mock DetalleCompraMapper detalleCompraMapper;
	@Mock ProductoMapper productoMapper;
	@Mock ProveedorMapper proveedorMapper;

	@InjectMocks
	private CarritoCompraBean carritoCompraBean = new CarritoCompraBean();
	
    class UserTransactionGetStatusAnswer implements Answer<Integer>{
    	public boolean transactionBegin = false;
    	
		@Override
		public Integer answer(InvocationOnMock invocation) throws Throwable {
			if(transactionBegin) return Status.STATUS_ACTIVE;
			else return Status.STATUS_PREPARED;
		}
	}
    
    class UserTransactionBeginAnswer implements Answer{
		@Override
		public Object answer(InvocationOnMock invocation) throws Throwable {
			userTransactionGetStatusAnswer.transactionBegin = true;
			return null;
		}
	}

    UserTransactionGetStatusAnswer userTransactionGetStatusAnswer = new UserTransactionGetStatusAnswer();
    UserTransactionBeginAnswer userTransactionBeginAnswer = new UserTransactionBeginAnswer();

	@Before
	public void setUp() throws Exception{
		prepareMapper();
    	userTransactionGetStatusAnswer.transactionBegin = false;
    	when(userTransaction.getStatus()).thenAnswer(userTransactionGetStatusAnswer);
    	doAnswer(userTransactionBeginAnswer).when(userTransaction).begin();
	}

    private void prepareMapper() throws Exception {
    	ArrayList<Proveedor> proveedoresDB = new ArrayList<Proveedor>();
    	proveedoresDB.add(new Proveedor(){{
	    	setId(10L);
	    	setNombre("Juan");
	    	setTelefono("0987-654321");
	    	setCompras(new TreeSet<Compra>());
    	}});
    	proveedoresDB.add(new Proveedor(){{
	    	setId(15L);
	    	setNombre("Maria");
	    	setTelefono("0986-543212");
	    	setCompras(new TreeSet<Compra>());
	    }});
    	ArrayList<Producto> productoDB = new ArrayList<Producto>();
    	productoDB.add(new Producto(){{
	    	setId(110L);
	    	setDescripcion("Lapiz");
	    	setCantidad(50);
	    	setDetalleVentas(null);
    	}});
    	productoDB.add(new Producto(){{
	    	setId(115L);
	    	setDescripcion("Borrador");
	    	setCantidad(60);
	    	setDetalleVentas(null);
    	}});

    	when(proveedorBean.getProveedor(10L)).thenReturn(proveedoresDB.get(0));
    	when(proveedorBean.getProveedor(15L)).thenReturn(proveedoresDB.get(1));
    	when(proveedorBean.getProveedor(20L)).thenThrow(new DataNotFoundException(""));

    	when(productoBean.getProducto(110L)).thenReturn(productoDB.get(0));
    	when(productoBean.getProducto(115L)).thenReturn(productoDB.get(1));
    	when(productoBean.getProducto(120L)).thenThrow(new DataNotFoundException(""));
    }

    @Test
    public void crearCompraTest() throws Exception{
    	Compra compra = new Compra();
    	compra.setProveedorId(10L);
    	doNothing().when(compraMapper).insertCompra(compra);

    	carritoCompraBean.initValues();
    	Compra resultado = carritoCompraBean.crearCompra(compra);
    	
    	assertEquals((long)resultado.getProveedor().getId(), 10L);
    	assertEquals(resultado.getProveedor().getNombre(), "Juan");
    	assertTrue(resultado.getMontoTotal() == 0.0);
    	assertEquals(resultado.getDetalles().length, 0);
    	verify(compraMapper).insertCompra(compra);
    }
    
    @Test(expected=InvalidArgumentException.class)
    public void crearCompraProveedorNoExisteTest() throws Exception{
    	Compra compra = new Compra();
    	compra.setProveedorId(20L);
    	doNothing().when(compraMapper).insertCompra(compra);

    	carritoCompraBean.initValues();
		Compra resultado = carritoCompraBean.crearCompra(compra);
    }
    
    @Test
    public void agregarProductosTest() throws Exception{
    	Compra compra = new Compra();
    	compra.setProveedorId(10L);
    	doNothing().when(compraMapper).insertCompra(compra);
    	List<DetalleCompra> detalleCompras = new ArrayList<DetalleCompra>();
    	detalleCompras.add(new DetalleCompra(){{
    		setPrecioUnitario(1000.0);
    		setCantidad(9);
    		setProductoId(110L);
    	}});
    	detalleCompras.add(new DetalleCompra(){{
    		setPrecioUnitario(1000.0);
    		setCantidad(2);
    		setProductoId(110L);
    	}});
    	detalleCompras.add(new DetalleCompra(){{
    		setPrecioUnitario(2000.0);
    		setCantidad(7);
    		setProductoId(115L);
    	}});

    	carritoCompraBean.initValues();
    	carritoCompraBean.crearCompra(compra);
    	Compra resultado = carritoCompraBean.agregarProductos(detalleCompras);


    	verify(detalleCompraMapper, times(3)).insertDetalleCompra(any(DetalleCompra.class));
    }

    @Test(expected=InvalidArgumentException.class)
    public void finalizarCompraSinProductoTest() throws Exception{
    	Compra compra = new Compra();
    	compra.setProveedorId(15L);
    	doNothing().when(compraMapper).insertCompra(compra);

    	carritoCompraBean.initValues();
		carritoCompraBean.crearCompra(compra);
		Compra resultado = carritoCompraBean.finalizarCompra();
    }

}
