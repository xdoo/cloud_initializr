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
    private List<Service> services;

    public Domain() {
        this.services = new ArrayList<>();
    }
    
    public Domain(final String name, final String basePath) {
        this.name = name;
        this.basePath = basePath;
        this.services = new ArrayList<>();
    }

    public Domain(final String name, final String basePath, final List<Service> services) {
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

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }
    
    public void addService(Service service) {
        this.services.add(service);
    }
    
    
}
