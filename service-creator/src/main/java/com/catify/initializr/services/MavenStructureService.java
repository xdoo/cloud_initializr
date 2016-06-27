package com.catify.initializr.services;

import com.catify.initializr.domain.Domain;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
    
    
    public Map<String,Path> createEmptyMavenProject(FileSystem fs, Domain domain, String name) {
        
        // paths
        Map<String,Path> paths = this.createMavenPaths(fs,
                domain, 
                Util.createServiceName(name));
        
        // create directories
        this.createDirectories(paths);
        
        return paths;
    }
    
    public Map<String,Path> createMavenPaths(FileSystem fs, Domain domain, String name) {
        Map<String,Path> paths = new HashMap<>();
        
        String domainPath = "/" + Util.cleanPackageName(domain.getPath());
        String basePath = String.format("/%s/%s", Util.createServiceName(domain.getName()), name);
        
        paths.put(BASE, fs.getPath(basePath));
        paths.put(MAIN_JAVA, fs.getPath(basePath + "/" + main + "/java" + domainPath + "/" + name));
        paths.put(MAIN_RESOURCES, fs.getPath(basePath + "/" + main + "/resources"));
        paths.put(MAIN_DOCKER, fs.getPath(basePath + "/" + main + "/docker"));
        paths.put(TEST_JAVA, fs.getPath(basePath + "/" + test + "/java" + domainPath + "/" + name));
        paths.put(TEST_RESOURCES, fs.getPath(basePath + "/" + test + "/resources"));
        
        return paths;
    }
    
    public void createDirectories(Map<String,Path> paths) {
        Collection<Path> values = paths.values();
        values.stream().forEach((path) -> {
            Util.createDirectory(path);
        });
    }    
}
