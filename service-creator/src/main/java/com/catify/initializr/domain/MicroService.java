package com.catify.initializr.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import com.catify.initializr.services.Util;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author claus
 */
public class MicroService {
    
    @NotNull @Pattern(regexp = "([A-Za-z]*\\.?\\-?\\_?[[:blank:]]?)*")
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
    
    @JsonIgnore
    public String getServiceId() {
        return Util.createServiceId(this.name);
    }
    
    @JsonIgnore
    public String getServiceName() {
        return Util.createServiceName(this.name);
    }
    
}
