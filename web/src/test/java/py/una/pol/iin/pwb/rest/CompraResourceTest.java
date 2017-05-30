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

import py.una.pol.iin.pwb.model.Proveedor;
import py.una.pol.iin.pwb.model.DetalleCompra;
import py.una.pol.iin.pwb.model.Producto;
import py.una.pol.iin.pwb.model.Compra;

public class CompraResourceTest extends RestTestSetup {
	
   @Rule
   public TemporaryFolder tempFolder = new TemporaryFolder();
	
	@Test
	public void getCompraTest(){
    	// Given
		Proveedor createdProveedor = given().auth().oauth2(token).contentType("application/json")
			.body(new Proveedor(0L, "Alan Brado", "1234567890"))
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
		compra.setDetalles(detalles);
		
		Compra createdCompra = given().auth().oauth2(token).contentType("application/json").body(compra).when().post("/compras").as(Compra.class);
    	
		RequestSpecification getRequest = given().auth().oauth2(token);
		
    	// When
		Response response = getRequest.when().get("/compras/" + createdCompra.getId());
    	
    	// Then
		response.then().statusCode(200);
		Compra responseCompra = response.as(Compra.class);
		assertNotNull(responseCompra);
		assertEquals(responseCompra.getId(), createdCompra.getId());
		assertEquals(responseCompra.getDetalles().length, 2);
	}
	
	@Test
	public void getAllComprasTest(){
    	// Given
    	RequestSpecification getRequest = given().auth().oauth2(token);
    	
    	// When
    	Response response = getRequest.when().get("/compras");
    	
    	// Then
    	response.then().statusCode(200);
    	List<Compra> compras = Arrays.asList(response.as(Compra[].class));
    	assertNotNull(compras);
	}
	
	@Test
	public void comprarTest(){
    	// Given
		Proveedor createdProveedor = given().auth().oauth2(token).contentType("application/json")
			.body(new Proveedor(0L, "Alan Brado", "1234567890"))
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
		compra.setDetalles(detalles);
		
		RequestSpecification compraRequest = given().auth().oauth2(token).contentType("application/json").body(compra);
    	
    	// When
		Response response = compraRequest.when().post("/compras");
    	
    	// Then
		response.then().statusCode(200);
		Compra responseCompra = response.as(Compra.class);
		assertNotNull(responseCompra);
		assertNotNull(responseCompra.getId());
		assertEquals(responseCompra.getDetalles().length, 2);
		assertTrue(Math.abs(responseCompra.getMontoTotal() - (7 * 1000.0 + 8 * 200.0)) < 0.0001);
	}
	
	@Test
	public void comprarWithFileTest(){
    	// Given
		Proveedor createdProveedor = given().auth().oauth2(token).contentType("application/json")
			.body(new Proveedor(0L, "Alan Brado", "1234567890"))
	        .when().post("/proveedores").as(Proveedor.class);
    	Producto createdProducto1 = given().auth().oauth2(token).contentType("application/json")
			.body(new Producto(null, "Lapiz", 100))
	        .when().post("/productos").as(Producto.class);
    	Producto createdProducto2 = given().auth().oauth2(token).contentType("application/json")
			.body(new Producto(null, "Borrador", 110))
	        .when().post("/productos").as(Producto.class);
    	
    	File tempFile = null;
    	try{
    		tempFile = tempFolder.newFile("Test_REST_Compra.txt");
			FileUtils.writeStringToFile(tempFile, "[" +
				"{" +
					"\"proveedorId\":" + createdProveedor.getId() + "," +
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
					"\"proveedorId\":" + createdProveedor.getId() + "," +
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
	    
    	RequestSpecification compraRequest = given().auth().oauth2(token).multiPart(tempFile);
    	
    	// When
    	Response response = compraRequest.post("/compras");

    	// Then
    	response.then().statusCode(200);
	}

}
