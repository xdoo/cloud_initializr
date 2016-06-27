package com.catify.initializr.services;

import com.catify.initializr.domain.Domain;
import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import org.rythmengine.Rythm;
import org.springframework.stereotype.Service;

/**
 *
 * @author claus
 */
@Service
public class DockerGeneratorService {
    
    private final File composeTemplate;

    public DockerGeneratorService() {
        
        // import templates
        ClassLoader classLoader = getClass().getClassLoader();
        this.composeTemplate = new File(classLoader.getResource("templates/docker/dockercompose.tmpl").getFile());
    }

    public void createServiceDockerFile(Map<String, Path> paths) {
        Path path = paths.get(MavenStructureService.MAIN_DOCKER).resolve("Dockerfile");
        Util.writeToFile(this.dockerTpl, path);
    }
    
    public void createDockerComposeFile(Path path, Domain domain) {
        String compose = Rythm.render(this.composeTemplate, domain.getServices());
        Util.writeToFile(compose, path.resolve("docker-compose.yml"));
    }

    /**
     * nothing to replace - so we can simply use a string.
     */
    private final String dockerTpl = "FROM java:8\n"
            + "\n"
            + "# environment\n"
            + "EXPOSE 8080\n"
            + "\n"
            + "# executable\n"
            + "ADD @project.artifactId@-@project.version@.jar app.jar\n"
            + "\n"
            + "# create group\n"
            + "RUN groupadd service\n"
            + "RUN chgrp service app.jar\n"
            + "\n"
            + "# create user\n"
            + "RUN useradd -ms /bin/bash -G service booter\n"
            + "USER booter\n"
            + "WORKDIR /home/booter\n"
            + "RUN mkdir gen/\n"
            + "\n"
            + "# run app as user 'booter'\n"
            + "RUN bash -c 'touch /app.jar'\n"
            + "ENTRYPOINT [\"java\", \"-Xmx256m\", \"-Xss32m\", \"-Djava.security.egd=file:/dev/./urandom\",\"-jar\",\"/app.jar\"]";

}
