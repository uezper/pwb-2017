package py.una.pol.iin.pwb.bean;

import java.util.List;

import javax.ejb.Local;

import py.una.pol.iin.pwb.exception.DataNotFoundException;
import py.una.pol.iin.pwb.exception.InvalidFormatException;
import py.una.pol.iin.pwb.model.Producto;

@Local
public interface IVulnerableProductoBean {
	
	public List<Producto> getAllProductos();
	public Producto getProducto(String id)  throws DataNotFoundException;
	public Producto addProducto(Producto producto) throws InvalidFormatException;
	public Producto updateProducto(Producto producto)  throws InvalidFormatException;
	public void removeProducto(String id);
	
}
