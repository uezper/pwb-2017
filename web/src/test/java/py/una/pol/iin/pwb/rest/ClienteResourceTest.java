package py.una.pol.iin.pwb.rest;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.restassured.RestAssured;

public class ClienteResourceTest {
	
	private final static int DEFAULT_PORT = 8080;
	private final static String DEFAULT_BASE_PATH = "/pwb-web/rest";
	private final static String DEFAULT_BASE_HOST = "http://localhost";
	
    @BeforeClass
    public static void setup() {
        String port = System.getProperty("server.port");
        if (port == null) {
            RestAssured.port = Integer.valueOf(DEFAULT_PORT);
        }
        else{
            RestAssured.port = Integer.valueOf(port);
        }

        String basePath = System.getProperty("server.base");
        if(basePath==null){
            basePath = DEFAULT_BASE_PATH;
        }
        RestAssured.basePath = basePath;

        String baseHost = System.getProperty("server.host");
        if(baseHost==null){
            baseHost = DEFAULT_BASE_HOST;
        }
        RestAssured.baseURI = baseHost;
    }

    @Test
	public void esteTestFunciona(){
    	given().when().get("/clientes").then().statusCode(200);
	}
}
