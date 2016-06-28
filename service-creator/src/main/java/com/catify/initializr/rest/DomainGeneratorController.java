package com.catify.initializr.rest;

import com.catify.initializr.domain.Domain;
import com.catify.initializr.domain.MicroService;
import com.catify.initializr.services.ZipGeneratorService;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author claus
 */
@RestController
public class DomainGeneratorController {
    
    private static final Logger LOG= Logger.getLogger( DomainGeneratorController.class.getName() );
    
    private final ZipGeneratorService zip;

    @Autowired
    public DomainGeneratorController(ZipGeneratorService zip) {
        this.zip = zip;
    }
    
    /**
     * Create cloud project.
     * 
     * @param domain
     * @return 
     */
    @RequestMapping(value = "/zip", method = RequestMethod.POST, produces = "application/zip")
    public byte[] createDomain(@Valid @RequestBody Domain domain) {
        LOG.log(Level.INFO, "request to create domain");
        return zip.zipFileSystem(domain);
    }
    
    /**
     * Get sample structure of a cloud project.
     * 
     * @return 
     */
    @RequestMapping(value = "/sample", method = RequestMethod.GET)
    public Domain createSample() {
        LOG.log(Level.INFO, "request to create sample");
        Domain domain = new Domain("My-Domain", "com.my.domain");
        domain.addService(new MicroService("service-a"));
        domain.addService(new MicroService("service-b"));
        return domain;
    }
    
    /**
     * Handle bean validation exceptions.
     * 
     * @param e
     * @return 
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException e) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        StringBuilder msg = new StringBuilder("Validation of your payload failed due to the following errors: \n\n");
        errors.stream().forEach((error) -> {
            msg.append(error.getDefaultMessage()).append("\n");
        });
        return new ResponseEntity<>(msg.toString(), HttpStatus.BAD_REQUEST);
    }
    
}
