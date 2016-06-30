package com.catify.initializr.services;

import com.catify.initializr.domain.Domain;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author claus
 */
@Service
public class ZipGeneratorService {

    private static final Logger LOG = Logger.getLogger(MavenStructureService.class.getName());

    private final DomainGeneratorService domainGen;

    @Autowired
    public ZipGeneratorService(DomainGeneratorService domainGen) {
        this.domainGen = domainGen;
    }

    public byte[] zipFileSystem(Domain domain) {

        byte[] result = null;

        // create zip file system
        Path zip = Paths.get(String.format("%s.zip", UUID.randomUUID().toString()));
        URI uri = URI.create("jar:" + zip.toUri());
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {

            this.domainGen.createDomain(domain, zipfs);

            // finally close file system
            zipfs.close();
            
            // read file as bytes
            result = Files.readAllBytes(zip);
            
            // delete file
            Files.deleteIfExists(zip);

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        return result;
    }

}
