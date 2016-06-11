package com.catify.initializr.services;

import com.catify.initializr.domain.Domain;
import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Jimfs;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author claus
 */
@Service
public class GeneratorService {
     
    private final Configuration cfg;
    private final MavenStructureService mvn;
    
    public final static String DOMAIN_NAME = "domainName";
    public final static String DOMAIN_PATH = "domainPath";
    public final static String DOMAIN_SERVICE = "sevice";

    @Autowired
    public GeneratorService(Configuration cfg, MavenStructureService mvn) {
        this.cfg = cfg;
        this.mvn = mvn;
    }
    
    public FileSystem createDomain(Domain domain) {
        Map<String, Object> model = new ConcurrentHashMap<>();
        FileSystem fs = Jimfs.newFileSystem(com.google.common.jimfs.Configuration.unix());
        
        model.put(DOMAIN_NAME, domain.getName());
        model.put(DOMAIN_PATH, domain.getBasePath());
        
        domain.getServices().stream().map((service) -> {
            model.put(DOMAIN_SERVICE, service);
            return service;
        }).forEach((_item) -> {
            this.createService(model, fs);
        });
        return fs;
    }
    
    public void createService(Map<String, Object> model, FileSystem fs) {
        
        // create maven project
        com.catify.initializr.domain.Service service = (com.catify.initializr.domain.Service) model.get(DOMAIN_SERVICE);
        Map<String, Path> paths = this.mvn.createEmptyMavenProject(fs, (String) model.get(DOMAIN_PATH),  service.getName());
        
        // create pom
        this.createServicePom(model, fs, paths);
    }
    
    public void createServicePom(Map<String, Object> model, FileSystem fs, Map<String, Path> paths) {
        try {
            Path path = paths.get(MavenStructureService.BASE).resolve("pom.xml");
            StringWriter out = new StringWriter();
            Template template = this.cfg.getTemplate("templates/service_pom.ftl");
            template.process(model, out);
            Files.write(path, ImmutableList.of(out.toString()), StandardCharsets.UTF_8);
            out.close();
        } catch (MalformedTemplateNameException ex) {
            Logger.getLogger(GeneratorService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(GeneratorService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | TemplateException ex) {
            Logger.getLogger(GeneratorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
