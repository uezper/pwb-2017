package py.una.pol.iin.pwb.bean;

import javax.ejb.Local;

import py.una.pol.iin.pwb.exception.InvalidArgumentException;
import py.una.pol.iin.pwb.exception.InvalidFormatException;
import py.una.pol.iin.pwb.model.Venta;

@Local
public interface IVentaBean {
	public Venta addVenta(Venta venta) throws InvalidFormatException, InvalidArgumentException;
}
