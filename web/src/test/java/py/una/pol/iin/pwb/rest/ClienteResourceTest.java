package py.una.pol.iin.pwb.rest;

import static com.jayway.restassured.RestAssured.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import py.una.pol.iin.pwb.model.Cliente;

public class ClienteResourceTest extends RestTestSetup {

    @Test
	public void getAllClientesTest(){
    	// Given
    	RequestSpecification getRequest = given();
    	
    	// When
    	Response response = getRequest.when().get("/clientes");
    	
    	// Then
    	response.then().statusCode(200);
    	List<Cliente> clientes = Arrays.asList(response.as(Cliente[].class));
    	assertNotNull(clientes);
	}
    
    @Test
    public void postClienteTest(){
    	// Given
    	Cliente postCliente = new Cliente(null, "Alan Brado", "1234567890", 0.0);
        RequestSpecification postRequest = given()
	        .contentType("application/json")
	        .body(postCliente);
        
    	// When
        Response response = postRequest.when().post("/clientes");

    	// Then
    	response.then().statusCode(200);
    	Cliente responseCliente = response.as(Cliente.class);
        assertNotNull(responseCliente);
        assertNotNull(responseCliente.getId());
        assertEquals(responseCliente.getNombre(), "Alan Brado");
    }
    
    @Test
    public void getClienteTest(){
    	// Given
    	Cliente createdCliente = given().contentType("application/json")
			.body(new Cliente(null, "Alan Brado", "1234567890", 0.0))
	        .when().post("/clientes").as(Cliente.class);
    	
    	RequestSpecification getRequest = given();

    	// When
    	Response response = getRequest.when().get("/clientes/" + createdCliente.getId());
    	
    	// Then
    	response.then().statusCode(200);
    	Cliente responseCliente = response.as(Cliente.class);
    	assertNotNull(responseCliente);
    }
    
    @Test
    public void putClienteTest(){
    	// Given
    	Cliente createdCliente = given().contentType("application/json")
			.body(new Cliente(null, "Alan Brado", "1234567890", 0.0))
	        .when().post("/clientes").as(Cliente.class);
    	
    	RequestSpecification putRequest = given().contentType("application/json")
			.body(new Cliente(null, "Elen Brado", "0987654321", 10.0));

    	// When
    	Response response = putRequest.when().put("/clientes/" + createdCliente.getId());
    	
    	// Then
    	response.then().statusCode(200);
    	Cliente responseCliente = response.as(Cliente.class);
    	assertNotNull(responseCliente);
    	assertEquals(responseCliente.getNombre(), "Elen Brado");
    	assertEquals(responseCliente.getTelefono(), "0987654321");
    	assertTrue(Math.abs(responseCliente.getDeuda() - 10.0) < 0.00001);
    }
    
    @Test
    public void deleteClienteTest(){
    	// Given
    	Cliente createdCliente = given().contentType("application/json")
			.body(new Cliente(null, "Alan Brado", "1234567890", 0.0))
	        .when().post("/clientes").as(Cliente.class);
    	
    	RequestSpecification deleteRequest = given();

    	// When
    	Response response = deleteRequest.when().delete("/clientes/" + createdCliente.getId());
    	
    	// Then
    	response.then().statusCode(204);
		given().when().get("clientes/" + createdCliente.getId()).then().statusCode(404);
    }
}
