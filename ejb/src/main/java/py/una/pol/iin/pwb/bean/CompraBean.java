package py.una.pol.iin.pwb.bean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import py.una.pol.iin.pwb.exception.DataNotFoundException;
import py.una.pol.iin.pwb.exception.InvalidArgumentException;
import py.una.pol.iin.pwb.exception.InvalidFormatException;
import py.una.pol.iin.pwb.model.Proveedor;
import py.una.pol.iin.pwb.model.DetalleCompra;
import py.una.pol.iin.pwb.model.Producto;
import py.una.pol.iin.pwb.model.Compra;
import py.una.pol.iin.pwb.validator.CustomValidator;

@Stateless
public class CompraBean implements ICompraBean {

	@Inject
	EntityManager em;
	@Inject
	IProductoBean productoBean;
	@Inject
	IProveedorBean proveedorBean;
	
	@Override
	public Compra addCompra(Compra compra) throws InvalidFormatException, InvalidArgumentException {								
		
		try {
			
			CustomValidator.validateAndThrow(compra);
			Proveedor proveedor = proveedorBean.getProveedor(compra.getProveedorId());
			
			
			
			double total = 0;
			for (DetalleCompra detalleCompra : compra.getDetalles())
			{			
				detalleCompra.setCompra(compra);							
				CustomValidator.validateAndThrow(detalleCompra);
				
							
				Producto producto = productoBean.getProducto(detalleCompra.getProductoId());		
				if (detalleCompra.getCantidad() > producto.getCantidad()) {
					throw new InvalidArgumentException("La cantidad vendida no puede superar a la cantidad en stock");
				}																
			}
			
		
			
			compra.setProveedor(proveedor);
			em.persist(compra);			
			
			for (DetalleCompra detalleCompra : compra.getDetalles())
			{
				Producto producto = productoBean.getProducto(detalleCompra.getProductoId());
				
				detalleCompra.setCompra(compra);
				detalleCompra.setProducto(producto);
				em.persist(detalleCompra);
				
				total += detalleCompra.getPrecioUnitario() * detalleCompra.getCantidad();
																
				producto.setCantidad(producto.getCantidad() - detalleCompra.getCantidad());
				em.merge(producto);
			}
			
			
			compra.setMontoTotal(total);
			
			
			em.merge(proveedor);
			em.merge(compra);
			em.flush();
			em.refresh(compra);			
			
			return compra;
			
		} catch(DataNotFoundException e)
		{
			throw new InvalidArgumentException(e.getMessage());
		}
		
		
	}

}
