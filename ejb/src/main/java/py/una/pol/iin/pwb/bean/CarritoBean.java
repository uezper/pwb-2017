package py.una.pol.iin.pwb.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.PostActivate;
import javax.ejb.Stateful;
import javax.inject.Inject;

import py.una.pol.iin.pwb.exception.DataNotFoundException;
import py.una.pol.iin.pwb.exception.InvalidArgumentException;
import py.una.pol.iin.pwb.exception.InvalidFormatException;
import py.una.pol.iin.pwb.model.CarritoQuery;
import py.una.pol.iin.pwb.model.Producto;
import py.una.pol.iin.pwb.validator.CustomValidator;

@Stateful
public class CarritoBean implements ICarritoBean {

	private String sessionKey;
	private HashMap<Long, Integer> carrito;

	@Inject
	IProductoBean productoBean;
	
	
	@PostConstruct
	@PostActivate
	public void initValues()
	{
		sessionKey = null;
		carrito = new HashMap<Long, Integer>();
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
	public List<CarritoQuery> agregarProductos(List<CarritoQuery> carritoQueries)
			throws InvalidArgumentException, InvalidFormatException {
		
		
		for (CarritoQuery carritoQuery : carritoQueries)
		{
			CustomValidator.validateAndThrow(carritoQuery);
			try {
				Producto producto = productoBean.getProducto(carritoQuery.getProductoId());							
				
				if (carrito.containsKey(producto.getId())) {
					Integer cantidad = carrito.get(producto.getId());
					cantidad += carritoQuery.getProductoCantidad();
					carrito.put(producto.getId(), cantidad);
				}
				else {
					carrito.put(producto.getId(), carritoQuery.getProductoCantidad());
				}
			} catch (DataNotFoundException e) {
				throw new InvalidArgumentException(e.getMessage());				
			}
			
		}
		
		return getCarrito();
	}

	@Override
	public List<CarritoQuery> quitarProductos(List<CarritoQuery> carritoQueries)
			throws InvalidArgumentException, InvalidFormatException {
		
		
		for (CarritoQuery carritoQuery : carritoQueries)
		{
			CustomValidator.validateAndThrow(carritoQuery);
			try {
				Producto producto = productoBean.getProducto(carritoQuery.getProductoId());
				
				if (carrito.containsKey(producto.getId())) {
					Integer cantidad = carrito.get(producto.getId());
					cantidad -= carritoQuery.getProductoCantidad();
					if (cantidad > 0) { carrito.put(producto.getId(), cantidad); }
					else { carrito.remove(producto.getId()); }
						
				}
				else {
					// do nothing
				}
			} catch (DataNotFoundException e) {
				throw new InvalidArgumentException(e.getMessage());				
			}
			
		}
		
		return getCarrito();
		
		
	}

	@Override
	public List<CarritoQuery> getCarrito() {
		
		List<CarritoQuery> result = new ArrayList<CarritoQuery>();
		
		for (Long key : carrito.keySet())
		{
			Producto p;
			try {
				p = productoBean.getProducto(key);
				result.add(new CarritoQuery(key, carrito.get(key), p.getDescripcion()));
			} catch (DataNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		
		return result;
	}
	
	
	
}
