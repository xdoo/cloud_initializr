package com.catify.initializr.services;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * @author claus
 */
@Service
public class MavenStructureService {
    
    private static final Logger LOG= Logger.getLogger( MavenStructureService.class.getName() );
    
    private final String main = "/src/main";
    private final String test = "/src/test";
    
    public static final String BASE = "base_path";
    public static final String MAIN_JAVA = "main_java";
    public static final String MAIN_RESOURCES = "main_resources";
    public static final String MAIN_DOCKER = "main_docker";
    public static final String TEST_JAVA = "test_java";
    public static final String TEST_RESOURCES = "test_resources";
    
    
    public Map<String,Path> createEmptyMavenProject(FileSystem fs, String domain, String name) {
        
        // paths
        Map<String,Path> paths = this.createMavenPaths(fs,
                "/" + Util.cleanPackageName(domain), 
                Util.cleanServiceName(name));
        
        // create directories
        this.createDirectories(paths);
        
        return paths;
    }
    
    public Map<String,Path> createMavenPaths(FileSystem fs, String domain, String name) {
        Map<String,Path> paths = new HashMap<>();
        
        paths.put(BASE, fs.getPath(name));
        paths.put(MAIN_JAVA, fs.getPath(name + "/" + main + "/java" + domain + "/" + name));
        paths.put(MAIN_RESOURCES, fs.getPath(name + "/" + main + "/resources"));
        paths.put(MAIN_DOCKER, fs.getPath(name + "/" + main + "/docker"));
        paths.put(TEST_JAVA, fs.getPath(name + "/" + test + "/java" + domain + "/" + name));
        paths.put(TEST_RESOURCES, fs.getPath(name + "/" + test + "/resources"));
        
        return paths;
    }
    
    public void createDirectories(Map<String,Path> paths) {
        Collection<Path> values = paths.values();
        values.stream().forEach((path) -> {
            try {
                Files.createDirectories(path);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        });
    }    
}
