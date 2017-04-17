package py.una.pol.iin.pwb.bean;

import java.util.List;

import javax.ejb.Stateless;

import org.apache.ibatis.session.SqlSession;

import py.una.pol.iin.pwb.exception.DataNotFoundException;
import py.una.pol.iin.pwb.exception.InvalidFormatException;
import py.una.pol.iin.pwb.model.Proveedor;
import py.una.pol.iin.pwb.mybatis.ProveedorMapper;
import py.una.pol.iin.pwb.mybatis.MyBatisUtil;
import py.una.pol.iin.pwb.validator.CustomValidator;

@Stateless
public class ProveedorBean implements IProveedorBean {

	@Override
	public List<Proveedor> getAllProveedores() throws Exception {
				
		SqlSession session = MyBatisUtil.getSession();
		ProveedorMapper proveedorMapper = session.getMapper(ProveedorMapper.class);
		List<Proveedor> proveedores = null;		
		proveedores = proveedorMapper.findAllProveedores();
		session.close();
			
		return proveedores;		
	}

	@Override
	public Proveedor getProveedor(Long id) throws DataNotFoundException, Exception {
		
		SqlSession session = MyBatisUtil.getSession();
		ProveedorMapper proveedorMapper = session.getMapper(ProveedorMapper.class);
		Proveedor proveedor = null;		
		proveedor = proveedorMapper.findProveedorById(id);
		session.close();			
		
		if (proveedor == null)
			throw new DataNotFoundException("El proveedor con id " + id + " no existe");
		
		
		return proveedor;
	}

	@Override
	public Proveedor addProveedor(Proveedor proveedor) throws InvalidFormatException, Exception {

		CustomValidator.validateAndThrow(proveedor);
		
		SqlSession session = MyBatisUtil.getSession();
		ProveedorMapper proveedorMapper = session.getMapper(ProveedorMapper.class);
		proveedorMapper.insertProveedor(proveedor);
		session.close();
		
		return proveedor;
	}

	@Override
	public Proveedor updateProveedor(Proveedor proveedor)  throws DataNotFoundException, InvalidFormatException, Exception {
		CustomValidator.validateAndThrow(proveedor);
		
		SqlSession session = MyBatisUtil.getSession();				
		ProveedorMapper proveedorMapper = session.getMapper(ProveedorMapper.class);
		
		Proveedor proveedor2 = proveedorMapper.findProveedorById(proveedor.getId());
		if (proveedor2 == null) throw new DataNotFoundException("El proveedor con el id " + proveedor.getId() + " no existe");
		
		proveedor2.setNombre(proveedor.getNombre());
		proveedor2.setTelefono(proveedor.getTelefono());
		
		proveedorMapper.updateProveedor(proveedor2);
		session.close();
		
		return proveedor2;
	}

	@Override
	public void removeProveedor(Long id) throws Exception {
		
		SqlSession session = MyBatisUtil.getSession();						
		ProveedorMapper proveedorMapper = session.getMapper(ProveedorMapper.class);
		proveedorMapper.deleteProveedorById(id);
		session.close();
					
	}

}
