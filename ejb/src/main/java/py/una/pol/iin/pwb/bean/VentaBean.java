package py.una.pol.iin.pwb.bean;

import java.awt.List;
import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import py.una.pol.iin.pwb.exception.DataNotFoundException;
import py.una.pol.iin.pwb.exception.InvalidArgumentException;
import py.una.pol.iin.pwb.exception.InvalidFormatException;
import py.una.pol.iin.pwb.model.Cliente;
import py.una.pol.iin.pwb.model.DetalleVenta;
import py.una.pol.iin.pwb.model.Producto;
import py.una.pol.iin.pwb.model.Venta;
import py.una.pol.iin.pwb.validator.CustomValidator;

@Stateless
public class VentaBean implements IVentaBean {

	@Inject
	EntityManager em;
	@Inject
	IProductoBean productoBean;
	@Inject
	IClienteBean clienteBean;
	
	@Override
	public Venta addVenta(Venta venta) throws InvalidFormatException, InvalidArgumentException {								
		
		try {
			
			CustomValidator.validateAndThrow(venta);
			Cliente cliente = clienteBean.getCliente(venta.getClienteId());
			
			
			
			double total = 0;
			for (DetalleVenta detalleVenta : venta.getDetalles())
			{			
				detalleVenta.setVenta(venta);							
				CustomValidator.validateAndThrow(detalleVenta);
				
							
				Producto producto = productoBean.getProducto(detalleVenta.getProductoId());		
				if (detalleVenta.getCantidad() > producto.getCantidad()) {
					throw new InvalidArgumentException("La cantidad vendida no puede superar a la cantidad en stock");
				}																
			}
			
		
			
			venta.setCliente(cliente);
			em.persist(venta);			
			
			for (DetalleVenta detalleVenta : venta.getDetalles())
			{
				Producto producto = productoBean.getProducto(detalleVenta.getProductoId());
				
				detalleVenta.setVenta(venta);
				detalleVenta.setProducto(producto);
				em.persist(detalleVenta);
				
				total += detalleVenta.getPrecioUnitario() * detalleVenta.getCantidad();
																
				producto.setCantidad(producto.getCantidad() - detalleVenta.getCantidad());
				em.merge(producto);
			}
			
			
			cliente.setDeuda(cliente.getDeuda() + total);
			venta.setMontoTotal(total);
			
			
			em.merge(cliente);
			em.merge(venta);
			em.flush();
			em.refresh(venta);			
			
			return venta;
			
		} catch(DataNotFoundException e)
		{
			throw new InvalidArgumentException(e.getMessage());
		}
		
		
	}

}
