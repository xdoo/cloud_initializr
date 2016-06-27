package com.catify.initializr.services;

import com.catify.initializr.domain.Domain;
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
public class RegistryGeneratorService extends BaseServiceGeneratorService {


    @Autowired
    public RegistryGeneratorService(MavenStructureService mvn, DockerGeneratorService docker) {
        super(mvn, docker, "registry");
    }

    public void createRegistry(Domain domain, FileSystem fs) {
        // create maven project
        Map<String, Path> paths = this.mvn.createEmptyMavenProject(fs, domain.getPath(), "registry");

        // create pom
        this.createPom(paths, domain.getPath());

        // create properties file
        this.createPropertiesFile(paths);

        // create docker file
        this.docker.createServiceDockerFile(paths);

        // create application file
        this.createApplicationClass(paths, domain.getPath());
    }

}
