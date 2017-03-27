package py.una.pol.iin.pwb.bean;

import java.util.ArrayList;
import java.util.Map.Entry;

import javax.ejb.Local;

import py.una.pol.iin.pwb.exception.InvalidArgumentException;
import py.una.pol.iin.pwb.exception.InvalidFormatException;
import py.una.pol.iin.pwb.model.Venta;

@Local
public interface IVentaBean {
	public Venta addVenta(Venta venta) throws InvalidFormatException, InvalidArgumentException;
	public boolean addVentasFromFile(String path) throws InvalidFormatException, InvalidArgumentException;
	public Entry<ArrayList<Venta>, Integer> getAllVentas(int offset_rows, int num_ventas);
}
