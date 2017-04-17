package py.una.pol.iin.pwb.mybatis;

import java.util.List;

import py.una.pol.iin.pwb.model.Proveedor;

public interface ProveedorMapper {
	public void insertProveedor(Proveedor proveedor);
	public void updateProveedor(Proveedor proveedor);
	public void deleteProveedorById(Long id);
	public List<Proveedor> findAllProveedores();
	public Proveedor findProveedorById(Long id);
}
