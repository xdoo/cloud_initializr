package com.catify.initializr.services;

import com.catify.initializr.domain.MicroService;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.rythmengine.Rythm;

/**
 *
 * @author claus
 */
public class ProxyGeneratorTemplatesTest {

//    private final File pomTemplate;
    private final File propertiesTemplate;
    private final File applicationTemplate;

    public ProxyGeneratorTemplatesTest() {
        // import templates
        ClassLoader classLoader = getClass().getClassLoader();
        this.propertiesTemplate = new File(classLoader.getResource("templates/proxy/properties.tmpl").getFile());
        this.applicationTemplate = new File(classLoader.getResource("templates/proxy/application.tmpl").getFile());
//        this.pomTemplate = new File(classLoader.getResource("templates/proxy/pom.tmpl").getFile());
    }
    
//    @Test
//    public void testPom() {
//        String result = Rythm.render(this.pomTemplate, "com.example");
//        Assert.assertNotNull(result);
//        System.out.println(result);
//    }

    @Test
    public void testProperties() {
        List<MicroService> services = new ArrayList<>();
        services.add(new MicroService("service-a"));
        services.add(new MicroService("service-b"));
        services.add(new MicroService("service-c"));
        services.add(new MicroService("service-d"));
        String result = Rythm.render(this.propertiesTemplate, services);
        Assert.assertNotNull(result);
        Assert.assertEquals(this.properties, result);
    }

    @Test
    public void testApplicationJava() {
        String result = Rythm.render(this.applicationTemplate, "com.example");
        Assert.assertNotNull(result);
        Assert.assertEquals(this.applicationClass, result);
    }

    private final String properties = "# GLOBAL CONFIGURATION\n"
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
            + "    # routes for service 'servicea'\n"
            + "    servicea:\n"
            + "      path: /**\n"
            + "      serviceId: ServiceA\n"
            + "    # routes for service 'serviceb'\n"
            + "    serviceb:\n"
            + "      path: /**\n"
            + "      serviceId: ServiceB\n"
            + "    # routes for service 'servicec'\n"
            + "    servicec:\n"
            + "      path: /**\n"
            + "      serviceId: ServiceC\n"
            + "    # routes for service 'serviced'\n"
            + "    serviced:\n"
            + "      path: /**\n"
            + "      serviceId: ServiceD\n"
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

    private final String applicationClass = "package com.example.proxy;\n"
            + "\n"
            + "import org.springframework.boot.SpringApplication;\n"
            + "import org.springframework.boot.autoconfigure.SpringBootApplication;\n"
            + "import org.springframework.cloud.netflix.zuul.EnableZuulProxy;\n"
            + "\n"
            + "@SpringBootApplication\n"
            + "@EnableZuulProxy\n"
            + "public class Application {\n"
            + "\n"
            + "	public static void main(String[] args) {\n"
            + "		SpringApplication.run(Application.class, args);\n"
            + "	}\n"
            + "}";
}
