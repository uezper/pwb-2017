package py.una.pol.iin.pwb.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CarritoQuery {
		
	@NotNull(message="El campo 'productoId' no puede ser nulo")
	private Long productoId;
	@NotNull(message="El campo 'productoCantidad' no puede ser nulo")
	@Min(value=1, message="El valor del campo 'productoCantidad' debe ser positivo")
	private Integer productoCantidad;
	private String descripcion;
	
	public CarritoQuery() {		
	}

	
	
	public CarritoQuery(Long productoId, Integer productoCantidad, String descripcion) {
		super();
		this.productoId = productoId;
		this.productoCantidad = productoCantidad;
		this.descripcion = descripcion;
	}



	public Long getProductoId() {
		return productoId;
	}

	public void setProductoId(Long productoId) {
		this.productoId = productoId;
	}

	public Integer getProductoCantidad() {
		return productoCantidad;
	}

	public void setProductoCantidad(Integer productoCantidad) {
		this.productoCantidad = productoCantidad;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
}