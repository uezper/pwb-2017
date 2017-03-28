package py.una.pol.iin.pwb.bean;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import py.una.pol.iin.pwb.exception.DataNotFoundException;
import py.una.pol.iin.pwb.exception.InvalidArgumentException;
import py.una.pol.iin.pwb.exception.InvalidFormatException;
import py.una.pol.iin.pwb.model.Proveedor;
import py.una.pol.iin.pwb.model.DetalleCompra;
import py.una.pol.iin.pwb.model.Producto;
import py.una.pol.iin.pwb.model.Compra;
import py.una.pol.iin.pwb.repository.CompraRepository;
import py.una.pol.iin.pwb.util.FileCompraParser;
import py.una.pol.iin.pwb.validator.CustomValidator;

@Stateless
@TransactionManagement(value=TransactionManagementType.BEAN)
public class CompraBean implements ICompraBean {

	@Inject
	EntityManager em;
	@Inject
	IProductoBean productoBean;
	@Inject
	IProveedorBean proveedorBean;
	@Inject
	CompraRepository compraRepository;
	
	
	@Resource
	UserTransaction userTransaction;
	
	
	@Override
	public Compra addCompra(Compra compra) throws InvalidFormatException, InvalidArgumentException, Exception {								
		
		try {
			
			boolean commit = false;
			if (userTransaction.getStatus() == Status.STATUS_NO_TRANSACTION)
			{
				userTransaction.begin();
				commit = true;
			}
			
			CustomValidator.validateAndThrow(compra);
			Proveedor proveedor = proveedorBean.getProveedor(compra.getProveedorId());
			
			
			
			double total = 0;
			for (DetalleCompra detalleCompra : compra.getDetalles())
			{			
				detalleCompra.setCompra(compra);							
				CustomValidator.validateAndThrow(detalleCompra);
			}
			
		
			
			compra.setProveedor(proveedor);
			em.persist(compra);			
			
			for (DetalleCompra detalleCompra : compra.getDetalles())
			{
				Producto producto = productoBean.getProducto(detalleCompra.getProductoId());
				
				detalleCompra.setCompra(compra);
				detalleCompra.setProducto(producto);
				em.persist(detalleCompra);
				
				total += detalleCompra.getPrecioUnitario() * detalleCompra.getCantidad();
																
				producto.setCantidad(producto.getCantidad() + detalleCompra.getCantidad());
				em.merge(producto);
			}
			
			
			compra.setMontoTotal(total);
			
			
			em.merge(proveedor);
			em.merge(compra);
			em.flush();
			em.refresh(compra);			
			
			if (commit)
			{
				userTransaction.commit();
			}
			
			return compra;
			
		} catch(DataNotFoundException e)
		{
			throw new InvalidArgumentException(e.getMessage());
		}
		
		
		
	}

	
	
	@Override
	public boolean addComprasFromFile(String path) throws InvalidFormatException, InvalidArgumentException, Exception {			
		
		try {
			
			userTransaction.begin();
			
			FileCompraParser fp = new FileCompraParser(path);
			Object o;
			
			Compra compra = null;
			DetalleCompra detalleCompra = null;
			ArrayList<DetalleCompra> detalles = null;
			
			while((o = fp.nextObject()) != null)
			{
				if (o instanceof Compra)
				{
					if (compra == null)
					{
						compra = (Compra)o;		
						detalles = new ArrayList<>();
					} else {						
						DetalleCompra[] detalles_array = new DetalleCompra[detalles.size()];
						detalles_array = detalles.toArray(detalles_array);
						compra.setDetalles(detalles_array);
						addCompra(compra);
						
						compra = (Compra)o;
						detalles = new ArrayList<>();
					}
					
				}
				else if (o instanceof DetalleCompra)
				{
					detalleCompra = (DetalleCompra)o;
					
					if (compra != null)
					{
						detalles.add(detalleCompra);
					}
				}
			}
			if (compra != null)
			{
				DetalleCompra[] detalles_array = new DetalleCompra[detalles.size()];
				detalles_array = detalles.toArray(detalles_array);
				compra.setDetalles(detalles_array);
				addCompra(compra);
				compra = null;
			}
			
		}		
		catch (Exception e) {
			userTransaction.rollback();
			throw new InvalidFormatException(e.getMessage());
		}
		
		if (userTransaction.getStatus() == Status.STATUS_ACTIVE)
		{
			userTransaction.commit();
		}
	
		
		return true;
	}



	@Override
	public Entry<ArrayList<Compra>, Integer> getAllCompras(int offset_rows, int num_compras) {
				
		int limit_rows = 50;
		int compras_count = 0;
		int last_offset = offset_rows;
		
		HashMap<Long, Compra> compras = new HashMap<>();
		List<Compra> result;
		
			
		while (true) {			
			
			result = compraRepository.findAllCompras(offset_rows, limit_rows);									
			
			if (result.size() == 0) break;
			
			for (Compra compra : result)
			{		
				if (!compras.containsKey(compra.getId()))
				{
					if (compras_count < num_compras) {
						last_offset += compra.getDetalles().length;
						
						compras.put(compra.getId(), compra);
					}
					compras_count++;
										
				} else {
						Compra existingCompra = compras.get(compra.getId());
						ArrayList<DetalleCompra> detalles = 
								new ArrayList<>(Arrays.asList(existingCompra.getDetalles()));
						
						for (DetalleCompra detalleCompra : compra.getDetalles())
						{
							detalles.add(detalleCompra);
						}
						
						DetalleCompra[] detalles_array = new DetalleCompra[detalles.size()];
						detalles_array = detalles.toArray(detalles_array);
						existingCompra.setDetalles(detalles_array);
						
						last_offset += compra.getDetalles().length;
						
						
						compras.put(existingCompra.getId(), existingCompra);
				}
				
				if (compras_count > num_compras) break;
			}
			if (compras_count > num_compras) break;
			
			offset_rows = last_offset;
		}
		
		ArrayList<Compra> compras_list = new ArrayList<>(compras.values());
				
		return new SimpleEntry(compras_list, last_offset);
	}

	
	
	
}
