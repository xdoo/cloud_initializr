package com.catify.initializr.services;

import com.catify.initializr.domain.Domain;
import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Map;
import org.rythmengine.Rythm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Create Maven project for proxy service.
 * 
 * @author claus
 */
@Service
public class ProxyGeneratorService {

    private final MavenStructureService mvn;
    private final DockerGeneratorService docker;
    
    private final File pomTemplate;
    private final File propertiesTemplate;
    private final File applicationTemplate;

    @Autowired
    public ProxyGeneratorService(MavenStructureService mvn, DockerGeneratorService docker) {
        this.mvn = mvn;
        this.docker = docker;
        
        // import templates
        ClassLoader classLoader = getClass().getClassLoader();
	this.pomTemplate = new File(classLoader.getResource("templates/proxy/pom.tmpl").getFile());
        this.propertiesTemplate = new File(classLoader.getResource("templates/proxy/properties.tmpl").getFile());
        this.applicationTemplate = new File(classLoader.getResource("templates/proxy/application.tmpl").getFile());
    }
    
    public void createProxy(FileSystem fs, Domain domain) {
        // create maven project
        Map<String, Path> paths = this.mvn.createEmptyMavenProject(fs, domain.getPath(), "proxy");

        // create pom
        this.createPom(paths, domain.getPath());
        
        // create properties file
        this.createPropertiesFile(paths, domain);
        
        // create application class file
        this.createApplicationClass(paths, domain);
        
        // create docker file
        this.docker.createServiceDockerFile(paths);
    }

    /**
     * create pom.xml for proxy
     * 
     * @param paths
     * @param groupId 
     */
    public void createPom(Map<String, Path> paths, String groupId) {
        Path path = paths.get(MavenStructureService.BASE).resolve("pom.xml");
        String pom = Rythm.render(this.pomTemplate, groupId);
        Util.writeToFile(pom, path);
    }
    
    /**
     * create application.yml for proxy
     * 
     * @param paths
     * @param domain 
     */
    public void createPropertiesFile(Map<String, Path> paths, Domain domain) {
        Path path = paths.get(MavenStructureService.MAIN_RESOURCES).resolve("application.yml"); 
        String properties = Rythm.render(this.propertiesTemplate, domain.getServices());
        Util.writeToFile(properties, path);
    }
    
    /**
     * create Application.java for proxy
     * 
     * @param paths
     * @param domain 
     */
    public void createApplicationClass(Map<String, Path> paths, Domain domain) {
        Path path = paths.get(MavenStructureService.MAIN_JAVA).resolve("Application.java");
        String application = Rythm.render(this.applicationTemplate, domain.getPath());
        Util.writeToFile(application, path);
    }

}
