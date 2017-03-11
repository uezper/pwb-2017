package py.una.pol.iin.pwb.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import py.una.pol.iin.pwb.bean.IProductoBean;
import py.una.pol.iin.pwb.bean.IVentaBean;
import py.una.pol.iin.pwb.decortator.CatchExceptions;
import py.una.pol.iin.pwb.exception.DataNotFoundException;
import py.una.pol.iin.pwb.model.Producto;
import py.una.pol.iin.pwb.model.Venta;

@Path("/productos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductoResource {
	
	@Inject
	IProductoBean productoBean;
	@Inject
	IVentaBean ventaBean;
	
	@POST
	@Path("/vender")
	@CatchExceptions
	public Venta vender(Venta venta) throws Exception
	{
		return ventaBean.addVenta(venta);
	}
	
	
	@GET
	@CatchExceptions
	public List<Producto> getAllProductos()
	{
		return productoBean.getAllProductos();
	}
	
	@GET
	@CatchExceptions
	@Path("/{productoId: [0-9]+}")
	public Producto getProducto(@PathParam("productoId") long id)  throws Exception
	{
		return productoBean.getProducto(id);
	}
	
	@POST
	@CatchExceptions
	public Producto postProducto(Producto producto) throws Exception
	{	
		producto.setId(null);
		return productoBean.addProducto(producto);
	}
	
	
	@PUT
	@CatchExceptions
	@Path("/{productoId: [0-9]+}")
	public Producto putProducto(@PathParam("productoId") long id, Producto producto)  throws Exception
	{
		producto.setId(id);
		return productoBean.updateProducto(producto);
	}
	
	@DELETE
	@CatchExceptions
	@Path("/{productoId: [0-9]+}")
	public void deleteProducto(@PathParam("productoId") long id)
	{
		productoBean.removeProducto(id);
	}

}