package py.una.pol.iin.pwb.bean;

import java.util.List;

import javax.ejb.Local;

import py.una.pol.iin.pwb.exception.InvalidArgumentException;
import py.una.pol.iin.pwb.exception.InvalidFormatException;
import py.una.pol.iin.pwb.model.CarritoQuery;

@Local
public interface ICarritoBean {
	
	public List<CarritoQuery> agregarProductos(List<CarritoQuery> carritoQueries) throws InvalidArgumentException, InvalidFormatException;
	public List<CarritoQuery> quitarProductos(List<CarritoQuery> carritoQueries) throws InvalidArgumentException, InvalidFormatException;
	public List<CarritoQuery> getCarrito();
	public String getSessionKey();
	public void setSessionKey(String sessionKey);
}
