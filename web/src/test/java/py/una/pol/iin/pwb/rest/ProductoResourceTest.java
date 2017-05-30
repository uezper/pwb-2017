package py.una.pol.iin.pwb.rest;

import static com.jayway.restassured.RestAssured.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import py.una.pol.iin.pwb.model.Producto;

public class ProductoResourceTest extends RestTestSetup {
	
    @Test
	public void getAllProductosTest(){
    	// Given
    	RequestSpecification getRequest = given();
    	
    	// When
    	Response response = getRequest.when().get("/productos");
    	
    	// Then
    	response.then().statusCode(200);
    	List<Producto> productos = Arrays.asList(response.as(Producto[].class));
    	assertNotNull(productos);
	}
    
    @Test
    public void postProductoTest(){
    	// Given
    	Producto postProducto = new Producto(null, "Lapiz", 100);
        RequestSpecification postRequest = given()
	        .contentType("application/json")
	        .body(postProducto);
        
    	// When
        Response response = postRequest.when().post("/productos");

    	// Then
    	response.then().statusCode(200);
    	Producto responseProducto = response.as(Producto.class);
        assertNotNull(responseProducto);
        assertNotNull(responseProducto.getId());
        assertEquals(responseProducto.getDescripcion(), "Lapiz");
        assertEquals(responseProducto.getCantidad(), 100);
    }
    
    @Test
    public void getProductoTest(){
    	// Given
    	Producto createdProducto = given().contentType("application/json")
			.body(new Producto(null, "Lapiz", 100))
	        .when().post("/productos").as(Producto.class);
    	
    	RequestSpecification getRequest = given();

    	// When
    	Response response = getRequest.when().get("/productos/" + createdProducto.getId());
    	
    	// Then
    	response.then().statusCode(200);
    	Producto responseProducto = response.as(Producto.class);
    	assertNotNull(responseProducto);
    }
    
    @Test
    public void putProductoTest(){
    	// Given
    	Producto createdProducto = given().contentType("application/json")
			.body(new Producto(null, "Lapiz", 100))
	        .when().post("/productos").as(Producto.class);
    	
    	RequestSpecification putRequest = given().contentType("application/json")
    			.body(new Producto(null, "Borrador", 101));

    	// When
    	Response response = putRequest.when().put("/productos/" + createdProducto.getId());
    	
    	// Then
    	response.then().statusCode(200);
    	Producto responseProducto = response.as(Producto.class);
    	assertNotNull(responseProducto);
    	assertEquals(responseProducto.getDescripcion(), "Borrador");
    	assertEquals(responseProducto.getCantidad(), 101);
    }
    
    @Test
    public void deleteProductoTest(){
    	// Given
    	Producto createdProducto = given().contentType("application/json")
			.body(new Producto(null, "Lapiz", 100))
	        .when().post("/productos").as(Producto.class);
    	
    	RequestSpecification deleteRequest = given();

    	// When
    	Response response = deleteRequest.when().delete("/productos/" + createdProducto.getId());
    	
    	// Then
    	response.then().statusCode(204);
		given().when().get("productos/" + createdProducto.getId()).then().statusCode(404);
    }
}
