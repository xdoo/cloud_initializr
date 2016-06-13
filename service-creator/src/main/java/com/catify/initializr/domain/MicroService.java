package com.catify.initializr.domain;

/**
 *
 * @author claus
 */
public class MicroService {
    
    private String name;
    private String version;

    public MicroService(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public MicroService() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    
    
    
}
