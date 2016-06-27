package com.catify.initializr.services;

import com.catify.initializr.domain.Domain;
import com.catify.initializr.domain.MicroService;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author claus
 */
@Service
public class ServiceGeneratorService extends BaseServiceGeneratorService{

    @Autowired
    public ServiceGeneratorService(MavenStructureService mvn, DockerGeneratorService docker) {
        super(mvn, docker, "service");
    }

    public void createService(Domain domain, int index, FileSystem fs) {
        
        // create maven project
        MicroService service = domain.getServices().get(index);
        Map<String, Path> paths = this.mvn.createEmptyMavenProject(fs, (String) domain.getPath(),  service.getName());
        
        // create pom
        this.createPom(paths, domain.getPath(), service);
        
        // create docker file
        this.docker.createServiceDockerFile(paths);
        
        // create properties
        this.createPropertiesFile(paths, service);
        
        // create application java
        this.createApplicationClass(paths, domain.getPath(), service);
    }
    
}
