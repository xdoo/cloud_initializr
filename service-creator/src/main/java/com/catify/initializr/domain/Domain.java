package com.catify.initializr.domain;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author claus
 */
public class Domain {
    
    private String name;
    private String basePath;
    private List<MicroService> services;

    public Domain() {
        this.services = new ArrayList<>();
    }
    
    public Domain(final String name, final String basePath) {
        this.name = name;
        this.basePath = basePath;
        this.services = new ArrayList<>();
    }

    public Domain(final String name, final String basePath, final List<MicroService> services) {
        this.name = name;
        this.basePath = basePath;
        this.services = services;
    }
    
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public List<MicroService> getServices() {
        return services;
    }

    public void setServices(List<MicroService> services) {
        this.services = services;
    }
    
    public void addService(MicroService service) {
        this.services.add(service);
    }
    
    
}
