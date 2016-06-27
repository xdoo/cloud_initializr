package com.catify.initializr.services;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author claus
 */
public class Util {
    
    public static String cleanPackageName(String packageName) {
        return packageName.replaceAll("\\.", "/");
    }
    
    public static String createServiceName(String name) {
        return name.replaceAll("-", "").replaceAll("_", "").replaceAll("\\.", "").toLowerCase();
    }
    
    public static String createServiceId(String name) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name.replaceAll("-", "_"));
    }
    
    public static void writeToFile(String content, Path path) {
        try {
            Files.write(path, ImmutableList.of(content), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Logger.getLogger(ServiceGeneratorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
