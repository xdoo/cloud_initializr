package com.catify.initializr.services;

import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Logger;
import org.rythmengine.Rythm;

/**
 *
 * @author claus
 */
public class BaseServiceGeneratorService {

    protected final MavenStructureService mvn;
    protected final DockerGeneratorService docker;
    protected final Util util;

    protected final String pomTemplate;
    protected final String propertiesTemplate;
    protected final String applicationTemplate;

    private static final Logger LOG = Logger.getLogger(BaseServiceGeneratorService.class.getName());

    public BaseServiceGeneratorService(MavenStructureService mvn, DockerGeneratorService docker, Util util, String folder) {
        this.mvn = mvn;
        this.docker = docker;
        this.util = util;

        // import templates
        this.pomTemplate = this.util.readTemplateContent(String.format("templates/%s/pom.tmpl", folder));
        this.propertiesTemplate = this.util.readTemplateContent(String.format("templates/%s/properties.tmpl", folder));
        this.applicationTemplate = this.util.readTemplateContent(String.format("templates/%s/application.tmpl", folder));
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
