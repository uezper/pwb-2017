package py.una.pol.iin.pwb.mybatis;

import java.util.List;
import java.util.Set;

import py.una.pol.iin.pwb.model.DetalleVenta;

public interface DetalleVentaMapper {
	public void insertDetalleVenta(DetalleVenta detalleVenta);
	public void updateDetalleVenta(DetalleVenta detalleVenta);
	public void deleteDetalleVentaById(Long id);
	public List<DetalleVenta> findAllDetalleVentas();
	public DetalleVenta findDetalleVentaById(Long id);
	public Set<DetalleVenta> findDetalleVentaFromVenta(Long id);
}
