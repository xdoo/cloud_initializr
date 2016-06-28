package com.catify.initializr.domain;

import com.catify.initializr.ServiceCreatorApplication;
import io.restassured.RestAssured;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static io.restassured.RestAssured.*;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 *
 * @author claus
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ServiceCreatorApplication.class)
@WebAppConfiguration 
@IntegrationTest("server.port=0")
public class ValidationIntegrationTest {
    
    @Value("${local.server.port}")
    private int port;
    
    @Before
    public void setUp() {
        RestAssured.port = this.port;
    }
    
    @Test
    public void testHttpDomainOk() {
        
        given().
                contentType(ContentType.JSON).
                body(this.getValidDomain()).
        when().
                post("/zip").
        then().
                statusCode(HttpStatus.SC_OK);
    }
    
    @Test
    public void testHttpDomainNotOk() {
        Domain d = this.getValidDomain();
        d.setPath("com example.foo");
        
        given().
                contentType(ContentType.JSON).
                body(d).
        when().
                post("/zip").
        then().
                statusCode(HttpStatus.SC_BAD_REQUEST);
    }
    
    @Test
    public void testSample() {
        
        when().get("/sample").then().statusCode(HttpStatus.SC_OK);
    }
    
    private Domain getValidDomain() {
        Domain domain = new Domain("My Domain", "com.example.foo");
        domain.addService(new MicroService("service-a"));
        domain.addService(new MicroService("service-b"));
        
        return domain;
    }
    
}
