package com.catify.initializr.services;

import com.catify.initializr.domain.Domain;
import com.catify.initializr.domain.MicroService;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rythmengine.Rythm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author claus
 */
@Service
public class DomainGeneratorService {
    
    private static final Logger LOG = Logger.getLogger(DomainGeneratorService.class.getName());

    private final ServiceGeneratorService serviceGen;
    private final RegistryGeneratorService registryGen;
    private final ProxyGeneratorService proxyGen;
    private final DockerGeneratorService dockerGen;

    protected final String pomTemplate;

    @Autowired
    public DomainGeneratorService(ServiceGeneratorService serviceGen, RegistryGeneratorService registryGen, ProxyGeneratorService proxyGen, DockerGeneratorService dockerGen, Util util) {
        this.serviceGen = serviceGen;
        this.registryGen = registryGen;
        this.proxyGen = proxyGen;
        this.dockerGen = dockerGen;

        // import template
        this.pomTemplate = util.readTemplateContent("templates/parent/pom.tmpl");
    }

    public void createDomain(Domain domain, FileSystem fs) {

        // create domain folder
        Path base = fs.getPath(Util.createServiceName(domain.getName()));
        Util.createDirectory(base);

        // create registry
        this.registryGen.createRegistry(domain, fs);

        // create proxy
        this.proxyGen.createProxy(fs, domain);

        // create services
        List<String> serviceNames = new ArrayList<>();
        List<MicroService> services = domain.getServices();
        services.stream().forEach((service) -> {
            int x = domain.getServices().indexOf(service);
            if (!serviceNames.contains(service.getName())) {
                this.serviceGen.createService(domain, x, fs);
                serviceNames.add(service.getName());
            } else {
                LOG.log(Level.WARNING, "redundant service name: {0}. no service has been created.", service.getName());
            }
        });

        // create docker compose file
        Path docker = base.resolve("/" + Util.createServiceName(domain.getName()) + "/docker");
        Util.createDirectory(docker);
        this.dockerGen.createDockerComposeFile(docker, domain);
        this.dockerGen.createCleanScript(docker, domain);

        // create aggregate pom
        String pom = Rythm.render(this.pomTemplate, Util.createServiceName(domain.getName()), domain.getPath(), domain.getServices());
        Util.writeToFile(pom, base.resolve("pom.xml"));
    }

}
