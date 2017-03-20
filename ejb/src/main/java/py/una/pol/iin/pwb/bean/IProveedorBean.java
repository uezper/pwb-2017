package py.una.pol.iin.pwb.bean;

import java.util.List;

import javax.ejb.Local;

import py.una.pol.iin.pwb.exception.DataNotFoundException;
import py.una.pol.iin.pwb.exception.InvalidFormatException;
import py.una.pol.iin.pwb.model.Proveedor;

@Local
public interface IProveedorBean {
	
	public List<Proveedor> getAllProveedores();
	public Proveedor getProveedor(Long id) throws DataNotFoundException, InvalidFormatException;
	public Proveedor addProveedor(Proveedor proveedor) throws InvalidFormatException;
	public Proveedor updateProveedor(Proveedor proveedor)  throws DataNotFoundException, InvalidFormatException;
	public void removeProveedor(Long id);
	
}
