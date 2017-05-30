package py.una.pol.iin.pwb.rest;

import static com.jayway.restassured.RestAssured.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import py.una.pol.iin.pwb.model.Cliente;
import py.una.pol.iin.pwb.model.DetalleVenta;
import py.una.pol.iin.pwb.model.Producto;
import py.una.pol.iin.pwb.model.Venta;

public class VentaResourceTest extends RestTestSetup {
	
   @Rule
   public TemporaryFolder tempFolder = new TemporaryFolder();
	
	@Test
	public void getVentaTest(){
    	// Given
		Cliente createdCliente = given().auth().oauth2(token).contentType("application/json")
			.body(new Cliente(0L, "Alan Brado", "1234567890", 0.0))
	        .when().post("/clientes").as(Cliente.class);
    	Producto createdProducto1 = given().auth().oauth2(token).contentType("application/json")
			.body(new Producto(null, "Lapiz", 100))
	        .when().post("/productos").as(Producto.class);
    	Producto createdProducto2 = given().auth().oauth2(token).contentType("application/json")
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
		venta.setDetalles(detalles);
		
		Venta createdVenta = given().auth().oauth2(token).contentType("application/json").body(venta).when().post("/ventas").as(Venta.class);
    	
		RequestSpecification getRequest = given().auth().oauth2(token);
		
    	// When
		Response response = getRequest.when().get("/ventas/" + createdVenta.getId());
    	
    	// Then
		response.then().statusCode(200);
		Venta responseVenta = response.as(Venta.class);
		assertNotNull(responseVenta);
		assertEquals(responseVenta.getId(), createdVenta.getId());
		assertEquals(responseVenta.getDetalles().length, 2);
	}
	
	@Test
	public void getAllVentasTest(){
    	// Given
    	RequestSpecification getRequest = given().auth().oauth2(token);
    	
    	// When
    	Response response = getRequest.when().get("/ventas");
    	
    	// Then
    	response.then().statusCode(200);
    	List<Venta> ventas = Arrays.asList(response.as(Venta[].class));
    	assertNotNull(ventas);
	}
	
	@Test
	public void venderTest(){
    	// Given
		Cliente createdCliente = given().auth().oauth2(token).contentType("application/json")
			.body(new Cliente(0L, "Alan Brado", "1234567890", 0.0))
	        .when().post("/clientes").as(Cliente.class);
    	Producto createdProducto1 = given().auth().oauth2(token).contentType("application/json")
			.body(new Producto(null, "Lapiz", 100))
	        .when().post("/productos").as(Producto.class);
    	Producto createdProducto2 = given().auth().oauth2(token).contentType("application/json")
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
		venta.setDetalles(detalles);
		
		RequestSpecification ventaRequest = given().auth().oauth2(token).contentType("application/json").body(venta);
    	
    	// When
		Response response = ventaRequest.when().post("/ventas");
    	
    	// Then
		response.then().statusCode(200);
		Venta responseVenta = response.as(Venta.class);
		assertNotNull(responseVenta);
		assertNotNull(responseVenta.getId());
		assertEquals(responseVenta.getDetalles().length, 2);
		assertTrue(Math.abs(responseVenta.getMontoTotal() - (7 * 1000.0 + 8 * 200.0)) < 0.0001);
	}
	
	@Test
	public void venderWithFileTest(){
    	// Given
		Cliente createdCliente = given().auth().oauth2(token).contentType("application/json")
			.body(new Cliente(0L, "Alan Brado", "1234567890", 0.0))
	        .when().post("/clientes").as(Cliente.class);
    	Producto createdProducto1 = given().auth().oauth2(token).contentType("application/json")
			.body(new Producto(null, "Lapiz", 100))
	        .when().post("/productos").as(Producto.class);
    	Producto createdProducto2 = given().auth().oauth2(token).contentType("application/json")
			.body(new Producto(null, "Borrador", 110))
	        .when().post("/productos").as(Producto.class);
    	
    	File tempFile = null;
    	try{
    		tempFile = tempFolder.newFile("Test_REST_Venta.txt");
			FileUtils.writeStringToFile(tempFile, "[" +
				"{" +
					"\"clienteId\":" + createdCliente.getId() + "," +
					"\"detalles\":[" +
					"{" +
					"\"productoId\":" + createdProducto1.getId() + "," +
						"\"cantidad\":7," +
						"\"precioUnitario\":1000" +
					"}," +
					"{" +
					"\"productoId\":" + createdProducto2.getId() + "," +
						"\"cantidad\":8," +
						"\"precioUnitario\":100" +
					"}" +
					"]" +
	    		"}," +
				"{" +
					"\"clienteId\":" + createdCliente.getId() + "," +
					"\"detalles\":[" +
					"{" +
					"\"productoId\":" + createdProducto1.getId() + "," +
						"\"cantidad\":3," +
						"\"precioUnitario\":1000" +
					"}," +
					"{" +
					"\"productoId\":" + createdProducto2.getId() + "," +
						"\"cantidad\":4," +
						"\"precioUnitario\":100" +
					"}" +
					"]" +
	    		"}" +
	    		"]");
		}catch(Exception e){}
	    
    	RequestSpecification ventaRequest = given().auth().oauth2(token).multiPart(tempFile);
    	
    	// When
    	Response response = ventaRequest.post("/ventas");

    	// Then
    	response.then().statusCode(200);
	}

}
