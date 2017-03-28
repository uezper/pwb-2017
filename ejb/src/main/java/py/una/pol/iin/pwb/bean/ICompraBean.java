package py.una.pol.iin.pwb.bean;

import java.util.ArrayList;
import java.util.Map.Entry;

import javax.ejb.Local;

import py.una.pol.iin.pwb.exception.InvalidArgumentException;
import py.una.pol.iin.pwb.exception.InvalidFormatException;
import py.una.pol.iin.pwb.model.Compra;

@Local
public interface ICompraBean {
	public Compra addCompra(Compra compra) throws InvalidFormatException, InvalidArgumentException, Exception;
	public boolean addComprasFromFile(String path) throws InvalidFormatException, InvalidArgumentException, Exception;
	public Entry<ArrayList<Compra>, Integer> getAllCompras(int offset_rows, int num_compras);
}
