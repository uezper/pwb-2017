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

import py.una.pol.iin.pwb.model.Proveedor;
import py.una.pol.iin.pwb.model.DetalleCompra;
import py.una.pol.iin.pwb.model.Producto;
import py.una.pol.iin.pwb.model.Compra;

public class CarritoCompraResourceTest extends RestTestSetup{
	
	@Test
	public void comprar(){
    	// Given
		Proveedor createdProveedor = given().auth().oauth2(token).contentType("application/json")
			.body(new Proveedor(null, "Alan Brado", "1234567890"))
	        .when().post("/proveedores").as(Proveedor.class);
    	Producto createdProducto1 = given().auth().oauth2(token).contentType("application/json")
			.body(new Producto(null, "Lapiz", 100))
	        .when().post("/productos").as(Producto.class);
    	Producto createdProducto2 = given().auth().oauth2(token).contentType("application/json")
			.body(new Producto(null, "Borrador", 110))
	        .when().post("/productos").as(Producto.class);
    	
    	DetalleCompra detalle1 = new DetalleCompra();
    	detalle1.setProductoId(createdProducto1.getId());
    	detalle1.setCantidad(7);
    	detalle1.setPrecioUnitario(1000.0);
    	DetalleCompra detalle2 = new DetalleCompra();
    	detalle2.setProductoId(createdProducto2.getId());
    	detalle2.setCantidad(8);
    	detalle2.setPrecioUnitario(200.0);

    	DetalleCompra[] detalles = {detalle1, detalle2};
		
		Compra compra = new Compra();
		compra.setProveedorId(createdProveedor.getId());
		
		// When
		Response response = given().auth().oauth2(token).contentType("application/json")
			.body(compra)
			.when().post("/carritoCompra/crear");
		response.then().statusCode(200);
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("session-key", response.getHeader("session-key"));

		response = given().auth().oauth2(token).contentType("application/json")
			.headers(headers).body(detalles)
			.when().post("/carritoCompra/agregarProducto");

		response = given().auth().oauth2(token).contentType("application/json").headers(headers).when().post("/carritoCompra/finalizar");
		
		// Then
		response.then().statusCode(200);
		Compra respondeCompra = response.as(Compra.class);
	}
}
