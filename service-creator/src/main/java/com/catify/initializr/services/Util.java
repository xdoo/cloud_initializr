package com.catify.initializr.services;

/**
 *
 * @author claus
 */
public class Util {
    
    public static String cleanPackageName(String packageName) {
        return packageName.replaceAll("\\.", "/");
    }
    
    public static String cleanServiceName(String name) {
        return name.replaceAll("-", "").replaceAll("_", "").replaceAll("\\.", "").toLowerCase();
    }
    
}
