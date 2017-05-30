package py.una.pol.iin.pwb.rest;

import org.junit.BeforeClass;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

import static com.jayway.restassured.RestAssured.*;

public class RestTestSetup {
	private final static int DEFAULT_PORT = 8080;
	private final static String DEFAULT_BASE_PATH = "/pwb-web/rest";
	private final static String DEFAULT_BASE_HOST = "http://localhost";
	private final static String DEFAULT_AUTH_URL = "http://localhost:8081/auth/realms/demo/protocol/openid-connect/token";
	private final static String DEFAULT_TESTUSER_USERNAME = "TestUser";
	private final static String DEFAULT_TESTUSER_PASSWORD = "78324n0v98s8d-9gf2341jok";
	
	static String token = "";
	
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

        String authURL = System.getProperty("auth.url");
        if(authURL==null){
        	authURL = DEFAULT_AUTH_URL;
        }
        
        String username = System.getProperty("test.user");
        if(username==null){
        	username = DEFAULT_TESTUSER_USERNAME;
        }

        String password = System.getProperty("test.password");
        if(password==null){
        	password = DEFAULT_TESTUSER_PASSWORD;
        }
        System.out.println("USR:[" + username + "]password:[" + password + "]");
        
        token = given().contentType("application/x-www-form-urlencoded")
        .body("username=" + username + "&password=" + password + "&client_id=pwb" + "&grant_type=password")
        .when().post(authURL)
        .then().statusCode(200).extract().path("access_token");
    }
}
