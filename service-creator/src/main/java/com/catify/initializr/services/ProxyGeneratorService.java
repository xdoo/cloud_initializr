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
        
        // create docker file
        this.docker.createServiceDockerFile(paths);
    }

    public void createPom(Map<String, Path> paths, String groupId) {
        Path path = paths.get(MavenStructureService.BASE).resolve("pom.xml");
        String pom = Rythm.render(this.pomTemplate, groupId);
        Util.writeToFile(pom, path);
    }
    
    public void createPropertiesFile(Map<String, Path> paths, Domain domain) {
        Path path = paths.get(MavenStructureService.MAIN_RESOURCES).resolve("application.yml");
        
        
    }

    private final String pomTpl = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            + "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n"
            + "    <modelVersion>4.0.0</modelVersion>\n"
            + "\n"
            + "    <groupId>%s</groupId>\n"
            + "    <artifactId>service-proxy</artifactId>\n"
            + "    <version>0.0.1-SNAPSHOT</version>\n"
            + "    <packaging>jar</packaging>\n"
            + "\n"
            + "    <name>service-proxy</name>\n"
            + "    <description>generated service proxy</description>\n"
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
            + "            <artifactId>spring-cloud-starter-eureka</artifactId>\n"
            + "        </dependency>\n"
            + "        <dependency>\n"
            + "            <groupId>org.springframework.cloud</groupId>\n"
            + "            <artifactId>spring-cloud-starter-zuul</artifactId>\n"
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
            + "    application.name: Zuul\n"
            + "\n"
            + "# zuul proxy always exposes 8080 \n"
            + "server.port: 8080\n"
            + "\n"
            + "# zuul routes\n"
            + "zuul:\n"
            + "  ignoredServices: '*'\n"
            + "  routes:\n"
            + "    servicea:\n"
            + "      path: /**\n"
            + "      serviceId: DemoServiceA\n"
            + "\n"
            + "---\n"
            + "# LOCAL CONFIGURATION\n"
            + "spring:\n"
            + "    profiles: local\n"
            + "      \n"
            + "      \n"
            + "eureka.client:\n"
            + "    # in local mode you have to run your service \n"
            + "    # discovery on localhost (you also can run the \n"
            + "    # cloud infrastructure on your local docker host)\n"
            + "    serviceUrl.defaultZone: http://localhost:8761/eureka/\n"
            + "    # fetch all 5 seconds the delta from the service registry\n"
            + "    registry-fetch-interval-seconds: 5\n"
            + "      \n"
            + "---\n"
            + "# DOCKER CONFIGURATION\n"
            + "spring:\n"
            + "    profiles: docker\n"
            + "    \n"
            + "\n"
            + "eureka.client:\n"
            + "    # the hostname of your eureka server should be 'discovery'\n"
            + "    serviceUrl.defaultZone: http://discovery:8761/eureka/\n"
            + "    # fetch all 5 seconds the delta from the service registry\n"
            + "    registry-fetch-interval-seconds: 5";

}
