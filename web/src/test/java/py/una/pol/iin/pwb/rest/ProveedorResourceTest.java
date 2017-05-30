package py.una.pol.iin.pwb.rest;

import static com.jayway.restassured.RestAssured.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import py.una.pol.iin.pwb.model.Proveedor;

public class ProveedorResourceTest extends RestTestSetup {
	
    @Test
	public void getAllProveedoresTest(){
    	// Given
    	RequestSpecification getRequest = given();
    	
    	// When
    	Response response = getRequest.when().get("/proveedores");
    	
    	// Then
    	response.then().statusCode(200);
    	List<Proveedor> proveedores = Arrays.asList(response.as(Proveedor[].class));
    	assertNotNull(proveedores);
	}
    
    @Test
    public void postProveedorTest(){
    	// Given
        RequestSpecification postRequest = given()
        .contentType("application/json")
        .body(new Proveedor(null, "Alan Brado", "1234567890"));
        
    	// When
        Response response = postRequest.when().post("/proveedores");

    	// Then
    	response.then()
        .statusCode(200);
    	Proveedor responseProveedor = response.as(Proveedor.class);
        assertNotNull(responseProveedor);
        assertNotNull(responseProveedor.getId());
        assertEquals(responseProveedor.getNombre(), "Alan Brado");
    }
    
    @Test
    public void getProveedorTest(){
    	// Given
    	Proveedor createdProveedor = given().contentType("application/json")
		.body(new Proveedor(null, "Alan Brado", "1234567890"))
        .when().post("/proveedores").as(Proveedor.class);
    	
    	RequestSpecification getRequest = given();

    	// When
    	Response response = getRequest.when().get("/proveedores/" + createdProveedor.getId());
    	
    	// Then
    	response.then().statusCode(200);
    	Proveedor responseProveedor = response.as(Proveedor.class);
    	assertNotNull(responseProveedor);
    }
    
    @Test
    public void putProveedorTest(){
    	// Given
        Proveedor createdProveedor = given().contentType("application/json")
		.body(new Proveedor(null, "Alan Brado", "1234567890"))
        .when().post("/proveedores").as(Proveedor.class);
    	
    	RequestSpecification putRequest = given().contentType("application/json")
		.body(new Proveedor(null, "Elen Brado", "0987654321"));

    	// When
    	Response response = putRequest.when().put("/proveedores/" + createdProveedor.getId());
    	
    	// Then
    	response.then().statusCode(200);
    	Proveedor responseProveedor = response.as(Proveedor.class);
    	assertNotNull(responseProveedor);
    	assertEquals(responseProveedor.getNombre(), "Elen Brado");
    	assertEquals(responseProveedor.getTelefono(), "0987654321");
    }
    
    @Test
    public void deleteProveedorTest(){
    	// Given
    	Proveedor createdProveedor = given().contentType("application/json").body(new Proveedor(null, "Alan Brado", "1234567890"))
        .when().post("/proveedores").as(Proveedor.class);
    	
    	RequestSpecification deleteRequest = given();

    	// When
    	Response response = deleteRequest.when().delete("/proveedores/" + createdProveedor.getId());
    	
    	// Then
    	response.then().statusCode(204);
		given().when().get("proveedores/" + createdProveedor.getId()).then().statusCode(404);
    }
}
