package py.una.pol.iin.pwb.bean;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import py.una.pol.iin.pwb.exception.DataNotFoundException;
import py.una.pol.iin.pwb.exception.InvalidFormatException;
import py.una.pol.iin.pwb.model.Proveedor;
import py.una.pol.iin.pwb.repository.ProveedorRepository;
import py.una.pol.iin.pwb.validator.CustomValidator;

@Stateless
public class ProveedorBean implements IProveedorBean {

	@Inject
	EntityManager em;
	@Inject
	ProveedorRepository proveedorRepository;
	
	@Override
	public List<Proveedor> getAllProveedores() {
		return proveedorRepository.findAllProveedores();
	}

	@Override
	public Proveedor getProveedor(Long id) throws DataNotFoundException {
		Proveedor proveedor = proveedorRepository.findProveedorById(id);
		if (proveedor == null)
			throw new DataNotFoundException("El proveedor con id " + id + " no existe");
		return proveedor;
	}

	@Override
	public Proveedor addProveedor(Proveedor proveedor) throws InvalidFormatException {
		CustomValidator.validateAndThrow(proveedor);
		em.persist(proveedor);
		em.flush();
		em.refresh(proveedor);
		return proveedor;
	}

	@Override
	public Proveedor updateProveedor(Proveedor proveedor)  throws DataNotFoundException, InvalidFormatException {
		CustomValidator.validateAndThrow(proveedor);		
		Proveedor proveedor2 = proveedorRepository.findProveedorById(proveedor.getId());
		if (proveedor2 == null) throw new DataNotFoundException("El proveedor con el id " + proveedor.getId() + " no existe");
		
		proveedor2.setNombre(proveedor.getNombre());
		proveedor2.setTelefono(proveedor.getTelefono());
		
		em.merge(proveedor2);
		em.flush();
		em.refresh(proveedor2);
		return proveedor2;
	}

	@Override
	public void removeProveedor(Long id) {
		Proveedor proveedor = proveedorRepository.findProveedorById(id);
		if (proveedor != null) em.remove(proveedor);		
	}

}
