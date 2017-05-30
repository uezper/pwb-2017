package py.una.pol.iin.pwb.rest;

import static com.jayway.restassured.RestAssured.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.jayway.restassured.response.Headers;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import py.una.pol.iin.pwb.model.Cliente;
import py.una.pol.iin.pwb.model.DetalleVenta;
import py.una.pol.iin.pwb.model.Producto;
import py.una.pol.iin.pwb.model.Venta;

public class CarritoVentaResourceTest extends RestTestSetup{
	
	@Test
	public void vender(){
    	// Given
		Cliente createdCliente = given().contentType("application/json")
			.body(new Cliente(null, "Alan Brado", "1234567890", 0.0))
	        .when().post("/clientes").as(Cliente.class);
    	Producto createdProducto1 = given().contentType("application/json")
			.body(new Producto(null, "Lapiz", 100))
	        .when().post("/productos").as(Producto.class);
    	Producto createdProducto2 = given().contentType("application/json")
			.body(new Producto(null, "Borrador", 110))
	        .when().post("/productos").as(Producto.class);
    	
    	DetalleVenta detalle1 = new DetalleVenta();
    	detalle1.setProductoId(createdProducto1.getId());
    	detalle1.setCantidad(7);
    	detalle1.setPrecioUnitario(1000.0);
    	DetalleVenta detalle2 = new DetalleVenta();
    	detalle2.setProductoId(createdProducto2.getId());
    	detalle2.setCantidad(8);
    	detalle2.setPrecioUnitario(200.0);

    	DetalleVenta[] detalles = {detalle1, detalle2};
		
		Venta venta = new Venta();
		venta.setClienteId(createdCliente.getId());
		
		// When
		Response response = given().contentType("application/json")
			.body(venta)
			.when().post("/carritoVenta/crear");
		response.then().statusCode(200);
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("session-key", response.getHeader("session-key"));

		response = given().contentType("application/json")
			.headers(headers).body(detalles)
			.when().post("/carritoVenta/agregarProducto");

		response = given().contentType("application/json").headers(headers).when().post("/carritoVenta/finalizar");
		
		// Then
		response.then().statusCode(200);
		Venta respondeVenta = response.as(Venta.class);
	}
}
