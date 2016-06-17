package com.catify.initializr.services;

import com.catify.initializr.domain.Domain;
import com.catify.initializr.domain.MicroService;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author claus
 */
@Service
public class ServiceGeneratorService {
     
    private final MavenStructureService mvn;

    @Autowired
    public ServiceGeneratorService(MavenStructureService mvn) {
        this.mvn = mvn;
    }
    
    public void createService(Domain domain, int index, FileSystem fs) {
        
        // create maven project
        MicroService service = domain.getServices().get(index);
        Map<String, Path> paths = this.mvn.createEmptyMavenProject(fs, (String) domain.getPath(),  service.getName());
        
        // create pom
        this.createServicePom(service, domain.getPath(), fs, paths);
        
        // create docker file
        this.createServiceDockerFile(fs, paths);
        
        // create properties
        this.createPropertiesFile(service, fs, paths);
    }
    
    public void createServicePom(MicroService service, String basePath, FileSystem fs, Map<String, Path> paths) {
        Path path = paths.get(MavenStructureService.BASE).resolve("pom.xml");
        String pom = String.format(this.pomTpl,
                basePath,
                service.getName(),
                service.getName());
        try {
            Files.write(path, ImmutableList.of(pom), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Logger.getLogger(ServiceGeneratorService.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    public void createServiceDockerFile(FileSystem fs, Map<String, Path> paths) {
        Path path = paths.get(MavenStructureService.MAIN_DOCKER).resolve("Dockerfile");
        try {
            Files.write(path, ImmutableList.of(this.dockerTpl), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Logger.getLogger(ServiceGeneratorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void createPropertiesFile(com.catify.initializr.domain.MicroService service, FileSystem fs, Map<String, Path> paths) {
        Path path = paths.get(MavenStructureService.MAIN_RESOURCES).resolve("application.yml");
        String props = String.format(this.propertiesTpl, service.getName());
        try {
            Files.write(path, ImmutableList.of(props), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Logger.getLogger(ServiceGeneratorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private final String pomTpl = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                                "    <modelVersion>4.0.0</modelVersion>\n" +
                                "\n" +
                                "    <groupId>%s</groupId>\n" +
                                "    <artifactId>%s</artifactId>\n" +
                                "    <version>0.0.1-SNAPSHOT</version>\n" +
                                "    <packaging>jar</packaging>\n" +
                                "\n" +
                                "    <name>%s</name>\n" +
                                "    <description>generated microservice</description>\n" +
                                "\n" +
                                "    <parent>\n" +
                                "        <groupId>org.springframework.boot</groupId>\n" +
                                "        <artifactId>spring-boot-starter-parent</artifactId>\n" +
                                "        <version>1.3.5.RELEASE</version>\n" +
                                "        <relativePath/> <!-- lookup parent from repository -->\n" +
                                "    </parent>\n" +
                                "\n" +
                                "    <properties>\n" +
                                "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
                                "        <java.version>1.8</java.version>\n" +
                                "    </properties>\n" +
                                "\n" +
                                "    <dependencies>\n" +
                                "        <dependency>\n" +
                                "            <groupId>org.springframework.cloud</groupId>\n" +
                                "            <artifactId>spring-cloud-starter-eureka</artifactId>\n" +
                                "        </dependency>\n" +
                                "        <dependency>\n" +
                                "            <groupId>org.springframework.cloud</groupId>\n" +
                                "            <artifactId>spring-cloud-starter-feign</artifactId>\n" +
                                "        </dependency>\n" +
                                "        <dependency>\n" +
                                "            <groupId>org.springframework.boot</groupId>\n" +
                                "            <artifactId>spring-boot-starter-web</artifactId>\n" +
                                "        </dependency>\n" +
                                "		\n" +
                                "        <dependency>\n" +
                                "            <groupId>org.springframework.boot</groupId>\n" +
                                "            <artifactId>spring-boot-starter-test</artifactId>\n" +
                                "            <scope>test</scope>\n" +
                                "        </dependency>\n" +
                                "    </dependencies>\n" +
                                "	\n" +
                                "    <dependencyManagement>\n" +
                                "        <dependencies>\n" +
                                "            <dependency>\n" +
                                "                <groupId>org.springframework.cloud</groupId>\n" +
                                "                <artifactId>spring-cloud-dependencies</artifactId>\n" +
                                "                <version>Brixton.RELEASE</version>\n" +
                                "                <type>pom</type>\n" +
                                "                <scope>import</scope>\n" +
                                "            </dependency>\n" +
                                "        </dependencies>\n" +
                                "    </dependencyManagement>\n" +
                                "	\n" +
                                "    <build>\n" +
                                "        <plugins>\n" +
                                "            <plugin>\n" +
                                "                <groupId>org.springframework.boot</groupId>\n" +
                                "                <artifactId>spring-boot-maven-plugin</artifactId>\n" +
                                "            </plugin>\n" +
                                "            <plugin>\n" +
                                "                <artifactId>maven-resources-plugin</artifactId>\n" +
                                "                <executions>\n" +
                                "                    <execution>\n" +
                                "                        <id>copy-resources</id>\n" +
                                "                        <phase>validate</phase>\n" +
                                "                        <goals>\n" +
                                "                            <goal>copy-resources</goal>\n" +
                                "                        </goals>\n" +
                                "                        <configuration>\n" +
                                "                            <outputDirectory>${basedir}/target/</outputDirectory>\n" +
                                "                            <resources>\n" +
                                "                                <resource>\n" +
                                "                                    <directory>src/main/docker</directory>\n" +
                                "                                    <filtering>true</filtering>\n" +
                                "                                </resource>\n" +
                                "                            </resources>\n" +
                                "                        </configuration>\n" +
                                "                    </execution>\n" +
                                "                </executions>\n" +
                                "            </plugin>\n" +
                                "        </plugins>\n" +
                                "    </build>\n" +
                                "	\n" +
                                "\n" +
                                "</project>";
    
    private final String dockerTpl =  "FROM java:8\n" +
                                "\n" +
                                "# environment\n" +
                                "EXPOSE 8080\n" +
                                "\n" +
                                "# executable\n" +
                                "ADD @project.artifactId@-@project.version@.jar app.jar\n" +
                                "\n" +
                                "# create group\n" +
                                "RUN groupadd service\n" +
                                "RUN chgrp service app.jar\n" +
                                "\n" +
                                "# create user\n" +
                                "RUN useradd -ms /bin/bash -G service booter\n" +
                                "USER booter\n" +
                                "WORKDIR /home/booter\n" +
                                "RUN mkdir gen/\n" +
                                "\n" +
                                "# run app as user 'booter'\n" +
                                "RUN bash -c 'touch /app.jar'\n" +
                                "ENTRYPOINT [\"java\", \"-Xmx256m\", \"-Xss32m\", \"-Djava.security.egd=file:/dev/./urandom\",\"-jar\",\"/app.jar\"]"; 
    
    private final String propertiesTpl =    "# GLOBAL CONFIGURATION\n" +
                                            "spring:\n" +
                                            "    # if you want to set a different profile\n" +
                                            "    # use environment variable:\n" +
                                            "    #\n" +
                                            "    # SPRING_PROFILES_ACTIVE=docker\n" +
                                            "    profiles.active: local\n" +
                                            "    application.name: %s\n" +
                                            "        \n" +
                                            "\n" +
                                            "eureka.instance:\n" +
                                            "    # send ip addresses to the discovery. this is\n" +
                                            "    # important for client side load balancing\n" +
                                            "    preferIpAddress: true\n" +
                                            "    # push this service all 10 seconds to the registry\n" +
                                            "    leaseRenewalIntervalInSeconds: 10\n" +
                                            "    metadataMap.instanceId: ${vcap.application.instance_id:${spring.application.name}:${spring.application.instance_id:${server.port}}}\n" +
                                            "\n" +
                                            "---\n" +
                                            "# DOCKER CONFIGURATION\n" +
                                            "spring:\n" +
                                            "    profiles: docker\n" +
                                            "\n" +
                                            "# inside a container, we can always map to 8080. Use your compose\n" +
                                            "# file to expose a service on a different port.\n" +
                                            "server.port: 8080\n" +
                                            "\n" +
                                            "# the hostname of your eureka server should be 'discovery'\n" +
                                            "eureka.client.serviceUrl.defaultZone: http://discovery:8761/eureka/\n" +
                                            "\n" +
                                            "---\n" +
                                            "# LOCAL CONFIGURATION\n" +
                                            "spring:\n" +
                                            "    profiles: local\n" +
                                            "    \n" +
                                            "# random server port to avoid port \n" +
                                            "# conflicts on localhost\n" +
                                            "server.port: 0\n" +
                                            "\n" +
                                            "eureka.client:\n" +
                                            "    # in local mode you have to run your service \n" +
                                            "    # discovery on localhost (you also can run the \n" +
                                            "    # cloud infrastructure on your local docker host)\n" +
                                            "    serviceUrl.defaultZone: http://localhost:8761/eureka/\n" +
                                            "    # fetch all 5 seconds the delta from the service registry\n" +
                                            "    registry-fetch-interval-seconds: 5";
    
}
