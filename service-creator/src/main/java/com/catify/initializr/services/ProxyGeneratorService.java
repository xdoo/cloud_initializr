package com.catify.initializr.services;

import com.catify.initializr.domain.Domain;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Create Maven project for proxy service.
 * 
 * @author claus
 */
@Service
public class ProxyGeneratorService extends BaseServiceGeneratorService {

    @Autowired
    public ProxyGeneratorService(MavenStructureService mvn, DockerGeneratorService docker) {
        super(mvn, docker, "proxy");
    }
    
    public void createProxy(FileSystem fs, Domain domain) {
        // create maven project
        Map<String, Path> paths = this.mvn.createEmptyMavenProject(fs, domain.getPath(), "proxy");

        // create pom
        this.createPom(paths, domain.getPath());
        
        // create properties file
        this.createPropertiesFile(paths, domain.getServices());
        
        // create application class file
        this.createApplicationClass(paths, domain.getPath());
        
        // create docker file
        this.docker.createServiceDockerFile(paths);
    }

}
