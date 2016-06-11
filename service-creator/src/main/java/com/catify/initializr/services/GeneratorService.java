package com.catify.initializr.services;

import freemarker.template.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author claus
 */
@Service
public class GeneratorService {
     
    private Configuration cfg;

    @Autowired
    public GeneratorService(Configuration cfg) {
        this.cfg = cfg;
    }
    
    public void createDomain() {
        
    }
    
}
