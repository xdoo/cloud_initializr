package com.catify.initializr.domain;

import com.catify.initializr.services.Util;

/**
 *
 * @author claus
 */
public class MicroService {
    
    private String name;

    public MicroService(String name) {
        this.name = name;
    }

    public MicroService() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    } 
    
    public String getServiceId() {
        return Util.createServiceId(this.name);
    }
    
    public String getServiceName() {
        return Util.createServiceName(this.name);
    }
    
}
