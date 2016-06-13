package com.catify.initializr.services;

import com.catify.initializr.domain.Domain;
import com.catify.initializr.domain.MicroService;
import java.nio.file.FileSystem;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author claus
 */
@Service
public class DomainGeneratorService {
    
    private ServiceGeneratorService serviceGen;

    @Autowired
    public DomainGeneratorService(ServiceGeneratorService serviceGen) {
        this.serviceGen = serviceGen;
    }
    
    public void createDomain(Domain domain, FileSystem fs) {
        
        // create services
        List<MicroService> services = domain.getServices();
        services.stream().forEach((service) -> {
            int x = domain.getServices().indexOf(service);
            this.serviceGen.createService(domain, x, fs);
        });
        
    }
    
    
}
