package com.catify.initializr.services;

import com.catify.initializr.domain.Domain;
import com.catify.initializr.domain.MicroService;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author claus
 */
@Service
public class DomainGeneratorService {
    
    private final ServiceGeneratorService serviceGen;
    private final RegistryGeneratorService registryGen;
    private final ProxyGeneratorService proxyGen;
    private final DockerGeneratorService dockerGen;

    @Autowired
    public DomainGeneratorService(ServiceGeneratorService serviceGen, RegistryGeneratorService registryGen, ProxyGeneratorService proxyGen, DockerGeneratorService dockerGen) {
        this.serviceGen = serviceGen;
        this.registryGen = registryGen;
        this.proxyGen = proxyGen;
        this.dockerGen = dockerGen;
    }
 
    public void createDomain(Domain domain, FileSystem fs) {
        
        // create registry
        this.registryGen.createRegistry(domain, fs);
        
        // create proxy
        this.proxyGen.createProxy(fs, domain);
        
        // create services
        List<MicroService> services = domain.getServices();
        services.stream().forEach((service) -> {
            int x = domain.getServices().indexOf(service);
            this.serviceGen.createService(domain, x, fs);
        });
        
        // create docker compose file
        Path docker = fs.getPath("/docker");
        Util.createDirectory(docker);
        this.dockerGen.createDockerComposeFile(docker, domain);
    }
    
    
}
