package py.una.pol.iin.pwb.mybatis;

import java.util.List;

import py.una.pol.iin.pwb.model.Producto;

public interface ProductoMapper {
	public void insertProducto(Producto producto);
	public void updateProducto(Producto producto);
	public void deleteProductoById(Long id);
	public List<Producto> findAllProductos();
	public Producto findProductoById(Long id);
}
