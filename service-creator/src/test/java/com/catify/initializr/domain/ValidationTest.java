package com.catify.initializr.domain;

import org.junit.Test;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 *
 * @author claus
 */
public class ValidationTest {

    
    private Validator validator;
    
    @Before
    public void setUp() {        
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();
    }
    
    @Test
    public void testDomainOk() {
        this.checkValidation(this.getValidDomain(), true);
    }
    
    @Test
    public void testNoService() {
        Domain d = this.getValidDomain();
        d.getServices().clear();
        this.checkValidation(d, false);
    }
    
    @Test
    public void testToManyServices() {
        Domain d = this.getValidDomain();
        for(int i = 0; i<29; i++) {
            d.addService(new MicroService("foo" + i));
        }
        this.checkValidation(d, false);
    }
    
    @Test
    public void testInvalidPath() {
        Domain d = this.getValidDomain();
        
        // space
        d.setPath("com example.foo");
        this.checkValidation(d, false);
        d.setPath("com.example foo");
        this.checkValidation(d, false);
        d.setPath("com.example.foo ");
        this.checkValidation(d, false);
        
        // big character
        d.setPath("com.Example.foo");
        this.checkValidation(d, false);
        
        // minus
        d.setPath("com.example-foo");
        this.checkValidation(d, false);
        
        // underscore
        d.setPath("com.example_foo");
        this.checkValidation(d, false);
    }
    
    @Test
    public void testValidNames() {
        Domain d = this.getValidDomain();
        
        // minus
        d.setName("My-Domain");
        this.checkValidation(d, true);
        
        // underscore
        d.setName("My_Domain");
        this.checkValidation(d, true);
        
        // camel case
        d.setName("MyDomain");
        this.checkValidation(d, true);
        
        // dot
        d.setName("My.Domain");
        this.checkValidation(d, true);
        
        // small
        d.setName("my domain");
        this.checkValidation(d, true);
    }
    
    private void checkValidation(Domain domain, boolean ok) {
        Set<ConstraintViolation<Domain>> violations = this.validator.validate(domain);
        violations.stream().forEach((violation) -> {
            System.out.println(">>>>>>>> " + violation.getMessage());
        });
        assertEquals(ok, violations.isEmpty());
    }
     
    private Domain getValidDomain() {
        Domain domain = new Domain("My Domain", "com.example.foo");
        domain.addService(new MicroService("service-a"));
        domain.addService(new MicroService("service-b"));
        
        return domain;
    }
    
}
