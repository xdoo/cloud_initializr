package com.catify.initializr.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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
    
}
