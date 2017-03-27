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
import py.una.pol.iin.pwb.model.Cliente;
import py.una.pol.iin.pwb.model.DetalleVenta;
import py.una.pol.iin.pwb.model.Producto;
import py.una.pol.iin.pwb.model.Venta;
import py.una.pol.iin.pwb.repository.VentaRepository;
import py.una.pol.iin.pwb.util.FileVentaParser;
import py.una.pol.iin.pwb.validator.CustomValidator;

@Stateless
@TransactionManagement(value=TransactionManagementType.BEAN)
public class VentaBean implements IVentaBean {

	@Inject
	EntityManager em;
	@Inject
	IProductoBean productoBean;
	@Inject
	IClienteBean clienteBean;
	@Inject
	VentaRepository ventaRepository;
	
	
	@Resource
	UserTransaction userTransaction;
	
	
	@Override
	public Venta addVenta(Venta venta) throws InvalidFormatException, InvalidArgumentException, Exception {								
		
		try {
			
			boolean commit = false;
			if (userTransaction.getStatus() == Status.STATUS_NO_TRANSACTION)
			{
				userTransaction.begin();
				commit = true;
			}
			
			CustomValidator.validateAndThrow(venta);
			Cliente cliente = clienteBean.getCliente(venta.getClienteId());
			
			
			
			double total = 0;
			for (DetalleVenta detalleVenta : venta.getDetalles())
			{			
				detalleVenta.setVenta(venta);							
				CustomValidator.validateAndThrow(detalleVenta);
				
							
				Producto producto = productoBean.getProducto(detalleVenta.getProductoId());		
				if (detalleVenta.getCantidad() > producto.getCantidad()) {
					throw new InvalidArgumentException("La cantidad vendida no puede superar a la cantidad en stock");
				}																
			}
			
		
			
			venta.setCliente(cliente);
			em.persist(venta);			
			
			for (DetalleVenta detalleVenta : venta.getDetalles())
			{
				Producto producto = productoBean.getProducto(detalleVenta.getProductoId());
				
				detalleVenta.setVenta(venta);
				detalleVenta.setProducto(producto);
				em.persist(detalleVenta);
				
				total += detalleVenta.getPrecioUnitario() * detalleVenta.getCantidad();
																
				producto.setCantidad(producto.getCantidad() - detalleVenta.getCantidad());
				em.merge(producto);
			}
			
			
			cliente.setDeuda(cliente.getDeuda() + total);
			venta.setMontoTotal(total);
			
			
			em.merge(cliente);
			em.merge(venta);
			em.flush();
			em.refresh(venta);			
			
			if (commit)
			{
				userTransaction.commit();
			}
			
			return venta;
			
		} catch(DataNotFoundException e)
		{
			throw new InvalidArgumentException(e.getMessage());
		}
		
		
		
	}

	
	
	@Override
	public boolean addVentasFromFile(String path) throws InvalidFormatException, InvalidArgumentException, Exception {			
		
		try {
			
			userTransaction.begin();
			
			FileVentaParser fp = new FileVentaParser(path);
			Object o;
			
			Venta venta = null;
			DetalleVenta detalleVenta = null;
			ArrayList<DetalleVenta> detalles = null;
			
			while((o = fp.nextObject()) != null)
			{
				if (o instanceof Venta)
				{
					if (venta == null)
					{
						venta = (Venta)o;		
						detalles = new ArrayList<>();
					} else {						
						DetalleVenta[] detalles_array = new DetalleVenta[detalles.size()];
						detalles_array = detalles.toArray(detalles_array);
						venta.setDetalles(detalles_array);
						addVenta(venta);
						
						venta = (Venta)o;
						detalles = new ArrayList<>();
					}
					
				}
				else if (o instanceof DetalleVenta)
				{
					detalleVenta = (DetalleVenta)o;
					
					if (venta != null)
					{
						detalles.add(detalleVenta);
					}
				}
			}
			if (venta != null)
			{
				DetalleVenta[] detalles_array = new DetalleVenta[detalles.size()];
				detalles_array = detalles.toArray(detalles_array);
				venta.setDetalles(detalles_array);
				addVenta(venta);
				venta = null;
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
	public Entry<ArrayList<Venta>, Integer> getAllVentas(int offset_rows, int num_ventas) {
				
		int limit_rows = 50;
		int ventas_count = 0;
		int last_offset = offset_rows;
		
		HashMap<Long, Venta> ventas = new HashMap<>();
		List<Venta> result;
		
			
		while (true) {			
			
			result = ventaRepository.findAllVentas(offset_rows, limit_rows);									
			
			if (result.size() == 0) break;
			
			for (Venta venta : result)
			{		
				if (!ventas.containsKey(venta.getId()))
				{
					if (ventas_count < num_ventas) {
						last_offset += venta.getDetalles().length;
						
						ventas.put(venta.getId(), venta);
					}
					ventas_count++;
										
				} else {
						Venta existingVenta = ventas.get(venta.getId());
						ArrayList<DetalleVenta> detalles = 
								new ArrayList<>(Arrays.asList(existingVenta.getDetalles()));
						
						for (DetalleVenta detalleVenta : venta.getDetalles())
						{
							detalles.add(detalleVenta);
						}
						
						DetalleVenta[] detalles_array = new DetalleVenta[detalles.size()];
						detalles_array = detalles.toArray(detalles_array);
						existingVenta.setDetalles(detalles_array);
						
						last_offset += venta.getDetalles().length;
						
						
						ventas.put(existingVenta.getId(), existingVenta);
				}
				
				if (ventas_count > num_ventas) break;
			}
			if (ventas_count > num_ventas) break;
			
			offset_rows = last_offset;
		}
		
		ArrayList<Venta> ventas_list = new ArrayList<>(ventas.values());
				
		return new SimpleEntry(ventas_list, last_offset);
	}

	
	
	
}
