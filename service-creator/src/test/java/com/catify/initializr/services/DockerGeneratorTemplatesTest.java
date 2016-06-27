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
public class DockerGeneratorTemplatesTest {

    private final File composeTmpl;

    public DockerGeneratorTemplatesTest() {
        // import templates
        ClassLoader classLoader = getClass().getClassLoader();
        this.composeTmpl = new File(classLoader.getResource("templates/docker/dockercompose.tmpl").getFile());
    }

    @Test
    public void testComposeTemplate() {
        List<MicroService> services = new ArrayList<>();
        services.add(new MicroService("service-a"));
        services.add(new MicroService("service-b"));
        services.add(new MicroService("service-c"));
        services.add(new MicroService("service-d"));
        String result = Rythm.render(this.composeTmpl, services);
        Assert.assertNotNull(result);
        Assert.assertEquals(this.compose, result);
    }

    private final String compose = "# --------------\n"
            + "# registry\n"
            + "# --------------\n"
            + "service-registry:\n"
            + "    hostname: discovery\n"
            + "    build: ../demo-registry/target\n"
            + "    ports:\n"
            + "        - \"8761:8080\"\n"
            + "    environment:\n"
            + "        - SPRING_PROFILES_ACTIVE=docker\n"
            + "        \n"
            + "# --------------\n"
            + "# zuul proxy\n"
            + "# --------------\n"
            + "zuul-proxy:\n"
            + "    hostname: zuul\n"
            + "    build: ../demo-proxy/target\n"
            + "    ports:\n"
            + "        - \"8080:8080\"\n"
            + "    links:\n"
            + "        - service-registry\n"
            + "    environment:\n"
            + "        - SPRING_PROFILES_ACTIVE=docker\n"
            + "\n"
            + "# --------------\n"
            + "# service-a\n"
            + "# --------------\n"
            + "servicea:\n"
            + "    build: ../servicea/target\n"
            + "    ports:\n"
            + "        - \"8080\"\n"
            + "    links:\n"
            + "        - service-registry\n"
            + "    environment:\n"
            + "        - SPRING_PROFILES_ACTIVE=docker\n"
            + "\n"
            + "# --------------\n"
            + "# service-b\n"
            + "# --------------\n"
            + "serviceb:\n"
            + "    build: ../serviceb/target\n"
            + "    ports:\n"
            + "        - \"8080\"\n"
            + "    links:\n"
            + "        - service-registry\n"
            + "    environment:\n"
            + "        - SPRING_PROFILES_ACTIVE=docker\n"
            + "\n"
            + "# --------------\n"
            + "# service-c\n"
            + "# --------------\n"
            + "servicec:\n"
            + "    build: ../servicec/target\n"
            + "    ports:\n"
            + "        - \"8080\"\n"
            + "    links:\n"
            + "        - service-registry\n"
            + "    environment:\n"
            + "        - SPRING_PROFILES_ACTIVE=docker\n"
            + "\n"
            + "# --------------\n"
            + "# service-d\n"
            + "# --------------\n"
            + "serviced:\n"
            + "    build: ../serviced/target\n"
            + "    ports:\n"
            + "        - \"8080\"\n"
            + "    links:\n"
            + "        - service-registry\n"
            + "    environment:\n"
            + "        - SPRING_PROFILES_ACTIVE=docker\n"
            + "\n";

}
