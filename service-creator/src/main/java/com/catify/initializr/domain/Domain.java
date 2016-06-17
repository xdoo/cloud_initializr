package com.catify.initializr.domain;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author claus
 */
public class Domain {
    
    private String name;
    private String path;
    private List<MicroService> services;

    public Domain() {
        this.services = new ArrayList<>();
    }
    
    public Domain(final String name, final String basePath) {
        this.name = name;
        this.path = basePath;
        this.services = new ArrayList<>();
    }

    public Domain(final String name, final String path, final List<MicroService> services) {
        this.name = name;
        this.path = path;
        this.services = services;
    }
    
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
