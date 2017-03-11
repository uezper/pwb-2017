package py.una.pol.iin.pwb.bean;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import py.una.pol.iin.pwb.exception.DataNotFoundException;
import py.una.pol.iin.pwb.exception.InvalidFormatException;
import py.una.pol.iin.pwb.model.Producto;
import py.una.pol.iin.pwb.repository.ProductoRepository;
import py.una.pol.iin.pwb.validator.CustomValidator;

@Stateless
public class ProductoBean implements IProductoBean {

	@Inject
	EntityManager em;
	@Inject
	ProductoRepository productoRepository;
	
	@Override
	public List<Producto> getAllProductos() {
		return productoRepository.findAllProductos();
	}

	@Override
	public Producto getProducto(Long id) throws DataNotFoundException {
		Producto producto = productoRepository.findProductoById(id);
		if (producto == null)
			throw new DataNotFoundException("El producto con el id " + id + " no existe" );
		return producto;
	}

	@Override
	public Producto addProducto(Producto producto) throws InvalidFormatException {
		CustomValidator.validateAndThrow(producto);
		em.persist(producto);
		em.flush();
		em.refresh(producto);
		return producto;
	}

	@Override
	public Producto updateProducto(Producto producto)  throws DataNotFoundException, InvalidFormatException {
		CustomValidator.validateAndThrow(producto);		
		Producto producto2 = productoRepository.findProductoById(producto.getId());
		if (producto2 == null) throw new DataNotFoundException("El producto con el id " + producto.getId() + " no existe.");
		
		producto2.setCantidad(producto.getCantidad());
		producto2.setDescripcion(producto.getDescripcion());
		
		em.merge(producto2);
		em.flush();
		em.refresh(producto2);
		return producto2;
	}

	@Override
	public void removeProducto(Long id) {
		Producto producto = productoRepository.findProductoById(id);
		if (producto != null) em.remove(producto);		
	}

	
}
