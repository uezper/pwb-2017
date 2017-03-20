package py.una.pol.iin.pwb.bean;

import javax.ejb.Local;

import py.una.pol.iin.pwb.exception.InvalidArgumentException;
import py.una.pol.iin.pwb.exception.InvalidFormatException;
import py.una.pol.iin.pwb.model.Compra;

@Local
public interface ICompraBean {
	public Compra addCompra(Compra compra) throws InvalidFormatException, InvalidArgumentException;
}
