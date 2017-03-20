package py.una.pol.iin.pwb.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.PostActivate;
import javax.ejb.Stateful;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import py.una.pol.iin.pwb.exception.DataNotFoundException;
import py.una.pol.iin.pwb.exception.InvalidArgumentException;
import py.una.pol.iin.pwb.exception.InvalidFormatException;
import py.una.pol.iin.pwb.model.Proveedor;
import py.una.pol.iin.pwb.model.DetalleCompra;
import py.una.pol.iin.pwb.model.Producto;
import py.una.pol.iin.pwb.model.Compra;
import py.una.pol.iin.pwb.validator.CustomValidator;

@Stateful
@TransactionManagement(TransactionManagementType.BEAN)
public class CarritoCompraBean implements ICarritoCompraBean {

	private String sessionKey;	

	@Resource
	private UserTransaction userTransaction;
	
	@Inject
	EntityManager em;
	@Inject
	IProductoBean productoBean;
	@Inject
	IProveedorBean proveedorBean;
	
	Compra compra;
	
	
	@PostConstruct
	@PostActivate
	public void initValues()
	{
		sessionKey = null;
		compra = null;
	}
	
	@Override
	public String getSessionKey() {
		return sessionKey;
	}

	@Override
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	
	@Override
	public Compra crearCompra(Compra compra) throws InvalidArgumentException, InvalidFormatException, DataNotFoundException, Exception {
				
		
		if (userTransaction.getStatus() == Status.STATUS_ACTIVE && this.compra == null)
		{
			throw new InvalidArgumentException("Ya existe una compra en curso. Debe finalizar o cancelar la operacion.");
		}
		
		try {
			
			// Para que el validador no tire el error de que la lista
			// no puede ser nula o estar vacia.
			compra.setDetalles(new DetalleCompra[2]);
			
			userTransaction.begin();
			
			CustomValidator.validateAndThrow(compra);
			Proveedor proveedor = proveedorBean.getProveedor(compra.getProveedorId());
			
			this.compra = compra;
			this.compra.setProveedor(proveedor);
			
			em.persist(this.compra);
			em.flush();
			em.refresh(this.compra);
		} catch (DataNotFoundException e) {
			if (userTransaction.getStatus() == Status.STATUS_ACTIVE) userTransaction.rollback();			
			throw new InvalidArgumentException(e.getMessage());
		} catch (InvalidFormatException e) {
			if (userTransaction.getStatus() == Status.STATUS_ACTIVE) userTransaction.rollback();			
			throw new InvalidFormatException(e.getMessage());
		} catch (Exception e) {			
			if (userTransaction.getStatus() == Status.STATUS_ACTIVE) userTransaction.rollback();
			throw new Exception(e.getMessage());
		}
			
		return getCompra();
	}
	
	
	@Override
	public Compra finalizarCompra() throws InvalidArgumentException, Exception {
		
		
		verifyStatus();
		
		
		em.flush();
		this.compra = getCompra();
		if (this.compra.getDetalleCompras().size() < 1)
		{
			throw new InvalidArgumentException("No se puede finalizar una compra sin productos asociados");
		}
		
		Proveedor proveedor = this.compra.getProveedor();
		em.merge(proveedor);
		em.flush();
		
		Compra compra = this.compra;
		this.compra = null;
		userTransaction.commit();				
		return compra;
	}
	@Override
	public void cancelarCompra() throws InvalidArgumentException, Exception {
		
		verifyStatus();
		
		this.compra = null;
		userTransaction.rollback();		
	}
	
	
	@Override
	public Compra agregarProductos(List<DetalleCompra> detallesCompras)
			throws InvalidArgumentException, InvalidFormatException, Exception {
		
		verifyStatus();
		
		CustomValidator.validateAndThrow(detallesCompras);
		
		for (DetalleCompra detalleCompra : detallesCompras)
		{
				try {
					
					Producto producto = productoBean.getProducto(detalleCompra.getProductoId());							
					
					producto.setCantidad(producto.getCantidad() + detalleCompra.getCantidad());
					em.merge(producto);
					
					boolean actualizado = false;
					for (DetalleCompra detalleCompraExistente : this.compra.getDetalleCompras())
					{
						if (detalleCompraExistente.getProducto().getId() == detalleCompra.getProductoId())
						{
							detalleCompraExistente.setCantidad(detalleCompraExistente.getCantidad() + detalleCompra.getCantidad());
							em.merge(detalleCompraExistente);
							actualizado = true;
							break;
						}
					}
					if (!actualizado)
					{					
						detalleCompra.setCompra(this.compra);
						detalleCompra.setProducto(producto);
						em.persist(detalleCompra);
						
					}
					
				} catch (Exception e)
				{
					if (userTransaction.getStatus() == Status.STATUS_ACTIVE) userTransaction.rollback();
					throw new Exception(e.getMessage());
				}
			
			
		}
		em.flush();
		return getCompra();
	}

	@Override
	public Compra quitarProductos(List<DetalleCompra> detallesCompras) throws InvalidArgumentException, InvalidFormatException, Exception {		
		
		verifyStatus();		
		
		for (DetalleCompra detalleCompra : detallesCompras)
		{
			try {
				CustomValidator.validateAndThrow(detallesCompras);
				
				Producto producto = productoBean.getProducto(detalleCompra.getProductoId());																			
				
				boolean encontrado = false;
				for (DetalleCompra detalleCompraExistente : this.compra.getDetalleCompras())
				{
					if (detalleCompraExistente.getProducto().getId() == detalleCompra.getProductoId())
					{
						if (detalleCompraExistente.getCantidad() < detalleCompra.getCantidad())
							throw new InvalidArgumentException("La cantidad del producto " + producto.getId() + " que se quiere quitar es menor a la cantidad existente en el carrito");
						encontrado = true;
						break;
					}
				}
				if (!encontrado)
				{					
					throw new InvalidArgumentException("No existe el producto " + producto.getId() + " en el carrito.");
					
				}
				
			} catch (DataNotFoundException e) {				
				throw new InvalidArgumentException(e.getMessage());				
			} 
		}		
			
		
		for (DetalleCompra detalleCompra : detallesCompras)
		{
				try {
					
					Producto producto = productoBean.getProducto(detalleCompra.getProductoId());							
					
					producto.setCantidad(producto.getCantidad() - detalleCompra.getCantidad());
					em.merge(producto);
					
					for (DetalleCompra detalleCompraExistente : this.compra.getDetalleCompras())
					{
						if (detalleCompraExistente.getProducto().getId() == detalleCompra.getProductoId())
						{
							int cantidad = detalleCompraExistente.getCantidad() - detalleCompra.getCantidad();
							if (cantidad > 0) {
								detalleCompraExistente.setCantidad(cantidad);
								em.merge(detalleCompraExistente);
							}							
							break;
						}
					}					
					
				} catch (Exception e)
				{
					if (userTransaction.getStatus() == Status.STATUS_ACTIVE) userTransaction.rollback();
					throw new Exception(e.getMessage());
				}
			
			
		}
		em.flush();
		return getCompra();
	}

	@Override
	public Compra getCompra() throws InvalidArgumentException, Exception {
		
		verifyStatus();
		
		DetalleCompra[] detalles = new DetalleCompra[this.compra.getDetalleCompras().size()];
		double monto = 0f;
		int i = 0;
		for (DetalleCompra detalleCompra : this.compra.getDetalleCompras()) {
			monto += detalleCompra.getPrecioUnitario() * detalleCompra.getCantidad();
			detalles[i++] = detalleCompra;
		}
		
		this.compra.setMontoTotal(monto);
		this.compra.setDetalles(detalles);
		
		em.merge(this.compra);
		em.flush();
		em.refresh(this.compra);
		
		return this.compra;
	}

	
	private void verifyStatus() throws InvalidArgumentException, Exception {
		
		
		
		if (userTransaction.getStatus() == Status.STATUS_ROLLEDBACK || userTransaction.getStatus() == Status.STATUS_ROLLING_BACK || userTransaction.getStatus() == Status.STATUS_MARKED_ROLLBACK)
		{
			throw new InvalidArgumentException("La transaccion ha sido abortada");
		}
		else if (userTransaction.getStatus() == Status.STATUS_COMMITTED || userTransaction.getStatus() == Status.STATUS_COMMITTING)
		{
			throw new InvalidArgumentException("La transaccion ha sido finalizada");
		}
				
		else if (userTransaction.getStatus() != Status.STATUS_ACTIVE || this.compra == null)
		{
			throw new InvalidArgumentException("No existe una transaccion en curso");
		}
	}
	
}
