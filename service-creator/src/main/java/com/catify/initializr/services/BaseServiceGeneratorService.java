package com.catify.initializr.services;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import org.rythmengine.Rythm;

/**
 *
 * @author claus
 */
public class BaseServiceGeneratorService {
    
    protected final MavenStructureService mvn;
    protected final DockerGeneratorService docker;
    
    protected final File pomTemplate;
    protected final File propertiesTemplate;
    protected final File applicationTemplate;

    public BaseServiceGeneratorService(MavenStructureService mvn, DockerGeneratorService docker, String folder) {
        this.mvn = mvn;
        this.docker = docker;
        
        // import templates
        ClassLoader classLoader = getClass().getClassLoader();
	this.pomTemplate = new File(classLoader.getResource(String.format("templates/%s/pom.tmpl", folder)).getFile());
        this.propertiesTemplate = new File(classLoader.getResource(String.format("templates/%s/properties.tmpl", folder)).getFile());
        this.applicationTemplate = new File(classLoader.getResource(String.format("templates/%s/application.tmpl", folder)).getFile());
    }
    
    public void createPom(Map<String, Path> paths, Object... args) {
        Path path = paths.get(MavenStructureService.BASE).resolve("pom.xml");
        String pom = Rythm.render(this.pomTemplate, args);
        Util.writeToFile(pom, path);
    }
    
    public void createPropertiesFile(Map<String, Path> paths, Object... args) {
        Path path = paths.get(MavenStructureService.MAIN_RESOURCES).resolve("application.yml"); 
        String properties = Rythm.render(this.propertiesTemplate, args);
        Util.writeToFile(properties, path);
    }
    
    public void createApplicationClass(Map<String, Path> paths, Object... args) {
        Path path = paths.get(MavenStructureService.MAIN_JAVA).resolve("Application.java");
        String application = Rythm.render(this.applicationTemplate, args);
        Util.writeToFile(application, path);
    }
    
    
}
