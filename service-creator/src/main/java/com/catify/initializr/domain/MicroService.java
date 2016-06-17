package com.catify.initializr.domain;

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
    
}
