package com.catify.initializr.services;

import com.catify.initializr.domain.Domain;
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
public class RegistryGeneratorService {

    private final MavenStructureService mvn;
    private final DockerGeneratorService docker;

    @Autowired
    public RegistryGeneratorService(MavenStructureService mvn, DockerGeneratorService docker) {
        this.mvn = mvn;
        this.docker = docker;
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
        this.createApplicationFile(paths, domain.getPath());
    }

    public void createPom(Map<String, Path> paths, String groupId) {
        Path path = paths.get(MavenStructureService.BASE).resolve("pom.xml");
        String pom = String.format(this.pomTpl, groupId);
        Util.writeToFile(pom, path);
    }

    public void createPropertiesFile(Map<String, Path> paths) {
        Path path = paths.get(MavenStructureService.MAIN_RESOURCES).resolve("application.yml");
        Util.writeToFile(this.propertiesTpl, path);
    }
    
    public void createApplicationFile(Map<String, Path> paths, String groupId) {
        Path path = paths.get(MavenStructureService.MAIN_JAVA).resolve("Application.java");
        String app = String.format(this.applicationTpl, groupId);
        Util.writeToFile(app, path);
    }

    private final String pomTpl = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            + "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n"
            + "    <modelVersion>4.0.0</modelVersion>\n"
            + "\n"
            + "    <groupId>%s</groupId>\n"
            + "    <artifactId>service-registry</artifactId>\n"
            + "    <version>0.0.1-SNAPSHOT</version>\n"
            + "    <packaging>jar</packaging>\n"
            + "\n"
            + "    <name>service-registry</name>\n"
            + "    <description>generated service registry</description>\n"
            + "\n"
            + "    <parent>\n"
            + "        <groupId>org.springframework.boot</groupId>\n"
            + "        <artifactId>spring-boot-starter-parent</artifactId>\n"
            + "        <version>1.3.5.RELEASE</version>\n"
            + "        <relativePath/> <!-- lookup parent from repository -->\n"
            + "    </parent>\n"
            + "\n"
            + "    <properties>\n"
            + "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n"
            + "        <java.version>1.8</java.version>\n"
            + "    </properties>\n"
            + "\n"
            + "    <dependencies>\n"
            + "        <dependency>\n"
            + "            <groupId>org.springframework.cloud</groupId>\n"
            + "            <artifactId>spring-cloud-starter-eureka-server</artifactId>\n"
            + "        </dependency>\n"
            + "		\n"
            + "        <dependency>\n"
            + "            <groupId>org.springframework.boot</groupId>\n"
            + "            <artifactId>spring-boot-starter-test</artifactId>\n"
            + "            <scope>test</scope>\n"
            + "        </dependency>\n"
            + "    </dependencies>\n"
            + "	\n"
            + "    <dependencyManagement>\n"
            + "        <dependencies>\n"
            + "            <dependency>\n"
            + "                <groupId>org.springframework.cloud</groupId>\n"
            + "                <artifactId>spring-cloud-dependencies</artifactId>\n"
            + "                <version>Brixton.RELEASE</version>\n"
            + "                <type>pom</type>\n"
            + "                <scope>import</scope>\n"
            + "            </dependency>\n"
            + "        </dependencies>\n"
            + "    </dependencyManagement>\n"
            + "	\n"
            + "    <build>\n"
            + "        <plugins>\n"
            + "            <plugin>\n"
            + "                <groupId>org.springframework.boot</groupId>\n"
            + "                <artifactId>spring-boot-maven-plugin</artifactId>\n"
            + "            </plugin>\n"
            + "            <plugin>\n"
            + "                <artifactId>maven-resources-plugin</artifactId>\n"
            + "                <executions>\n"
            + "                    <execution>\n"
            + "                        <id>copy-resources</id>\n"
            + "                        <phase>validate</phase>\n"
            + "                        <goals>\n"
            + "                            <goal>copy-resources</goal>\n"
            + "                        </goals>\n"
            + "                        <configuration>\n"
            + "                            <outputDirectory>${basedir}/target/</outputDirectory>\n"
            + "                            <resources>\n"
            + "                                <resource>\n"
            + "                                    <directory>src/main/docker</directory>\n"
            + "                                    <filtering>true</filtering>\n"
            + "                                </resource>\n"
            + "                            </resources>\n"
            + "                        </configuration>\n"
            + "                    </execution>\n"
            + "                </executions>\n"
            + "            </plugin>\n"
            + "        </plugins>\n"
            + "    </build>\n"
            + "</project>";

    private final String propertiesTpl = "# GLOBAL CONFIGURATION\n"
            + "spring:\n"
            + "    # if you want to set a different profile\n"
            + "    # use environment variable:\n"
            + "    #\n"
            + "    # SPRING_PROFILES_ACTIVE=docker\n"
            + "    profiles.active: local\n"
            + "    cloud.config.discovery.enabled: true\n"
            + "    application.name: discovery\n"
            + "\n"
            + "# don't register with your self\n"
            + "eureka.client:\n"
            + "    registerWithEureka: false\n"
            + "    fetchRegistry: false\n"
            + "\n"
            + "  \n"
            + "---\n"
            + "# LOCAL CONFIGURATION\n"
            + "spring:\n"
            + "  profiles: local\n"
            + "eureka:\n"
            + "  instance:\n"
            + "    hostname: localhost\n"
            + "endpoints:\n"
            + "  health:\n"
            + "    sensitive: false\n"
            + "\n"
            + "# use always the standard server port\n"
            + "server.port: 8761\n"
            + "\n"
            + "---\n"
            + "# DOCKER CONFIGURATION\n"
            + "spring:\n"
            + "  profiles: docker\n"
            + "eureka:\n"
            + "  instance:\n"
            + "    hostname: discovery\n"
            + "    \n"
            + "# server port will be mapped with docker\n"
            + "server.port: 8080";

    private final String applicationTpl = "package %s.registry;\n"
            + "\n"
            + "import org.springframework.boot.SpringApplication;\n"
            + "import org.springframework.boot.autoconfigure.SpringBootApplication;\n"
            + "import org.springframework.cloud.netflix.zuul.EnableZuulProxy;\n"
            + "\n"
            + "@SpringBootApplication\n"
            + "@EnableZuulProxy\n"
            + "public class DemoProxyApplication {\n"
            + "\n"
            + "	public static void main(String[] args) {\n"
            + "		SpringApplication.run(DemoProxyApplication.class, args);\n"
            + "	}\n"
            + "}";

}
